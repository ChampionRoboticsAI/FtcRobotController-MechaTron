package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.subsystems.FlowerArms;

/**
 * Scenario 1 from AUTONOMOUS.md. Each block below matches one numbered step in that file.
 * The drive paths (Path1, Path2, Path3) are field-coordinate curves in Scenario1Path.java.
 *
 * Start: place the robot at Scenario1Path's start pose (x 59.1, y 6.5, facing 90 deg) holding the
 * 4 preloaded Pollen. The INIT telemetry shows the pose the robot thinks it is at.
 * Use "BioBuzz: Scenario 1 (Mirrored)" from the other alliance side: the paths are flipped across the field centerline.
 *
 * Tuning values are at the top of this class and in subsystems/Catapult.java (launch timing).
 */
@Autonomous(name = "BioBuzz: Scenario 1", group = "BioBuzz", preselectTeleOp = "BioBuzz: TeleOp")
public class Scenario1Auto extends BioBuzzAutoBase {

    /** Step 2: how long the intake runs in reverse so the flower hood pops up. */
    public static double HOOD_POP_SECONDS = 5.0;

    /** Step 3: time for the flower arm servos to reach FLAT before driving. */
    public static double FLOWER_ARM_SETTLE_SECONDS = 0.5;

    /**
     * Top speed (inches/second) for each path. FULL_SPEED by default; lower one (e.g. 25) if the
     * robot overshoots, or if Pollen is pushed away instead of picked up.
     */
    public static double PATH1_SPEED = FULL_SPEED;
    public static double PATH2_SPEED = FULL_SPEED;
    public static double PATH3_SPEED = FULL_SPEED;

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
        // 1. Launch catapult and pull catapult arm down.
        step("1. Launch preload");
        launchCatapult();

        // 2. Run intake in reverse so the flower hood can pop up, then run it forward continuously.
        step("2. Pop flower hood");
        robot.intake.out();
        waitSeconds(HOOD_POP_SECONDS);
        robot.intake.in();

        // 3. Move the flower arm down 90 degrees.
        step("3. Flower arms down");
        robot.flowerArms.setStage(FlowerArms.Stage.FLAT);
        waitSeconds(FLOWER_ARM_SETTLE_SECONDS);

        // 4. Run Path1.
        step("4. Path1");
        followPath(paths().path1(), PATH1_SPEED);

        // 5. Run Path2.
        step("5. Path2");
        followPath(paths().path2(), PATH2_SPEED);

        // 6. Launch catapult and pull catapult arm down.
        step("6. Launch collected pollen");
        launchCatapult();

        // 7. Run Path3.
        step("7. Path3");
        followPath(paths().path3(), PATH3_SPEED);
    }
}
