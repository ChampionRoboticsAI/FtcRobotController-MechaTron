package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorController;

/*
 * Bench test for the three goBILDA 4-Bar Odometry Pods on the Control Hub encoder ports 0, 3 and 2.
 * Hardware is described in ROBOT_CONFIGURATION.md.
 *
 * Encoders are read directly from the hub by port number, so this OpMode does not depend on
 * the motor names in the robot configuration. Only the hub name must match. It never sends
 * power to a motor, so it is safe to run with the robot on the bench.
 *
 * To test: roll one pod's wheel by hand and check that only that pod's counts change.
 * One full turn of the wheel should read about 2000 counts (about 100.5 mm).
 *
 * Gamepad1:  X = zero pod A (port 0)   B = zero pod B (port 3)   Y = zero pod C (port 2)
 *            A = zero all pods
 */

@TeleOp(name="🔧 goBILDA Odometry Pod Test", group="Test") // wrench emoji
public class SensorGoBildaOdometryPodTest extends LinearOpMode {

    // Hub name from the robot configuration ("Control Hub" is the default).
    static final String HUB_NAME = "Control Hub";

    static final int POD_A_PORT = 0;
    static final int POD_B_PORT = 3;
    static final int POD_C_PORT = 2;

    // goBILDA 4-Bar Odometry Pod: 2000 counts per revolution of a 32 mm wheel.
    static final double COUNTS_PER_REV = 2000.0;
    static final double WHEEL_DIAMETER_MM = 32.0;
    static final double MM_PER_COUNT = (Math.PI * WHEEL_DIAMETER_MM) / COUNTS_PER_REV;
    static final double MM_PER_INCH = 25.4;

    @Override
    public void runOpMode() {
        // tryGet returns null instead of throwing when the name is not in the configuration.
        DcMotorController hub = hardwareMap.tryGet(DcMotorController.class, HUB_NAME);
        if (hub == null) {
            telemetry.addData("ERROR", "No hub named \"%s\" in the active configuration", HUB_NAME);
            telemetry.addData("Hubs found", hardwareMap.getAllNames(DcMotorController.class));
            telemetry.update();
            waitForStart();
            return;
        }

        telemetry.addData("Status", "Initialized. Press START, then roll each pod by hand.");
        telemetry.update();
        waitForStart();

        // Counts at the last zero. Zeroing here leaves the hub's own encoder count untouched.
        int podAZero = hub.getMotorCurrentPosition(POD_A_PORT);
        int podBZero = hub.getMotorCurrentPosition(POD_B_PORT);
        int podCZero = hub.getMotorCurrentPosition(POD_C_PORT);

        while (opModeIsActive()) {
            int podARaw = hub.getMotorCurrentPosition(POD_A_PORT);
            int podBRaw = hub.getMotorCurrentPosition(POD_B_PORT);
            int podCRaw = hub.getMotorCurrentPosition(POD_C_PORT);

            if (gamepad1.x || gamepad1.a) podAZero = podARaw;
            if (gamepad1.b || gamepad1.a) podBZero = podBRaw;
            if (gamepad1.y || gamepad1.a) podCZero = podCRaw;

            addPodTelemetry("Pod A (port " + POD_A_PORT + ")", podARaw, podARaw - podAZero);
            addPodTelemetry("Pod B (port " + POD_B_PORT + ")", podBRaw, podBRaw - podBZero);
            addPodTelemetry("Pod C (port " + POD_C_PORT + ")", podCRaw, podCRaw - podCZero);
            telemetry.addLine("X = zero A | B = zero B | Y = zero C | A = zero all");
            telemetry.update();
        }
    }

    private void addPodTelemetry(String label, int raw, int counts) {
        double mm = counts * MM_PER_COUNT;
        telemetry.addData(label + " counts", "%d (raw %d)", counts, raw);
        telemetry.addData(label + " distance", "%.1f mm, %.2f in, %.2f rev",
                mm, mm / MM_PER_INCH, counts / COUNTS_PER_REV);
    }
}
