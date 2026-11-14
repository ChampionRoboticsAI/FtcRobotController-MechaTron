package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.Range;

/**
 * The left and right flower arms (REV Smart Robot Servo V2 in Standard mode, Control Hub servo ports 0 and 1).
 *
 * The arms have three stages, measured outward from straight up:
 *   UP    -   0 deg  arm vertical (TeleOp start position)
 *   BLOCK -  45 deg  blocks Nectar balls from the intake, lets Pollen through
 *   FLAT  -  90 deg  arm flat on the field, for pulling Pollen out of the Flower section
 *
 * Only the UP positions need calibrating (use the "BioBuzz: Flower Arm Calibration" OpMode).
 * The other stages are computed from them using the servo's 270 deg range.
 */
public class FlowerArms {

    public enum Stage {
        UP(0.0), BLOCK(45.0), FLAT(90.0);

        public final double degreesOut;

        Stage(double degreesOut) {
            this.degreesOut = degreesOut;
        }
    }

    // Servo position (0..1) at which each arm points straight up.
    // TODO: starting guesses - replace with the values from "BioBuzz: Flower Arm Calibration".
    // Each arm needs its own value because the servo horns rarely sit on the same spline tooth.
    public static final double LEFT_UP_POSITION  = 0.20;
    public static final double RIGHT_UP_POSITION = 0.20;

    // With these directions, increasing position must move BOTH arms outward (away from the robot).
    // If one arm folds into the robot when stepping out, flip its direction here.
    public static final Servo.Direction LEFT_DIRECTION  = Servo.Direction.FORWARD;
    public static final Servo.Direction RIGHT_DIRECTION = Servo.Direction.REVERSE;

    // REV Smart Robot Servo: 270 deg of travel over a 500-2500 us pulse. The SDK default pulse range
    // (600-2400 us) would only use about 243 deg, so widen it to make 0..1 cover the full 270 deg.
    public static final double SERVO_RANGE_DEGREES = 270.0;
    private static final PwmControl.PwmRange SERVO_PWM_RANGE = new PwmControl.PwmRange(500, 2500);

    private final ServoImplEx left;
    private final ServoImplEx right;
    private Stage stage = Stage.UP;

    public FlowerArms(HardwareMap hardwareMap) {
        left = hardwareMap.get(ServoImplEx.class, "Left Flower Arm");
        right = hardwareMap.get(ServoImplEx.class, "Right Flower Arm");
        left.setPwmRange(SERVO_PWM_RANGE);
        right.setPwmRange(SERVO_PWM_RANGE);
        left.setDirection(LEFT_DIRECTION);
        right.setDirection(RIGHT_DIRECTION);
        // No position is sent here, so the arms stay still (unpowered) until setStage() is called.
    }

    /** Moves both arms to the given stage. */
    public void setStage(Stage newStage) {
        stage = newStage;
        setPositions(positionFor(LEFT_UP_POSITION, newStage), positionFor(RIGHT_UP_POSITION, newStage));
    }

    /** UP -> BLOCK -> FLAT. Does nothing if already FLAT. */
    public void stepOut() {
        if (stage.ordinal() < Stage.values().length - 1) {
            setStage(Stage.values()[stage.ordinal() + 1]);
        }
    }

    /** FLAT -> BLOCK -> UP. Does nothing if already UP. */
    public void stepIn() {
        if (stage.ordinal() > 0) {
            setStage(Stage.values()[stage.ordinal() - 1]);
        }
    }

    public Stage getStage() {
        return stage;
    }

    /** Raw servo positions, for calibration. */
    public void setPositions(double leftPosition, double rightPosition) {
        left.setPosition(Range.clip(leftPosition, 0.0, 1.0));
        right.setPosition(Range.clip(rightPosition, 0.0, 1.0));
    }

    public double getLeftPosition() {
        return left.getPosition();
    }

    public double getRightPosition() {
        return right.getPosition();
    }

    /** Servo position for an arm whose straight-up position is {@code upPosition}. */
    public static double positionFor(double upPosition, Stage stage) {
        return Range.clip(upPosition + stage.degreesOut / SERVO_RANGE_DEGREES, 0.0, 1.0);
    }
}
