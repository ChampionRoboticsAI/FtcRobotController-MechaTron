package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * Scenario 1 from AUTONOMOUS.md. Each block below matches one numbered step in that file.
 * The drive path (Path1) is a field-coordinate curve in Scenario1Path.java.
 *
 * Start: place the robot at Scenario1Path's start pose (x 58.5, y 9.2, facing 90 deg) holding the
 * preloaded Pollen. The INIT telemetry shows the pose the robot thinks it is at.
 * Use "BioBuzz: Scenario 1 (Mirrored)" from the other alliance side: the path is flipped across the field centerline.
 *
 * Tuning values are at the top of this class and in subsystems/Catapult.java (launch timing).
 */
@Autonomous(name = "BioBuzz: Scenario 1", group = "BioBuzz", preselectTeleOp = "BioBuzz: TeleOp")
public class Scenario1Auto extends BioBuzzAutoBase {

    /** Step 2: how long the intake runs in reverse so the flower hood pops up. */
    public static double HOOD_POP_SECONDS = 5.0;

    /**
     * Top speed (inches/second) for Path1. FULL_SPEED by default; lower it (e.g. 25) if the
     * robot overshoots, or if Pollen is pushed away instead of picked up.
     */
    public static double PATH1_SPEED = FULL_SPEED;

    private Scenario1Path paths;

    /** Built on first use so the mirrored subclass gets mirrored paths. */
    private Scenario1Path paths() {
        if (paths == null) {
            paths = new Scenario1Path(mirrored());
        }
        return paths;
    }

    @Override
    protected Pose startPose() {
        return paths().start();
    }

    @Override
    protected void runRoutine() {
        // 1. Launch catapult, then pull it back down (launchCatapult() does both and waits until it is down).
        step("1. Launch preload");
        launchCatapult();

        // 2. Run intake in reverse so the flower hood can pop up, then run it forward continuously.
        step("2. Pop flower hood");
        robot.intake.out();
        waitSeconds(HOOD_POP_SECONDS);
        robot.intake.in();

        // 3. Run Path1.
        step("3. Path1");
        followPath(paths().path1(), PATH1_SPEED);
    }
}
