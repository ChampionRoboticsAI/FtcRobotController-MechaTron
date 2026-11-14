package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * Finds the servo position at which each flower arm points straight up, for FlowerArms.java.
 *
 * gamepad1 (DualSense):
 *   D-pad up / down       left arm  out / in
 *   Triangle / Cross      right arm out / in
 *   hold L1               fine steps (0.002 instead of 0.01, about 0.5 deg instead of 2.7 deg)
 *   R1                    preview the stages using the values currently in FlowerArms.java
 *                         (UP -> BLOCK -> FLAT -> UP ...)
 *
 * 1. Press START. Both arms go to the UP positions currently in FlowerArms.java. If an arm is
 *    pushing against the robot, move it out immediately (D-pad up / Triangle).
 * 2. Nudge each arm until it points straight up (use a square or level).
 * 3. In MANUAL mode, copy the "Left position" and "Right position" numbers into LEFT_UP_POSITION / RIGHT_UP_POSITION.
 * 4. Redeploy, run this again and press R1 to check BLOCK (45 deg) and FLAT (90 deg).
 */
@TeleOp(name = "BioBuzz: Flower Arm Calibration", group = "BioBuzz Test")
public class FlowerArmCalibration extends LinearOpMode {

    static final double COARSE_STEP = 0.01;
    static final double FINE_STEP = 0.002;

    @Override
    public void runOpMode() {
        FlowerArms arms = new FlowerArms(hardwareMap);

        telemetry.addLine("Press START: arms move to the UP positions in FlowerArms.java");
        telemetry.update();
        waitForStart();

        double leftPosition = FlowerArms.LEFT_UP_POSITION;
        double rightPosition = FlowerArms.RIGHT_UP_POSITION;
        arms.setPositions(leftPosition, rightPosition);
        String mode = "MANUAL";

        boolean lastDpadUp = false, lastDpadDown = false, lastTriangle = false, lastCross = false, lastR1 = false;
        FlowerArms.Stage previewStage = FlowerArms.Stage.UP;

        while (opModeIsActive()) {
            double step = gamepad1.left_bumper ? FINE_STEP : COARSE_STEP;
            boolean moved = false;

            if (gamepad1.dpad_up && !lastDpadUp)     { leftPosition += step;  moved = true; }
            if (gamepad1.dpad_down && !lastDpadDown) { leftPosition -= step;  moved = true; }
            if (gamepad1.y && !lastTriangle)         { rightPosition += step; moved = true; }
            if (gamepad1.a && !lastCross)            { rightPosition -= step; moved = true; }

            if (moved) {
                leftPosition = Math.max(0.0, Math.min(1.0, leftPosition));
                rightPosition = Math.max(0.0, Math.min(1.0, rightPosition));
                arms.setPositions(leftPosition, rightPosition);
                mode = "MANUAL";
            }

            if (gamepad1.right_bumper && !lastR1) {
                previewStage = FlowerArms.Stage.values()[(previewStage.ordinal() + 1) % FlowerArms.Stage.values().length];
                arms.setStage(previewStage);
                leftPosition = arms.getLeftPosition();
                rightPosition = arms.getRightPosition();
                mode = "PREVIEW " + previewStage + " (" + (int) previewStage.degreesOut + " deg)";
            }

            lastDpadUp = gamepad1.dpad_up;
            lastDpadDown = gamepad1.dpad_down;
            lastTriangle = gamepad1.y;
            lastCross = gamepad1.a;
            lastR1 = gamepad1.right_bumper;

            telemetry.addData("Mode", mode);
            telemetry.addData("Left  position", "%.3f  (%+.1f deg out from current UP value)",
                    leftPosition, (leftPosition - FlowerArms.LEFT_UP_POSITION) * FlowerArms.SERVO_RANGE_DEGREES);
            telemetry.addData("Right position", "%.3f  (%+.1f deg out from current UP value)",
                    rightPosition, (rightPosition - FlowerArms.RIGHT_UP_POSITION) * FlowerArms.SERVO_RANGE_DEGREES);
            telemetry.addLine("D-pad up/down: left arm | Triangle/Cross: right arm | L1: fine | R1: preview stages");
            telemetry.update();
        }
    }
}
