package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Pedro Pathing 3 configuration for the MechaTron robot (mecanum + goBILDA Pinpoint V2 odometry computer
 * with two goBILDA 4-bar odometry pods). See ROBOT_CONFIGURATION.md for the port mapping.
 *
 * Each config below is meant to be REPLACED with the Java output from the matching AutoTune procedure
 * (http://192.168.43.1:10158 while connected to the robot's Wi-Fi):
 *   drivetrainConfig -> "Mecanum Tuner"
 *   localizerConfig  -> "Pinpoint Tuner"
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

    // Step 2 - Pinpoint Tuner. Pinpoint V2 on Control Hub I2C bus 0, configured as "pinpoint"
    // (device type "goBILDA Pinpoint Odometry Computer"). Pods plug into the Pinpoint, not the hub:
    //   forward pod (wheel parallel to drive direction)      -> Pinpoint "X" port
    //   strafe pod  (wheel perpendicular to drive direction) -> Pinpoint "Y" port
    // The Pinpoint fuses both pods with its own IMU for heading, so the hub IMU is not used.
    // Offset sign convention (goBILDA): xPodOffset = how far LEFT of the tracking center the X pod is
    // (right is negative); yPodOffset = how far FORWARD of the tracking center the Y pod is (back is negative).
    // TODO: offsets and directions below are PLACEHOLDERS until the Pinpoint Tuner is run. Paste its output over this block.
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(2.7905720988596516);
        c.yPodOffset.set(0.43468918387345445);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    // Step 3 - Foresight Tuner.
    // TODO: STALE - these values were tuned with an earlier odometry setup and are kept only so the
    // Follower can be constructed. Re-run the Foresight Tuner after the Pinpoint Tuner and replace them.
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.3303705601736964);
                Controller secondaryTranslationalForward = Controller.proportional(0.12206309432023157);
                Controller primaryTranslationalLateral = Controller.proportional(0.559665975145598);
                Controller secondaryTranslationalLateral = Controller.proportional(0.20678162326602068);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.018710838876781318));
                c.brake.set(Controller.proportionalFeedforward(0.01590421304526412));

                c.headingFeedback.set(Controller.proportional(6.014763779707165));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.03993813593313058, 0.007908686187312644));

                c.linearBrakeCoefficients.set(Matrix.diag(0.10591660365211525, 0.043733949417021065));
                c.quadraticBrakeCoefficients.set(Matrix.diag(5.4595267045757464E-5, 0.0019779532961136485));

                c.maxAchievableForwardVelocity.set(57.76411044436386);
                c.maxAchievableStrafeVelocity.set(44.62539539243014);
                c.naturalForwardDeceleration.set(38.86864334092874);
                c.naturalStrafeDeceleration.set(70.01422821560132);
            }
    );

    /**
     * Builds the Pedro mecanum drivetrain with all four drive motors in RUN_WITHOUT_ENCODER.
     * Pedro drives on raw power and gets all position feedback from the Pinpoint, so the motors must not
     * run their own velocity loop (RUN_USING_ENCODER) or RUN_TO_POSITION. Pedro's Mecanum class does not
     * set a run mode itself, so we do it here.
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

    public static PinpointLocalizer localizer(HardwareMap h) {
        return new PinpointLocalizer(h, localizerConfig);
    }

    public static Follower create(HardwareMap h) {
        return new Follower(
                localizer(h),
                drivetrain(h),
                new Foresight(foresightConfig)
        );
    }
}
