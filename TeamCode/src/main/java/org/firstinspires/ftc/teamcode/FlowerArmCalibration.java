package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * Finds the servo position at which each flower arm points straight up, for FlowerArms.java.
 *
 * The arms stay LIMP (unpowered) after START. An arm only powers up when you first nudge it,
 * starting from the middle of the servo range (0.50), so nothing jumps to an unknown position.
 *
 * gamepad1 (DualSense):
 *   D-pad up / down       left arm  +/- (should be out / in)
 *   Triangle / Cross      right arm +/- (should be out / in)
 *   hold L1               fine steps  (0.002, about 0.5 deg)
 *   hold L2               large steps (0.05, about 13.5 deg)
 *   (neither)             normal steps (0.01, about 2.7 deg)
 *   Square                RELAX both arms immediately (use whenever a servo hums or an arm presses on the robot)
 *   R1                    preview the stages using the values currently in FlowerArms.java
 *                         (UP -> BLOCK -> FLAT -> UP ...)
 *
 * 1. Press START. Nudge the left arm once (D-pad up). It moves to about 0.50.
 *    If it presses on the robot or hums, press Square, then keep nudging the OTHER way.
 * 2. Nudge it until it points straight up (use a square or level). Repeat for the right arm.
 * 3. Copy the "Left position" and "Right position" numbers into LEFT_UP_POSITION / RIGHT_UP_POSITION.
 * 4. Check the direction: "+" (D-pad up / Triangle) must move that arm OUTWARD, away from the robot.
 *    If "+" moves an arm inward, flip LEFT_DIRECTION / RIGHT_DIRECTION in FlowerArms.java and calibrate again.
 * 5. Redeploy, run this again and press R1 to check BLOCK (60 deg) and FLAT (90 deg).
 */
@TeleOp(name = "BioBuzz: Flower Arm Calibration", group = "BioBuzz Test")
public class FlowerArmCalibration extends LinearOpMode {

    static final double START_POSITION = 0.50;
    static final double NORMAL_STEP = 0.01;
    static final double FINE_STEP = 0.002;
    static final double LARGE_STEP = 0.05;

    @Override
    public void runOpMode() {
        FlowerArms arms = new FlowerArms(hardwareMap);

        telemetry.addLine("Arms stay limp after START until you nudge them.");
        telemetry.update();
        waitForStart();
        arms.relax();

        double leftPosition = START_POSITION;
        double rightPosition = START_POSITION;
        boolean leftPowered = false;
        boolean rightPowered = false;
        String mode = "RELAXED";

        boolean lastDpadUp = false, lastDpadDown = false, lastTriangle = false, lastCross = false;
        boolean lastR1 = false, lastSquare = false;
        FlowerArms.Stage previewStage = null;

        while (opModeIsActive()) {
            double step = gamepad1.left_bumper ? FINE_STEP
                        : gamepad1.left_trigger > 0.2 ? LARGE_STEP
                        : NORMAL_STEP;

            // ---- Relax: cut power to both servos ----
            if (gamepad1.x && !lastSquare) {
                arms.relax();
                leftPowered = false;
                rightPowered = false;
                previewStage = null;
                mode = "RELAXED";
            }

            // ---- Left arm ----
            int leftNudge = (gamepad1.dpad_up && !lastDpadUp ? 1 : 0) - (gamepad1.dpad_down && !lastDpadDown ? 1 : 0);
            if (leftNudge != 0) {
                // The first nudge after START/relax only powers the arm at its last position.
                if (leftPowered) leftPosition = clip(leftPosition + leftNudge * step);
                arms.setLeftPosition(leftPosition);
                leftPowered = true;
                mode = "MANUAL";
            }

            // ---- Right arm ----
            int rightNudge = (gamepad1.y && !lastTriangle ? 1 : 0) - (gamepad1.a && !lastCross ? 1 : 0);
            if (rightNudge != 0) {
                if (rightPowered) rightPosition = clip(rightPosition + rightNudge * step);
                arms.setRightPosition(rightPosition);
                rightPowered = true;
                mode = "MANUAL";
            }

            // ---- Stage preview using the values in FlowerArms.java ----
            if (gamepad1.right_bumper && !lastR1) {
                int next = previewStage == null ? 0 : (previewStage.ordinal() + 1) % FlowerArms.Stage.values().length;
                previewStage = FlowerArms.Stage.values()[next];
                arms.setStage(previewStage);
                leftPosition = arms.getLeftPosition();
                rightPosition = arms.getRightPosition();
                leftPowered = true;
                rightPowered = true;
                mode = "PREVIEW " + previewStage + " (" + (int) previewStage.degreesOut + " deg)";
            }

            lastDpadUp = gamepad1.dpad_up;
            lastDpadDown = gamepad1.dpad_down;
            lastTriangle = gamepad1.y;
            lastCross = gamepad1.a;
            lastR1 = gamepad1.right_bumper;
            lastSquare = gamepad1.x;

            telemetry.addData("Mode", mode);
            telemetry.addData("Left  position", "%.3f  %s", leftPosition, leftPowered ? "" : "(limp)");
            telemetry.addData("Right position", "%.3f  %s", rightPosition, rightPowered ? "" : "(limp)");
            telemetry.addLine("Humming / pressing on robot? Press SQUARE now.");
            telemetry.addLine("D-pad: left | Triangle/Cross: right | L1 fine | L2 large | R1 preview");
            telemetry.update();
        }
    }

    private static double clip(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
