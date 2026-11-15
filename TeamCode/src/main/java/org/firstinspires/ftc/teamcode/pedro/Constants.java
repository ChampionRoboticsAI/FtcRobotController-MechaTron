package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.Encoder;
import com.pedropathing.revhub.localizers.ThreeWheelConfig;
import com.pedropathing.revhub.localizers.ThreeWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Pedro Pathing 3 configuration for the MechaTron robot (mecanum + 3 goBILDA 4-bar odometry pods).
 * See ROBOT_CONFIGURATION.md for the port mapping.
 *
 * Each config below is meant to be REPLACED with the Java output from the matching AutoTune procedure
 * (http://192.168.43.1:10158 while connected to the robot's Wi-Fi):
 *   drivetrainConfig -> "Mecanum Tuner"
 *   localizerConfig  -> "Three Wheel Tuner"
 *   foresightConfig  -> "Foresight Tuner"
 */
public class Constants {

    // Step 1 - Mecanum Tuner. Names match the Driver Hub configuration; confirm directions with the tuner.
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("Front Left");
        c.frontRightName.set("Front Right");
        c.backLeftName.set("Back Left");
        c.backRightName.set("Back Right");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });

    // Step 2 - Three Wheel Tuner. Pods are read through the Control Hub drive-motor encoder ports:
    //   left  parallel pod (x1) -> encoder port of motor "Front Left" (motor port 0)
    //   right parallel pod (x2) -> encoder port of motor "Back Right" (motor port 3)
    //   strafe pod (y)          -> encoder port of motor "Back Left"  (motor port 2)
    // TODO: every numeric value and direction below is a PLACEHOLDER (nominal goBILDA 4-bar pod,
    // 2000 ticks/rev, 32 mm wheel) until the Three Wheel Tuner is run. Paste its output over this block.
    public static ThreeWheelConfig localizerConfig = new ThreeWheelConfig(c -> {
        c.leftEncoderName.set("Front Left");
        c.rightEncoderName.set("Back Right");
        c.strafeEncoderName.set("Back Left");
        c.leftPodY.set(6.0);
        c.rightPodY.set(-6.0);
        c.strafePodX.set(0.0);
        c.forwardTicksToInches.set(0.001979);
        c.strafeTicksToInches.set(0.001979);
        c.turnTicksToRadians.set(0.001979);
        c.leftEncoderDirection.set(Encoder.FORWARD);
        c.rightEncoderDirection.set(Encoder.FORWARD);
        c.strafeEncoderDirection.set(Encoder.FORWARD);
    });

    // Step 3 - Foresight Tuner.
    // TODO: STALE - these values were tuned with the old two-wheel localizer and are kept only so the
    // Follower can be constructed. Re-run the Foresight Tuner after the Three Wheel Tuner and replace them.
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.6850697130730637);
                Controller secondaryTranslationalForward = Controller.proportional(0.2531149535806284);
                Controller primaryTranslationalLateral = Controller.proportional(1.0633043370772866);
                Controller secondaryTranslationalLateral = Controller.proportional(0.39286254053489905);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.03738634357334483));
                c.brake.set(Controller.proportionalFeedforward(0.0317783920373431));

                c.headingFeedback.set(Controller.proportional(124.95130219429352));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.06403573825044491, 1.65065750247749E-4));

                c.linearBrakeCoefficients.set(Matrix.diag(0.04935973006661506, 0.01079415315533114));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.002331998638511691, 0.00383875275444274));

                c.maxAchievableForwardVelocity.set(27.48933552180684);
                c.maxAchievableStrafeVelocity.set(23.6836633560403);
                c.naturalForwardDeceleration.set(17.924993558472817);
                c.naturalStrafeDeceleration.set(39.69320828172962);
            }
    );

    /**
     * Builds the Pedro mecanum drivetrain with all four drive motors in RUN_WITHOUT_ENCODER.
     * Three of the four drive-motor encoder ports are used by odometry pods, so the drive motors have
     * no encoder feedback and must never use RUN_USING_ENCODER / RUN_TO_POSITION.
     * Pedro's Mecanum class does not set a run mode itself, so we do it here.
     */
    public static Mecanum drivetrain(HardwareMap h) {
        setDriveMotorsRunWithoutEncoder(h);
        return new Mecanum(h, drivetrainConfig);
    }

    public static void setDriveMotorsRunWithoutEncoder(HardwareMap h) {
        String[] names = {
                drivetrainConfig.frontLeftName.get(),
                drivetrainConfig.frontRightName.get(),
                drivetrainConfig.backLeftName.get(),
                drivetrainConfig.backRightName.get()
        };
        for (String name : names) {
            h.get(DcMotor.class, name).setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    public static ThreeWheelLocalizer localizer(HardwareMap h) {
        return new ThreeWheelLocalizer(h, localizerConfig);
    }

    public static Follower create(HardwareMap h) {
        return new Follower(
                localizer(h),
                drivetrain(h),
                new Foresight(foresightConfig)
        );
    }
}
