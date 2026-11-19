package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.PinpointTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.Tests;

/**
 * Procedures shown in AutoTune (http://192.168.43.1:10158). Run them in this order:
 * Mecanum Tuner -> Pinpoint Tuner -> Tests (Pose/Localization/Odometry) -> Foresight Tuner -> Tests (Line/Curve/Hold).
 */
public class Tuning {

    @Tuner
    public static Procedure mecanumTuner() {
        return new MecanumTuner();
    }

    @Tuner
    public static Procedure pinpointTuner() {
        return new PinpointTuner();
    }

    @Tuner
    public static Procedure foresightTuner() {
        return new ForesightTuner(Constants::localizer, Constants::drivetrain);
    }

    @Tuner
    public static Procedure tests() {
        return new Tests(
                Constants::drivetrain,
                Constants::localizer,
                () -> new Foresight(Constants.foresightConfig));
    }
}
