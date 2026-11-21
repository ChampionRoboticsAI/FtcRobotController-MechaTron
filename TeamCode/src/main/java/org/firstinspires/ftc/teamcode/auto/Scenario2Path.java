package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

/**
 * Scenario 2 drive path in field coordinates (inches, degrees, from the Pedro Visualizer: Scenario2.pp.json).
 * When mirrored, every pose is flipped across the field centerline: x -> 144 - x, heading -> 180 - heading.
 *
 * To update from a new Visualizer export: copy only the numbers into the pose(...) lines below.
 * Do NOT paste over the whole file, or the package line and mirroring are lost and the build breaks.
 */
public class Scenario2Path {

    private static final double FIELD_SIZE = 144.0;

    private final PoseFactory poseFactory = PoseFactory.degrees();
    private final boolean mirrored;

    private final Pose start;
    private final Pose point1;
    private final Pose point1Control1;

    public Scenario2Path(boolean mirrored) {
        this.mirrored = mirrored;
        start          = pose(83, 9.2, 90);
        point1         = pose(131.5901, 27.5001, 10.3288);
        point1Control1 = pose(81.2614, 18.3276, 0);
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

    /** Front of the robot leads along the curve. */
    public Path path1() {
        return Paths.curve(start, point1Control1, point1).tangent();
    }
}
