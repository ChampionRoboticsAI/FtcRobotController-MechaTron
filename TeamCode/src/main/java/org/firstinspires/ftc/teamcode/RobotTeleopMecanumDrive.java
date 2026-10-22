package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * Robot-centric TeleOp for the goBILDA StarterBot with Mecanum wheels.
 * Hardware is described in ROBOT_CONFIGURATION.md:
 *
 *   Control Hub   motor port 1: "Front Right"    motor port 2: "Back Right"
 *   Expansion Hub motor port 1: "Front Left"     motor port 2: "Back Left"
 *
 * Driven with gamepad1 (Logitech F310, switch on the back set to "X"):
 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 * 4) Slow mode: hold Left Bumper to drive at reduced speed for precise positioning.
 * 5) Motor test: hold Back and press X (front left), A (back left), Y (front right),
 *    B (back right) to run one wheel at a time.
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

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Speed", motorTest ? "MOTOR TEST (X=FL A=BL Y=FR B=BR)"
                                                 : speedScale < 1.0 ? "SLOW" : "FULL");
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower * speedScale, frontRightPower * speedScale);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower * speedScale, backRightPower * speedScale);
            telemetry.update();
        }
    }
}
