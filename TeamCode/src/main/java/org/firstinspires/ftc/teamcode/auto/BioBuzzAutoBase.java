package org.firstinspires.ftc.teamcode.auto;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.utils.Angle;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Catapult;
import org.firstinspires.ftc.teamcode.subsystems.FlowerArms;

import java.util.function.BooleanSupplier;

/**
 * Base class for every BioBuzz autonomous. A new scenario only has to extend this class, add an
 * {@code @Autonomous} annotation and write its steps in {@link #runRoutine()}, e.g.
 *
 * <pre>
 *   step("Launch preload");
 *   launchCatapult();
 *   forward(24);
 *   strafeLeft(24);
 *   turnRight(90);
 * </pre>
 *
 * Every step BLOCKS until it is finished, but while it waits the base class keeps
 * calling Pedro Pathing's {@code follower.update()} (so the robot drives / holds its position) and
 * {@code robot.update()} (so the catapult launch sequence and hold power keep running).
 *
 * Movement is ROBOT-RELATIVE: forward(24) means "24 inches the way the robot is facing right now".
 * Moves are chained from the PLANNED end of the previous move (not the measured position), so small
 * errors do not add up over the routine. Pedro coordinates: +x forward, +y left, heading CCW-positive.
 *
 * Alliance mirroring: override {@link #mirrored()} to return true and every left/right strafe and
 * turn is swapped, so the same routine can run from the opposite side of the field.
 *
 * Rules this class enforces (BIOBUZZ Competition Manual):
 *   G304.H - nothing moves during INIT (Robot and Follower are only created, never powered).
 *   G403   - no powered movement after AUTO: all steps are skipped and everything is stopped at
 *            {@link #SAFETY_STOP_SECONDS}, before the 30 s AUTO period ends.
 */
public abstract class BioBuzzAutoBase extends LinearOpMode {

    /** Stop everything this long after START, a little before the 30 s AUTO period ends. */
    public static double SAFETY_STOP_SECONDS = 29.5;

    /** Give up on a single drive move after this long and continue with the next step. */
    public static double MOVE_TIMEOUT_SECONDS = 4.0;

    /** Give up on a followPath() step after this long (curved paths are longer than single moves). */
    public static double PATH_TIMEOUT_SECONDS = 8.0;

    /** An in-place turn is done when heading is within this many degrees and the robot has stopped rotating. */
    public static double TURN_TOLERANCE_DEGREES = 2.0;
    public static double TURN_SETTLED_RAD_PER_SEC = 0.25;
    public static double TURN_TIMEOUT_SECONDS = 2.0;

    /** Use as the speed of a move to run it at the follower's full speed. */
    public static final double FULL_SPEED = Double.POSITIVE_INFINITY;

    protected Robot robot;
    protected Follower follower;

    private final ElapsedTime autoTimer = new ElapsedTime();
    private Pose target;
    private String stepName = "INIT";
    private boolean timeUp = false;

    // ---------------------------------------------------------------------------------------------
    // Override these in each scenario
    // ---------------------------------------------------------------------------------------------

    /** The steps of the autonomous. Runs once after START. */
    protected abstract void runRoutine();

    /**
     * Where the robot starts. Relative scenarios can leave this at (0, 0, 0). Override with a field
     * coordinate (inches, radians) when a scenario uses {@link #driveTo(Pose, double)}.
     */
    protected Pose startPose() {
        return new Pose(0, 0, 0);
    }

    /** Return true to swap every left/right strafe and turn (run the routine from the other side). */
    protected boolean mirrored() {
        return false;
    }

    // ---------------------------------------------------------------------------------------------
    // OpMode lifecycle
    // ---------------------------------------------------------------------------------------------

    @Override
    public final void runOpMode() {
        robot = new Robot(hardwareMap);
        follower = Constants.create(hardwareMap);
        target = startPose();
        follower.setPose(target);

        // INIT: show odometry so the drive team can check the pods before the match. Nothing moves.
        while (opModeInInit()) {
            follower.update();
            telemetry.addData("Ready", "%s%s", getClass().getSimpleName(), mirrored() ? " (MIRRORED)" : "");
            telemetry.addLine("Place the robot at the start position, then press START.");
            telemetry.addData("Pose", formatPose(follower.pose()));
            telemetry.update();
        }
        if (isStopRequested()) {
            return;
        }

        autoTimer.reset();
        follower.setPose(target);       // Zero out any drift from the robot being placed during INIT.
        follower.hold(target);          // Hold position while the first mechanism steps run.

        // Power the flower arms to UP right away (same as TeleOp), so later stages move from a known position.
        robot.flowerArms.setStage(FlowerArms.Stage.UP);

        runRoutine();

        // Done (or out of time): stop everything so nothing is powered into the TeleOp transition.
        stepName = timeUp ? "TIME UP - stopped" : "DONE";
        robot.stop();
        follower.stop();
        while (opModeIsActive()) {
            telemetry.addData("Step", stepName);
            telemetry.addData("Finished at", "%.1f s", autoTimer.seconds());
            telemetry.addData("Pose", formatPose(follower.pose()));
            telemetry.update();
            idle();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Building blocks for routines
    // ---------------------------------------------------------------------------------------------

    /** Names the current step (shown on the Driver Station). */
    protected void step(String name) {
        stepName = name;
    }

    /** True while the routine should keep going (OpMode running and still inside the AUTO period). */
    protected boolean running() {
        return opModeIsActive() && !timeUp;
    }

    /** Drives forward the given inches at full speed. */
    protected void forward(double inches) {
        move(inches, 0, FULL_SPEED);
    }

    /** Drives backward the given inches at full speed. */
    protected void back(double inches) {
        move(-inches, 0, FULL_SPEED);
    }

    /** Strafes left the given inches at full speed (right when mirrored). */
    protected void strafeLeft(double inches) {
        move(0, inches, FULL_SPEED);
    }

    /** Strafes right the given inches at full speed (left when mirrored). */
    protected void strafeRight(double inches) {
        move(0, -inches, FULL_SPEED);
    }

    /**
     * Robot-relative move without turning.
     * @param forwardInches + forward / - back
     * @param leftInches    + left / - right (swapped when mirrored)
     * @param maxSpeed      top speed in inches/second, or {@link #FULL_SPEED}
     */
    protected void move(double forwardInches, double leftInches, double maxSpeed) {
        if (mirrored()) {
            leftInches = -leftInches;
        }
        double h = target.heading();
        double x = target.x() + forwardInches * Math.cos(h) - leftInches * Math.sin(h);
        double y = target.y() + forwardInches * Math.sin(h) + leftInches * Math.cos(h);
        driveTo(new Pose(x, y, h), maxSpeed);
    }

    /**
     * Drives in a straight line to a pose in field coordinates (inches, radians), turning to the
     * pose's heading along the way. NOT mirrored - mirror the pose yourself if needed.
     */
    protected void driveTo(Pose end, double maxSpeed) {
        if (!running()) {
            return;
        }
        Path path = line(target, end).linear(target.heading(), end.heading());
        if (!Double.isInfinite(maxSpeed)) {
            path = path.with(Constants.foresightConfig.maxPathSpeed.at(maxSpeed));
        }
        follower.follow(path);

        ElapsedTime timer = new ElapsedTime();
        while (running() && follower.isBusy() && timer.seconds() < MOVE_TIMEOUT_SECONDS) {
            tick();
        }
        // On a timeout keep pushing toward the planned end while the next step runs.
        if (follower.following()) {
            follower.hold(end);
        }
        target = end;
    }

    /** Follows a Pedro path (e.g. from the Visualizer) at full speed. Paths are used as-is: NOT mirrored. */
    protected void followPath(Path path) {
        followPath(path, FULL_SPEED);
    }

    /**
     * Follows a Pedro path, capped at maxSpeed inches/second ({@link #FULL_SPEED} for no cap).
     * The path should start where the previous step ended; the next relative move starts from its end pose.
     */
    protected void followPath(Path path, double maxSpeed) {
        if (!running()) {
            return;
        }
        if (!Double.isInfinite(maxSpeed)) {
            path = path.with(Constants.foresightConfig.maxPathSpeed.at(maxSpeed));
        }
        Pose end = path.endPose();
        follower.follow(path);

        ElapsedTime timer = new ElapsedTime();
        while (running() && follower.isBusy() && timer.seconds() < PATH_TIMEOUT_SECONDS) {
            tick();
        }
        // On a timeout keep pushing toward the end of the path while the next step runs.
        if (follower.following()) {
            follower.hold(end);
        }
        target = end;
    }

    /** Turns in place counter-clockwise (clockwise when mirrored). */
    protected void turnLeft(double degrees) {
        turn(degrees);
    }

    /** Turns in place clockwise (counter-clockwise when mirrored). */
    protected void turnRight(double degrees) {
        turn(-degrees);
    }

    private void turn(double degreesCcw) {
        if (!running()) {
            return;
        }
        if (mirrored()) {
            degreesCcw = -degreesCcw;
        }
        Pose end = target.withHeading(Angle.normalize(target.heading() + Math.toRadians(degreesCcw)));
        follower.hold(end);

        ElapsedTime timer = new ElapsedTime();
        while (running() && timer.seconds() < TURN_TIMEOUT_SECONDS) {
            tick();
            double error = Angle.normalizeSigned(end.heading() - follower.pose().heading());
            boolean onHeading = Math.abs(Math.toDegrees(error)) < TURN_TOLERANCE_DEGREES;
            boolean stopped = Math.abs(follower.velocity().omega) < TURN_SETTLED_RAD_PER_SEC;
            if (onHeading && stopped) {
                break;
            }
        }
        target = end;
    }

    /** Launches the catapult and waits until the arm has been pulled back down. */
    protected void launchCatapult() {
        if (!running()) {
            return;
        }
        robot.catapult.launch();
        waitUntil(() -> !robot.catapult.isBusy(),
                Catapult.LAUNCH_SECONDS + Catapult.PULL_DOWN_SECONDS + 1.0);
    }

    /** Waits (while holding position and running mechanisms). */
    protected void waitSeconds(double seconds) {
        ElapsedTime timer = new ElapsedTime();
        while (running() && timer.seconds() < seconds) {
            tick();
        }
    }

    /** Waits until the condition is true or the timeout passes. Returns true if the condition was met. */
    protected boolean waitUntil(BooleanSupplier condition, double timeoutSeconds) {
        ElapsedTime timer = new ElapsedTime();
        while (running() && timer.seconds() < timeoutSeconds) {
            if (condition.getAsBoolean()) {
                return true;
            }
            tick();
        }
        return condition.getAsBoolean();
    }

    /** Seconds since START. */
    protected double autoSeconds() {
        return autoTimer.seconds();
    }

    /** One control loop: drive, mechanisms, safety timer, telemetry. Every waiting loop calls this. */
    protected void tick() {
        follower.update();
        robot.update();

        if (autoTimer.seconds() > SAFETY_STOP_SECONDS) {
            timeUp = true;
        }

        telemetry.addData("Step", stepName);
        telemetry.addData("Time", "%.1f s%s", autoTimer.seconds(), mirrored() ? "  (MIRRORED)" : "");
        telemetry.addData("Pose", formatPose(follower.pose()));
        telemetry.addData("Target", formatPose(target));
        robot.addTelemetry(telemetry);
        telemetry.update();
    }

    private static String formatPose(Pose pose) {
        return String.format("x=%.1f y=%.1f h=%.0f deg", pose.x(), pose.y(), Math.toDegrees(pose.heading()));
    }
}
