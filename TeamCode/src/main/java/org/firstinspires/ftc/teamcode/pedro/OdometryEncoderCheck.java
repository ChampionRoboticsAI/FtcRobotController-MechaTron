package org.firstinspires.ftc.teamcode.pedro;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/**
 * Shows the raw encoder count on all four drive-motor encoder ports so you can confirm each odometry
 * pod is wired to the port the Pedro config expects. Push the robot by hand; motors are never powered.
 *   x1 (left)  pod -> "Front Left" (port 0)
 *   x2 (right) pod -> "Back Right" (port 3)
 *   y (strafe) pod -> "Back Left"  (port 2)
 */
@TeleOp(name = "Odometry Encoder Check", group = "Pedro")
public class OdometryEncoderCheck extends LinearOpMode {

    private static final String[][] PORTS = {
            {"Front Left", "port 0 - x1 left pod"},
            {"Front Right", "port 1 - no pod"},
            {"Back Left", "port 2 - y strafe pod"},
            {"Back Right", "port 3 - x2 right pod"},
    };

    @Override
    public void runOpMode() {
        DcMotorEx[] motors = new DcMotorEx[PORTS.length];
        for (int i = 0; i < PORTS.length; i++) {
            motors[i] = hardwareMap.get(DcMotorEx.class, PORTS[i][0]);
            motors[i].setPower(0);
            motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        telemetry.addLine("Press Start, then push the robot forward and left by hand.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            telemetry.addLine("Forward push: x1 and x2 should change. Left push: y should change.");
            for (int i = 0; i < PORTS.length; i++) {
                telemetry.addData(PORTS[i][0] + " (" + PORTS[i][1] + ")", motors[i].getCurrentPosition());
            }
            telemetry.update();
        }
    }
}
