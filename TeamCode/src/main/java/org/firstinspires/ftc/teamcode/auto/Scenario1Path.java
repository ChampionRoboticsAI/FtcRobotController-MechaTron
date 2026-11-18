package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

/**
 * Scenario 1 drive paths in field coordinates (inches, from the Pedro Visualizer export
 * MechaTronScenario1.pp.json). When mirrored, every pose is flipped across the field centerline:
 * x -> 144 - x, heading -> 180 - heading.
 */
public class Scenario1Path {

    private static final double FIELD_SIZE = 144.0;

    private final PoseFactory poseFactory = PoseFactory.degrees();
    private final boolean mirrored;

    private final Pose start;
    private final Pose point1;
    private final Pose point1Control1;
    private final Pose point2;
    private final Pose point2Control1;
    private final Pose point2Control2;
    private final Pose point3;
    private final Pose point3Control1;

    public Scenario1Path(boolean mirrored) {
        this.mirrored = mirrored;
        start          = pose(59.0601, 6.5311, 90);
        point1         = pose(4.7497, 46.6801, 180);
        point1Control1 = pose(58.0619, 43.8567, 0);
        point2         = pose(59.9461, 140.1454, -86.7121);
        point2Control1 = pose(20.963, 115.848, 0);
        point2Control2 = pose(61.7856, 108.1248, 0);
        point3         = pose(4.6042, 107.8007, 0);
        point3Control1 = pose(36.2714, 112.2622, 0);
    }

    /** Builds a pose from Visualizer values (degrees), mirrored if this is the mirrored side. */
    private Pose pose(double x, double y, double headingDegrees) {
        if (mirrored) {
            x = FIELD_SIZE - x;
            headingDegrees = 180 - headingDegrees;
        }
        return poseFactory.of(x, y, headingDegrees);
    }

    public Pose start() {
        return start;
    }

    public Path path1() {
        return Paths.curve(start, point1Control1, point1).linear(start, point1);
    }

    public Path path2() {
        return Paths.curve(point1, point2Control1, point2Control2, point2).reverseTangent();
    }

    public Path path3() {
        return Paths.curve(point2, point3Control1, point3).constant(point3);
    }
}
