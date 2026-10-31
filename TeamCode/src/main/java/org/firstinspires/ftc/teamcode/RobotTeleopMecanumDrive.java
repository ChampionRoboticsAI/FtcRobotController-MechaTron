package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * Robot-centric TeleOp for the goBILDA StarterBot with Mecanum wheels.
 * Hardware is described in ROBOT_CONFIGURATION.md:
 *
 *   Control Hub   motor port 1: "Back Left"      motor port 2: "Front Left"
 *   Expansion Hub motor port 1: "Back Right"     motor port 2: "Front Right"
 *   Expansion Hub motor port 0: "Intake"         motor port 3: "Midtake"
 *   Control Hub   motor port 0: "Shooter"
 *
 * Driven with gamepad1 (Logitech F310, switch on the back set to "X"):
 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 * 4) Slow mode: hold Left Bumper to drive at reduced speed for precise positioning.
 * 5) Motor test: hold Back and press X (front left), A (back left), Y (front right),
 *    B (back right) to run one wheel at a time.
 * 6) Intake + Midtake: push Right Trigger (RT); motor speed follows how far it is pushed.
 * 7) Intake only: hold A to run the Intake at full power.
 *    Hold Right Bumper (RB) to run the Intake in reverse (overrides A and RT).
 * 8) Shooter: hold B to run the Shooter at full power; it coasts to a stop when released.
 *
 * If the robot rotates when strafing but drives and turns correctly, either the Mecanum
 * rollers do not form an "X" when viewed from above (swap the wheels), or the front and
 * back motors on one side are swapped (fix the names in the robot configuration).
 *
 * The left-side motors are reversed so that positive power drives the robot forward.
 * When you first test the robot, if any wheel spins backward when the left stick is pushed
 * forward, flip that motor's direction below.
 */

@TeleOp(name="Robot: Teleop Mecanum Drive", group="Robot")
public class RobotTeleopMecanumDrive extends LinearOpMode {

    // Fraction of full power used while the slow-mode button is held.
    static final double SLOW_MODE_SCALE = 0.4;

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotor intake = null;
    private DcMotor midtake = null;
    private DcMotor shooter = null;

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

        // If either roller pushes game pieces out instead of pulling them in, flip its direction.
        intake = hardwareMap.get(DcMotor.class, "Intake");
        midtake = hardwareMap.get(DcMotor.class, "Midtake");
        intake.setDirection(DcMotor.Direction.REVERSE);
        midtake.setDirection(DcMotor.Direction.FORWARD);
        for (DcMotor motor : new DcMotor[] {intake, midtake}) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        // FLOAT lets the flywheel spin down on its own instead of braking hard.
        shooter = hardwareMap.get(DcMotor.class, "Shooter");
        shooter.setDirection(DcMotor.Direction.FORWARD);
        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
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
            // Used to verify each configured name drives the wheel at that corner of the robot,
            // and that it spins in the direction that pushes the robot forward.
            boolean motorTest = gamepad1.back;
            if (motorTest) {
                frontLeftPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
                backLeftPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
                frontRightPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
                backRightPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad
            }

            double speedScale = (gamepad1.left_bumper || motorTest) ? SLOW_MODE_SCALE : 1.0;

            frontLeftDrive.setPower(frontLeftPower * speedScale);
            frontRightDrive.setPower(frontRightPower * speedScale);
            backLeftDrive.setPower(backLeftPower * speedScale);
            backRightDrive.setPower(backRightPower * speedScale);

            // Right trigger reads 0.0 (released) to 1.0 (fully pushed).
            double midtakePower = gamepad1.right_trigger;
            // Holding A runs only the Intake at full power (ignored during motor test, where A is a wheel).
            double intakePower = (gamepad1.a && !motorTest) ? 1.0 : midtakePower;
            // Holding RB reverses the Intake at full power to spit game pieces back out.
            if (gamepad1.right_bumper) {
                intakePower = -1.0;
            }
            intake.setPower(intakePower);
            midtake.setPower(midtakePower);

            // Holding B runs the Shooter (ignored during motor test, where B is a wheel).
            double shooterPower = (gamepad1.b && !motorTest) ? 1.0 : 0.0;
            shooter.setPower(shooterPower);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Speed", motorTest ? "MOTOR TEST (X=FL A=BL Y=FR B=BR)"
                                                 : speedScale < 1.0 ? "SLOW" : "FULL");
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower * speedScale, frontRightPower * speedScale);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower * speedScale, backRightPower * speedScale);
            telemetry.addData("Intake/Midtake", "%4.2f, %4.2f", intakePower, midtakePower);
            telemetry.addData("Shooter", "%4.2f", shooterPower);
            telemetry.update();
        }
    }
}
