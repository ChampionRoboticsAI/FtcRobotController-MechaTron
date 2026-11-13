package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.follower.Follower;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.Encoder;
import com.pedropathing.revhub.localizers.RevHubIMU;
import com.pedropathing.revhub.localizers.TwoWheelConfig;
import com.pedropathing.revhub.localizers.TwoWheelLocalizer;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Pedro Pathing 3 configuration for the MechaTron StarterBot (mecanum + 2 goBILDA 4-bar pods + Control Hub IMU).
 * See ROBOT_CONFIGURATION.md for the port mapping.
 *
 * Each config below is meant to be REPLACED with the Java output from the matching AutoTune procedure
 * (http://192.168.43.1:10158 while connected to the robot's Wi-Fi):
 *   drivetrainConfig -> "Mecanum Tuner"
 *   localizerConfig  -> "Two Wheel Tuner"
 *   foresightConfig  -> "Foresight Tuner"
 */
public class Constants {

    // Step 1 - Mecanum Tuner. Names match the Driver Hub configuration; directions are copied from
    // BioBuzzTeleOp and must still be confirmed with the Mecanum Tuner.
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

    // Step 2 - Two Wheel Tuner. Pods are read through the drive motor encoder ports:
    //   x (forward) pod -> Control Hub motor port 0 encoder ("Front Left")
    //   y (strafe)  pod -> Control Hub motor port 3 encoder ("Back Right")
    // TODO: every numeric value and direction below is a PLACEHOLDER until the tuner is run.
    public static TwoWheelConfig localizerConfig = new TwoWheelConfig(c -> {
        c.xPodName.set("Front Left");
        c.yPodName.set("Back Right");
        c.imuName.set("imu");
        c.xPodOffset.set(25.10309329566553);
        c.yPodOffset.set(80.97762906858335);
        c.forwardTicksToInches.set(0.09791277685589667);
        c.strafeTicksToInches.set(0.04241193447265154);
        c.xPodDirection.set(Encoder.FORWARD);
        c.yPodDirection.set(Encoder.FORWARD);
        c.imu.set(new RevHubIMU(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
        )));
    });

    // Step 3 - Foresight Tuner. TODO: replace with the generated ForesightConfig.
    public static ForesightConfig foresightConfig = new ForesightConfig(c -> {
    });

    public static Follower create(HardwareMap h) {
        return new Follower(
                new TwoWheelLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }
}
