package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * The intake roller (Expansion Hub motor port 3, configuration name "Intake").
 * Forward (positive power) pulls game pieces IN.
 */
public class Intake {

    // Intake powers (from Everybot code).
    public static final double IN_POWER  =  1.0;
    public static final double OUT_POWER = -0.9;

    private final DcMotor motor;
    private double power = 0.0;

    public Intake(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "Intake");
        // Forward should pull game pieces IN. Flip if the intake spits them out.
        motor.setDirection(DcMotor.Direction.FORWARD);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /** Pull game pieces in. */
    public void in() {
        setPower(IN_POWER);
    }

    /** Push game pieces out (also pops the flower hood up). */
    public void out() {
        setPower(OUT_POWER);
    }

    public void stop() {
        setPower(0.0);
    }

    public void setPower(double newPower) {
        power = newPower;
        motor.setPower(newPower);
    }

    public double getPower() {
        return power;
    }
}
