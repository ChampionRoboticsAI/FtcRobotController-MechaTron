package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Robot-centric mecanum drive for TeleOp (Control Hub motor ports 0-3).
 * Autonomous does NOT use this class: Pedro Pathing's Follower drives the same motors (see pedro/Constants.java).
 *
 * The drive motors run WITHOUT encoders: three of their encoder ports are used by the odometry pods.
 */
public class MecanumDrive {

    private final DcMotor frontLeft;
    private final DcMotor backLeft;
    private final DcMotor frontRight;
    private final DcMotor backRight;

    private double frontLeftPower, frontRightPower, backLeftPower, backRightPower;

    public MecanumDrive(HardwareMap hardwareMap) {
        // These names must match the names in the robot's active configuration file.
        frontLeft = hardwareMap.get(DcMotor.class, "Front Left");
        backLeft = hardwareMap.get(DcMotor.class, "Back Left");
        frontRight = hardwareMap.get(DcMotor.class, "Front Right");
        backRight = hardwareMap.get(DcMotor.class, "Back Right");

        // Keep these in sync with drivetrainConfig in pedro/Constants.java.
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // Drive purely on joystick power, and hold position when the sticks are released.
        for (DcMotor motor : new DcMotor[] {frontLeft, backLeft, frontRight, backRight}) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    /**
     * Robot-centric drive.
     * @param axial   forward (+) / back (-)
     * @param lateral strafe right (+) / left (-)
     * @param yaw     turn right (+) / left (-)
     * @param scale   overall power multiplier (e.g. slow mode)
     */
    public void drive(double axial, double lateral, double yaw, double scale) {
        double fl = axial + lateral + yaw;
        double fr = axial - lateral - yaw;
        double bl = axial - lateral + yaw;
        double br = axial + lateral - yaw;

        // Normalize so no wheel power exceeds 100%, preserving the requested motion.
        double max = Math.max(Math.max(Math.abs(fl), Math.abs(fr)), Math.max(Math.abs(bl), Math.abs(br)));
        if (max > 1.0) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }
        setWheelPowers(fl * scale, fr * scale, bl * scale, br * scale);
    }

    /** Raw per-wheel powers (used by motor test mode). */
    public void setWheelPowers(double fl, double fr, double bl, double br) {
        frontLeftPower = fl;
        frontRightPower = fr;
        backLeftPower = bl;
        backRightPower = br;
        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);
    }

    public void stop() {
        setWheelPowers(0, 0, 0, 0);
    }

    public double getFrontLeftPower()  { return frontLeftPower; }
    public double getFrontRightPower() { return frontRightPower; }
    public double getBackLeftPower()   { return backLeftPower; }
    public double getBackRightPower()  { return backRightPower; }
}
