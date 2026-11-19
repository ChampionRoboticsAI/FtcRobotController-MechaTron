package org.firstinspires.ftc.teamcode.pedro;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Bench check for the goBILDA Pinpoint V2 and its two odometry pods. Push the robot by hand; motors are
 * never powered. Uses the name, pod type, offsets and directions from Constants.localizerConfig, so after
 * the Pinpoint Tuner has been pasted in, this also confirms the tuned config reads correctly.
 *
 *   Status should be READY. If it is not, check the I2C cable and the configuration name.
 *   Push forward -> raw X counts change, X (in) increases.
 *   Push left    -> raw Y counts change, Y (in) increases.
 *   Turn CCW     -> heading (deg) increases.
 *
 * Gamepad1 A: reset position and recalibrate the IMU (keep the robot still for ~0.25 s).
 */
@TeleOp(name = "Pinpoint Check", group = "Pedro")
public class PinpointCheck extends LinearOpMode {

    @Override
    public void runOpMode() {
        PinpointConfigView cfg = new PinpointConfigView();
        GoBildaPinpointDriver pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, cfg.name);
        pinpoint.setOffsets(cfg.xOffset, cfg.yOffset, cfg.offsetUnit);
        pinpoint.setEncoderResolution(cfg.podType);
        pinpoint.setEncoderDirections(cfg.xDirection, cfg.yDirection);
        pinpoint.resetPosAndIMU();

        telemetry.addData("Pinpoint", "\"%s\" found, device version %d", cfg.name, pinpoint.getDeviceVersion());
        telemetry.addLine("Keep the robot still, then press Start.");
        telemetry.update();
        waitForStart();

        boolean prevA = false;
        while (opModeIsActive()) {
            pinpoint.update();

            if (gamepad1.a && !prevA) {
                pinpoint.resetPosAndIMU();
            }
            prevA = gamepad1.a;

            telemetry.addData("Status", pinpoint.getDeviceStatus());
            telemetry.addData("Loop rate (Hz)", "%.0f", pinpoint.getFrequency());
            telemetry.addLine("Forward push: X up.  Left push: Y up.  CCW turn: heading up.");
            telemetry.addData("Raw X pod counts", pinpoint.getEncoderX());
            telemetry.addData("Raw Y pod counts", pinpoint.getEncoderY());
            telemetry.addData("X (in)", "%.2f", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("Y (in)", "%.2f", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("Heading (deg)", "%.1f", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.addLine("Gamepad1 A = reset position + recalibrate IMU");
            telemetry.update();
        }
    }

    /** Reads the values out of Constants.localizerConfig once so this OpMode never drifts from Pedro's config. */
    private static class PinpointConfigView {
        final String name = Constants.localizerConfig.name.get();
        final double xOffset = Constants.localizerConfig.xPodOffset.get();
        final double yOffset = Constants.localizerConfig.yPodOffset.get();
        final DistanceUnit offsetUnit = Constants.localizerConfig.offsetUnits.get();
        final GoBildaPinpointDriver.GoBildaOdometryPods podType = Constants.localizerConfig.podType.get();
        final GoBildaPinpointDriver.EncoderDirection xDirection = Constants.localizerConfig.xPodDirection.get();
        final GoBildaPinpointDriver.EncoderDirection yDirection = Constants.localizerConfig.yPodDirection.get();
    }
}
