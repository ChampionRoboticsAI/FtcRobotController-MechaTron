package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.FlowerArms;
import org.firstinspires.ftc.teamcode.subsystems.MecanumDrive;

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
 * Hardware setup and tuning values live in shared classes so Autonomous uses exactly the same settings:
 *   Robot.java                   intake, catapult and flower arms (classes in subsystems/)
 *   subsystems/MecanumDrive.java drive motors
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
 *  A / Cross (press)     flower arms one stage OUT: UP -> BLOCK (60 deg) -> FLAT (90 deg)
 *  B / Circle (press)    flower arms one stage IN:  FLAT -> BLOCK -> UP
 *                        (arms go to UP when TeleOp starts; positions are in subsystems/FlowerArms.java)
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

    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        MecanumDrive drive = new MecanumDrive(hardwareMap);
        Robot robot = new Robot(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // Arms point straight up as soon as TeleOp starts.
        robot.flowerArms.setStage(FlowerArms.Stage.UP);

        boolean slowMode = false;
        boolean lastSlowButton = false;
        boolean lastFlowerOutButton = false;
        boolean lastFlowerInButton = false;

        while (opModeIsActive()) {
            boolean motorTest = gamepad1.back;

            // ---------------- DRIVE ----------------
            // Click the left stick (L3) to toggle slow mode.
            boolean slowButton = gamepad1.left_stick_button;
            if (slowButton && !lastSlowButton) {
                slowMode = !slowMode;
            }
            lastSlowButton = slowButton;

            double speedScale = (slowMode || motorTest) ? SLOW_MODE_SCALE : 1.0;

            if (motorTest) {
                // Motor test mode: hold BACK and press X/A/Y/B to run a single wheel forward.
                drive.setWheelPowers(
                        gamepad1.x ? speedScale : 0.0,    // front left
                        gamepad1.y ? speedScale : 0.0,    // front right
                        gamepad1.a ? speedScale : 0.0,    // back left
                        gamepad1.b ? speedScale : 0.0);   // back right
            } else {
                drive.drive(
                        -gamepad1.left_stick_y,   // Note: pushing stick forward gives negative value
                        gamepad1.left_stick_x,
                        gamepad1.right_stick_x,
                        speedScale);
            }

            // ---------------- INTAKE ----------------
            // LB (out) wins if both are pressed, as in the Everybot code.
            if (gamepad1.left_bumper) {
                robot.intake.out();
            } else if (gamepad1.left_trigger > TRIGGER_THRESHOLD) {
                robot.intake.in();
            } else {
                robot.intake.stop();
            }

            // ---------------- CATAPULT ----------------
            // RT (down) wins over RB (launch). Neither: HOLD once used, IDLE (no power) before first use.
            robot.catapult.manual(gamepad1.right_bumper, gamepad1.right_trigger > TRIGGER_THRESHOLD);

            // ---------------- FLOWER ARMS ----------------
            // Each press of A moves one stage out, each press of B one stage in (UP <-> BLOCK <-> FLAT).
            // Ignored during motor test, where A/B run wheels.
            boolean flowerOutButton = gamepad1.a;
            boolean flowerInButton = gamepad1.b;
            if (!motorTest) {
                if (flowerOutButton && !lastFlowerOutButton) {
                    robot.flowerArms.stepOut();
                } else if (flowerInButton && !lastFlowerInButton) {
                    robot.flowerArms.stepIn();
                }
            }
            lastFlowerOutButton = flowerOutButton;
            lastFlowerInButton = flowerInButton;

            robot.update();

            // ---------------- TELEMETRY ----------------
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Speed", motorTest ? "MOTOR TEST (X=FL A=BL Y=FR B=BR)"
                                                 : slowMode ? "SLOW" : "FULL");
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", drive.getFrontLeftPower(), drive.getFrontRightPower());
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", drive.getBackLeftPower(), drive.getBackRightPower());
            robot.addTelemetry(telemetry);
            telemetry.update();
        }
    }
}
