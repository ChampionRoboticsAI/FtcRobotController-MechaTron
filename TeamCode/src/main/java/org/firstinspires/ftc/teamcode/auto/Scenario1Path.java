package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

/**
 * Scenario 1 drive path in field coordinates (inches, degrees, from the Pedro Visualizer: Scenario1.pp.json).
 * When mirrored, every pose is flipped across the field centerline: x -> 144 - x, heading -> 180 - heading.
 *
 * To update from a new Visualizer export: copy only the numbers into the pose(...) lines below.
 * Do NOT paste over the whole file, or the package line and mirroring are lost and the build breaks.
 */
public class Scenario1Path {

    private static final double FIELD_SIZE = 144.0;

    private final PoseFactory poseFactory = PoseFactory.degrees();
    private final boolean mirrored;

    private final Pose start;
    private final Pose point1;
    private final Pose point1Control1;
    private final Pose point1Control2;
    private final Pose point1Control3;

    public Scenario1Path(boolean mirrored) {
        this.mirrored = mirrored;
        start          = pose(58.5, 9.2, 90);
        point1         = pose(11.9313, 94.4767, 91.2452);
        point1Control1 = pose(58.3441, 33.3902, 0);
        point1Control2 = pose(4.0853, 64.9315, 0);
        point1Control3 = pose(13.2463, 33.9823, 0);
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
        return Paths.curve(start, point1Control1, point1Control2, point1Control3, point1).tangent();
    }
}
