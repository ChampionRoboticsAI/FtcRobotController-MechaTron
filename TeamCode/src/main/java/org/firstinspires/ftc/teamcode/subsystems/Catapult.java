package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * The catapult: two geared-together motors (Expansion Hub ports 1 and 2, "Catapult Right" / "Catapult Left")
 * pulling against a rubber band.
 *
 * Two ways to drive it:
 *  - TeleOp: {@link #manual(boolean, boolean)} every loop with the launch / down buttons.
 *  - Autonomous: {@link #launch()} starts a timed LAUNCH -> PULL DOWN -> HOLD sequence that runs in the
 *    background while {@link #update()} is called every loop. {@link #isBusy()} is true until the arm is back down.
 *
 * FIRST TEST: lift the robot. If one motor is reversed wrongly they will fight each other. Tap launch briefly and
 * confirm both motors turn the arm the same way before holding any button down.
 */
public class Catapult {

    public enum Mode { IDLE, UP, DOWN, HOLD }

    // Catapult powers (from Everybot code). Positive power drives the arm DOWN.
    public static final double UP_POWER   = -1.0;
    public static final double DOWN_POWER =  1.0;
    // Pulls against the rubber band to keep the arm down after a launch. Raise if the band
    // lifts the arm back up; lower if the motors buzz or get hot while holding.
    public static final double HOLD_POWER =  0.2;

    // Timed auto launch. TODO: tune on the robot.
    // LAUNCH_SECONDS: how long full launch power is applied - long enough for the arm to reach the top.
    // PULL_DOWN_SECONDS: how long full down power is applied - long enough for the arm to come all the way down.
    public static double LAUNCH_SECONDS    = 0.6;
    public static double PULL_DOWN_SECONDS = 0.8;

    // TeleOp auto return: after the launch button is released, wait this long, then pull the arm
    // down for PULL_DOWN_SECONDS automatically (no need to press the down button). TODO: tune on the robot.
    public static double AUTO_DOWN_DELAY_SECONDS = 2.0;

    private final DcMotor left;
    private final DcMotor right;
    private final ElapsedTime sequenceTimer = new ElapsedTime();
    private final ElapsedTime autoDownTimer = new ElapsedTime();

    private Mode mode = Mode.IDLE;
    private boolean sequenceRunning = false;
    private boolean used = false;   // No hold power until the arm has been moved once.
    private boolean autoDownPending = false;
    private boolean lastLaunchButton = false;

    public Catapult(HardwareMap hardwareMap) {
        // The two catapult motors face each other, so one is reversed (as in the Everybot code).
        // With these directions, positive power must move the arm DOWN on BOTH motors.
        left = hardwareMap.get(DcMotor.class, "Catapult Left");
        right = hardwareMap.get(DcMotor.class, "Catapult Right");
        left.setDirection(DcMotor.Direction.FORWARD);
        right.setDirection(DcMotor.Direction.REVERSE);
        for (DcMotor motor : new DcMotor[] {left, right}) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    /**
     * TeleOp control, call every loop. Down wins if both are pressed (as in the Everybot code).
     * Neither pressed: HOLD once the catapult has been used, otherwise IDLE so it stays in its start position.
     * Pressing either button cancels a running {@link #launch()} sequence.
     * Releasing the launch button starts an automatic return: after AUTO_DOWN_DELAY_SECONDS the arm is
     * pulled down for PULL_DOWN_SECONDS, then held. Pressing either button cancels the automatic return.
     */
    public void manual(boolean launchButton, boolean downButton) {
        if (lastLaunchButton && !launchButton && !downButton) {
            autoDownPending = true;
            autoDownTimer.reset();
        }
        lastLaunchButton = launchButton;

        if (downButton) {
            sequenceRunning = false;
            autoDownPending = false;
            setMode(Mode.DOWN);
        } else if (launchButton) {
            sequenceRunning = false;
            autoDownPending = false;
            setMode(Mode.UP);
        } else if (sequenceRunning) {
            // update() drives the launch sequence.
        } else if (autoDownPending) {
            double t = autoDownTimer.seconds();
            if (t < AUTO_DOWN_DELAY_SECONDS) {
                setMode(Mode.HOLD);
            } else if (t < AUTO_DOWN_DELAY_SECONDS + PULL_DOWN_SECONDS) {
                setMode(Mode.DOWN);
            } else {
                autoDownPending = false;
                setMode(Mode.HOLD);
            }
        } else {
            setMode(used ? Mode.HOLD : Mode.IDLE);
        }
    }

    /** True while TeleOp is waiting to (or currently) pulling the arm down automatically after a launch. */
    public boolean isAutoReturning() {
        return autoDownPending;
    }

    /** Starts a timed launch: full power up, then full power down, then hold. Needs {@link #update()} every loop. */
    public void launch() {
        sequenceRunning = true;
        sequenceTimer.reset();
        setMode(Mode.UP);
    }

    /** True while a {@link #launch()} sequence is still moving the arm. */
    public boolean isBusy() {
        return sequenceRunning;
    }

    /** Advances the launch sequence. Call every loop. */
    public void update() {
        if (!sequenceRunning) {
            return;
        }
        double t = sequenceTimer.seconds();
        if (t < LAUNCH_SECONDS) {
            setMode(Mode.UP);
        } else if (t < LAUNCH_SECONDS + PULL_DOWN_SECONDS) {
            setMode(Mode.DOWN);
        } else {
            sequenceRunning = false;
            setMode(Mode.HOLD);
        }
    }

    /** Cuts all power (motors brake). Use at the end of autonomous. */
    public void stop() {
        sequenceRunning = false;
        setMode(Mode.IDLE);
    }

    public Mode getMode() {
        return mode;
    }

    public double getPower() {
        return powerFor(mode);
    }

    private void setMode(Mode newMode) {
        if (newMode == Mode.UP || newMode == Mode.DOWN) {
            used = true;
        }
        mode = newMode;
        double power = powerFor(newMode);
        left.setPower(power);
        right.setPower(power);
    }

    private static double powerFor(Mode mode) {
        switch (mode) {
            case UP:   return UP_POWER;
            case DOWN: return DOWN_POWER;
            case HOLD: return HOLD_POWER;
            default:   return 0.0;
        }
    }
}
