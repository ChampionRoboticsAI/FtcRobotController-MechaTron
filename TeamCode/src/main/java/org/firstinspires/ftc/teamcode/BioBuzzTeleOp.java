package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * BioBuzz (2026-27) robot-centric TeleOp for the Everybot build on a Mecanum drive.
 * Mechanism controls follow the Robonauts Everybot reference code:
 *   https://github.com/Robonauts-Everybot/FTC-Everybot-Code-2025-DECODE
 * Hardware is described in ROBOT_CONFIGURATION.md. Configuration names used here:
 *
 *   Control Hub   motor port 0: "Front Left"      motor port 1: "Front Right"
 *   Control Hub   motor port 2: "Back Left"       motor port 3: "Back Right"
 *   Control Hub   servo port 0: "Left Flower Arm" servo port 1: "Right Flower Arm"
 *   Expansion Hub motor port 1: "Catapult Right"  motor port 2: "Catapult Left"
 *   Expansion Hub motor port 3: "Intake"
 *
 * Driven with gamepad1 (PlayStation DualSense: Cross = A, Circle = B, Square = X,
 * Triangle = Y, Share/Create = BACK, L3 = left stick button). Full map: GAMEPAD_MAPPING.md.
 *
 *  Left stick            drive forward/back and strafe
 *  Right stick X         turn
 *  L3 (click)            toggle slow mode
 *  LT (hold)             intake in
 *  LB (hold)             intake out (wins over LT)
 *  RB (hold)             catapult UP (launch)
 *  RT (hold)             catapult DOWN (wins over RB, as in the Everybot code)
 *  neither RB nor RT     catapult HOLD: small power keeps the arm down against the rubber band
 *                        (IDLE, no power, at start until RB or RT is first pressed)
 *  A / Cross             flower arms OUT
 *  B / Circle            flower arms IN
 *  BACK + X/A/Y/B        motor test: FL / BL / FR / BR wheel
 *
 * FIRST TEST: lift the robot. The two catapult motors are geared together, so if one
 * is reversed wrongly they will fight each other. Tap RB briefly and confirm both
 * catapult motors turn the arm the same way before holding any button down.
 */

@TeleOp(name="BioBuzz: TeleOp", group="BioBuzz")
public class BioBuzzTeleOp extends LinearOpMode {

    // Fraction of full power used while slow mode is on.
    static final double SLOW_MODE_SCALE = 0.4;

    // Trigger travel (0..1) needed to count as "pressed".
    static final double TRIGGER_THRESHOLD = 0.2;

    // Intake powers (from Everybot code).
    static final double INTAKE_IN_POWER  =  1.0;
    static final double INTAKE_OUT_POWER = -0.9;

    // Catapult powers (from Everybot code). Positive power drives the arm DOWN.
    static final double CATAPULT_UP_POWER   = -1.0;
    static final double CATAPULT_DOWN_POWER =  1.0;
    // Pulls against the rubber band to keep the arm down after a launch. Raise if the band
    // lifts the arm back up; lower if the motors buzz or get hot while holding.
    static final double CATAPULT_HOLD_POWER =  0.2;

    // Flower arm servo positions (0.0..1.0). These are starting guesses: tune on the robot.
    // The right servo is reversed, so the same value moves both arms as mirror images.
    static final double FLOWER_ARM_IN_POSITION  = 0.0;
    static final double FLOWER_ARM_OUT_POSITION = 0.5;

    private enum CatapultMode { IDLE, UP, DOWN, HOLD }

    private final ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotor intake = null;
    private DcMotor catapultLeft = null;
    private DcMotor catapultRight = null;
    private Servo leftFlowerArm = null;
    private Servo rightFlowerArm = null;

    @Override
    public void runOpMode() {

        // These names must match the names in the robot's active configuration file.
        frontLeftDrive = hardwareMap.get(DcMotor.class, "Front Left");
        backLeftDrive = hardwareMap.get(DcMotor.class, "Back Left");
        frontRightDrive = hardwareMap.get(DcMotor.class, "Front Right");
        backRightDrive = hardwareMap.get(DcMotor.class, "Back Right");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        // Drive purely on joystick power, and hold position when the sticks are released.
        for (DcMotor motor : new DcMotor[] {frontLeftDrive, backLeftDrive, frontRightDrive, backRightDrive}) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        // Forward should pull game pieces IN. Flip if the intake spits them out.
        intake = hardwareMap.get(DcMotor.class, "Intake");
        intake.setDirection(DcMotor.Direction.FORWARD);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // The two catapult motors face each other, so one is reversed (as in the Everybot code).
        // With these directions, positive power must move the arm DOWN on BOTH motors.
        catapultLeft = hardwareMap.get(DcMotor.class, "Catapult Left");
        catapultRight = hardwareMap.get(DcMotor.class, "Catapult Right");
        catapultLeft.setDirection(DcMotor.Direction.FORWARD);
        catapultRight.setDirection(DcMotor.Direction.REVERSE);
        for (DcMotor motor : new DcMotor[] {catapultLeft, catapultRight}) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        leftFlowerArm = hardwareMap.get(Servo.class, "Left Flower Arm");
        rightFlowerArm = hardwareMap.get(Servo.class, "Right Flower Arm");
        leftFlowerArm.setDirection(Servo.Direction.FORWARD);
        rightFlowerArm.setDirection(Servo.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        boolean slowMode = false;
        boolean lastSlowButton = false;
        boolean flowerArmsOut = false;  // Arms move to IN as soon as the OpMode starts.
        boolean catapultUsed = false;   // No hold power until the driver first moves the catapult.

        while (opModeIsActive()) {
            boolean motorTest = gamepad1.back;

            // ---------------- DRIVE ----------------
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            double frontLeftPower  = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower   = axial - lateral + yaw;
            double backRightPower  = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%, preserving the requested motion.
            double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }

            // Motor test mode: hold BACK and press X/A/Y/B to run a single wheel forward.
            if (motorTest) {
                frontLeftPower  = gamepad1.x ? 1.0 : 0.0;
                backLeftPower   = gamepad1.a ? 1.0 : 0.0;
                frontRightPower = gamepad1.y ? 1.0 : 0.0;
                backRightPower  = gamepad1.b ? 1.0 : 0.0;
            }

            // Click the left stick (L3) to toggle slow mode.
            boolean slowButton = gamepad1.left_stick_button;
            if (slowButton && !lastSlowButton) {
                slowMode = !slowMode;
            }
            lastSlowButton = slowButton;

            double speedScale = (slowMode || motorTest) ? SLOW_MODE_SCALE : 1.0;

            frontLeftDrive.setPower(frontLeftPower * speedScale);
            frontRightDrive.setPower(frontRightPower * speedScale);
            backLeftDrive.setPower(backLeftPower * speedScale);
            backRightDrive.setPower(backRightPower * speedScale);

            // ---------------- INTAKE ----------------
            // LB (out) wins if both are pressed, as in the Everybot code.
            double intakePower = 0.0;
            if (gamepad1.left_bumper) {
                intakePower = INTAKE_OUT_POWER;
            } else if (gamepad1.left_trigger > TRIGGER_THRESHOLD) {
                intakePower = INTAKE_IN_POWER;
            }
            intake.setPower(intakePower);

            // ---------------- CATAPULT ----------------
            // RT (down) wins if both are pressed, as in the Everybot code.
            CatapultMode catapultMode;
            double catapultPower;
            if (gamepad1.right_trigger > TRIGGER_THRESHOLD) {
                catapultMode = CatapultMode.DOWN;
                catapultPower = CATAPULT_DOWN_POWER;
            } else if (gamepad1.right_bumper) {
                catapultMode = CatapultMode.UP;
                catapultPower = CATAPULT_UP_POWER;
            } else if (catapultUsed) {
                // Slight feed-forward keeps the catapult down while driving.
                catapultMode = CatapultMode.HOLD;
                catapultPower = CATAPULT_HOLD_POWER;
            } else {
                // Catapult starts in its initial position: leave it braked until first used.
                catapultMode = CatapultMode.IDLE;
                catapultPower = 0.0;
            }
            if (catapultMode == CatapultMode.UP || catapultMode == CatapultMode.DOWN) {
                catapultUsed = true;
            }
            catapultLeft.setPower(catapultPower);
            catapultRight.setPower(catapultPower);

            // ---------------- FLOWER ARMS ----------------
            // A = out, B = in; ignored during motor test, where A/B run wheels.
            if (!motorTest) {
                if (gamepad1.a) {
                    flowerArmsOut = true;
                } else if (gamepad1.b) {
                    flowerArmsOut = false;
                }
            }
            double flowerArmPosition = flowerArmsOut ? FLOWER_ARM_OUT_POSITION : FLOWER_ARM_IN_POSITION;
            leftFlowerArm.setPosition(flowerArmPosition);
            rightFlowerArm.setPosition(flowerArmPosition);

            // ---------------- TELEMETRY ----------------
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Speed", motorTest ? "MOTOR TEST (X=FL A=BL Y=FR B=BR)"
                                                 : slowMode ? "SLOW" : "FULL");
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower * speedScale, frontRightPower * speedScale);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower * speedScale, backRightPower * speedScale);
            telemetry.addData("Intake", "%4.2f", intakePower);
            telemetry.addData("Catapult", "%s (%4.2f)", catapultMode, catapultPower);
            telemetry.addData("Flower Arms", "%s (%4.2f)", flowerArmsOut ? "OUT" : "IN", flowerArmPosition);
            telemetry.update();
        }
    }
}
