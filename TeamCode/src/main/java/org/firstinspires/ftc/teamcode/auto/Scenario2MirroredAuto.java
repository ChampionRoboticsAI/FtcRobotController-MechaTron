package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * Scenario 2 run from the opposite alliance side: Scenario2Path is flipped across the field
 * centerline (x -> 144 - x, heading -> 180 - heading). All mechanism steps are unchanged.
 * Place the robot at the mirrored start pose (x 85.5, y 9.2, facing 90 deg).
 */
@Autonomous(name = "BioBuzz: Scenario 2 (Mirrored)", group = "BioBuzz", preselectTeleOp = "BioBuzz: TeleOp")
public class Scenario2MirroredAuto extends Scenario2Auto {

    @Override
    protected boolean mirrored() {
        return true;
    }
}
