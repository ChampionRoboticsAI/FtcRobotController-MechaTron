package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * Scenario 1 run from the opposite side of the field: every left/right strafe and turn is swapped.
 * Forward/back distances and all mechanism steps are unchanged.
 */
@Autonomous(name = "BioBuzz: Scenario 1 (Mirrored)", group = "BioBuzz", preselectTeleOp = "BioBuzz: TeleOp")
public class Scenario1MirroredAuto extends Scenario1Auto {

    @Override
    protected boolean mirrored() {
        return true;
    }
}
