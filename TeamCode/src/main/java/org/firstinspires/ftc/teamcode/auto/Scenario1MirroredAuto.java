package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * Scenario 1 run from the opposite alliance side: Scenario1Path is flipped across the field
 * centerline (x -> 144 - x, heading -> 180 - heading). All mechanism steps are unchanged.
 * Place the robot at the mirrored start pose (x 85.5, y 9.2, facing 90 deg).
 */
@Autonomous(name = "BioBuzz: Scenario 1 (Mirrored)", group = "BioBuzz", preselectTeleOp = "BioBuzz: TeleOp")
public class Scenario1MirroredAuto extends Scenario1Auto {

    @Override
    protected boolean mirrored() {
        return true;
    }
}
