package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.revhub.localizers.TwoWheelLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.Tests;
import org.firstinspires.ftc.teamcode.pedro.procedures.TwoWheelTuner;

/**
 * Procedures shown in AutoTune (http://192.168.43.1:10158). Run them in this order:
 * Mecanum Tuner -> Two Wheel Tuner -> Tests (Localization/Odometry) -> Foresight Tuner -> Tests (Line/Curve/Hold).
 */
public class Tuning {

    @Tuner
    public static Procedure mecanumTuner() {
        return new MecanumTuner();
    }

    @Tuner
    public static Procedure twoWheelTuner() {
        return new TwoWheelTuner();
    }

    @Tuner
    public static Procedure foresightTuner() {
        return new ForesightTuner(
                hardwareMap -> new TwoWheelLocalizer(hardwareMap, Constants.localizerConfig),
                Constants::drivetrain);
    }

    @Tuner
    public static Procedure tests() {
        return new Tests(
                Constants::drivetrain,
                hardwareMap -> new TwoWheelLocalizer(hardwareMap, Constants.localizerConfig),
                () -> new Foresight(Constants.foresightConfig));
    }
}
