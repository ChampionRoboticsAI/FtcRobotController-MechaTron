package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.subsystems.FlowerArms;

/**
 * Scenario 1 from AUTONOMOUS.md. Each block below matches one numbered step in that file.
 *
 * Start: robot against the perimeter wall on our alliance side, holding the 4 preloaded Pollen,
 * facing the direction of the first "drive straight 24 inches".
 * The routine runs exactly as written; use "BioBuzz: Scenario 1 (Mirrored)" from the other side
 * of the field, where every left/right is swapped.
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
     * Step 4: top speed (inches/second) while sweeping Pollen into the intake.
     * FULL_SPEED by default; lower it (e.g. 25) if Pollen gets pushed away instead of picked up.
     */
    public static double SWEEP_SPEED = FULL_SPEED;

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

        // 4. Straight 24, left 24, right 12, left 36 - sweeps Pollen in through the intake.
        step("4. Sweep pollen");
        move(24, 0, SWEEP_SPEED);
        move(0, 24, SWEEP_SPEED);
        move(0, -12, SWEEP_SPEED);
        move(0, 36, SWEEP_SPEED);

        // 5. Back 36, turn right, straight 60, turn right, straight 24, turn right, reverse 24.
        step("5. Drive to launch spot");
        back(36);
        turnRight(90);
        forward(60);
        turnRight(90);
        forward(24);
        turnRight(90);
        back(24);

        // 6. Launch catapult and pull catapult arm down.
        step("6. Launch collected pollen");
        launchCatapult();

        // 7. Straight 24, turn right, forward 40.
        step("7. Park");
        forward(24);
        turnRight(90);
        forward(40);
    }
}
