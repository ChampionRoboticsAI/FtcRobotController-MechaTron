package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.Catapult;
import org.firstinspires.ftc.teamcode.subsystems.FlowerArms;
import org.firstinspires.ftc.teamcode.subsystems.Intake;

/**
 * All of the robot's mechanisms in one place, shared by every TeleOp and Autonomous OpMode.
 *
 * The drivetrain is not included because TeleOp and Autonomous drive it differently:
 *   TeleOp     -> subsystems.MecanumDrive (joystick power)
 *   Autonomous -> Pedro Pathing Follower (pedro/Constants.create)
 *
 * Constructing a Robot does not move anything, so it is safe to call during INIT
 * (BIOBUZZ G304.H: robots must be motionless after OpMode initialization).
 *
 * Adding a mechanism: create a class in subsystems/, add a field here, and call it from update()/stop()/addTelemetry().
 */
public class Robot {

    public final Intake intake;
    public final Catapult catapult;
    public final FlowerArms flowerArms;

    public Robot(HardwareMap hardwareMap) {
        intake = new Intake(hardwareMap);
        catapult = new Catapult(hardwareMap);
        flowerArms = new FlowerArms(hardwareMap);
    }

    /** Runs background mechanism logic (e.g. the catapult launch sequence). Call once per loop. */
    public void update() {
        catapult.update();
    }

    /** Stops every powered mechanism. Flower arm servos keep their position. */
    public void stop() {
        intake.stop();
        catapult.stop();
    }

    public void addTelemetry(Telemetry telemetry) {
        telemetry.addData("Intake", "%4.2f", intake.getPower());
        telemetry.addData("Catapult", "%s (%4.2f)%s", catapult.getMode(), catapult.getPower(),
                catapult.isBusy() ? " launching" : "");
        telemetry.addData("Flower Arms", "%s (%d deg) L=%4.3f R=%4.3f",
                flowerArms.getStage(), (int) flowerArms.getStage().degreesOut,
                flowerArms.getLeftPosition(), flowerArms.getRightPosition());
    }
}
