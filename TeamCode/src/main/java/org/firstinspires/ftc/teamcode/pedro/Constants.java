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
        c.xPodOffset.set(0.0);
        c.yPodOffset.set(0.0);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    // Step 3 - Foresight Tuner.
    // TODO: STALE - these values were tuned with an earlier odometry setup and are kept only so the
    // Follower can be constructed. Re-run the Foresight Tuner after the Pinpoint Tuner and replace them.
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.3465517621917762);
                Controller secondaryTranslationalForward = Controller.proportional(0.12804161609623108);
                Controller primaryTranslationalLateral = Controller.proportional(2.872886393417167);
                Controller secondaryTranslationalLateral = Controller.proportional(1.0614547574294109);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.018490952888152876));
                c.brake.set(Controller.proportionalFeedforward(0.015717309954929943));

                c.headingFeedback.set(Controller.proportional(12.281786102893301));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.06139836247742579, 6.321832078074226E-4));

                c.linearBrakeCoefficients.set(Matrix.diag(0.10579129242595278, 0.09580580962131512));
                c.quadraticBrakeCoefficients.set(Matrix.diag(-1.8613035374189795E-4, 3.0331836771166343E-4));

                c.maxAchievableForwardVelocity.set(60.801051034920384);
                c.maxAchievableStrafeVelocity.set(45.98194035058009);
                c.naturalForwardDeceleration.set(53.44487382794588);
                c.naturalStrafeDeceleration.set(94.5163785165338);
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
