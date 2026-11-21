package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

/**
 * Scenario 3 drive paths in field coordinates (inches, degrees, from the Pedro Visualizer: Scenario3.pp.json).
 * When mirrored, every pose is flipped across the field centerline: x -> 144 - x, heading -> 180 - heading.
 *
 * To update from a new Visualizer export: copy only the numbers into the pose(...) lines below.
 * Do NOT paste over the whole file, or the package line and mirroring are lost and the build breaks.
 */
public class Scenario3Path {

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

    public Scenario3Path(boolean mirrored) {
        this.mirrored = mirrored;
        start          = pose(58.5, 9.2, 90);
        Pose point1Raw = pose(9.3316, 46.8149, 0); // heading is calculated below
        point1Control1 = pose(58.4679, 47.0079, 0);
        point2         = pose(59.921, 132.1912, -89.32);
        point2Control1 = pose(22.555, 119.1933, 0);
        point2Control2 = pose(60.231, 106.0672, 0);
        point3         = pose(9.9947, 111.1698, -177.6194);
        point3Control1 = pose(36.2714, 112.2622, 0);

        // Path2 drives in reverse (back of the robot leading). Make Path1 END facing exactly the way
        // Path2 STARTS, so the robot does not turn when switching paths. That heading points from
        // Path2's first control point back to point1 (the curve's starting direction, flipped).
        point1 = point1Raw.withHeading(Math.atan2(
                point1Raw.y() - point2Control1.y(),
                point1Raw.x() - point2Control1.x()));
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

    /** Turns gradually from the start heading to the heading Path2 needs (shortest way round). */
    public Path path1() {
        return Paths.curve(start, point1Control1, point1).linear(start, point1);
    }

    /** Back of the robot leads along the curve. */
    public Path path2() {
        return Paths.curve(point1, point2Control1, point2Control2, point2).reverseTangent();
    }

    /** Holds point3's heading the whole way. */
    public Path path3() {
        return Paths.curve(point2, point3Control1, point3).constant(point3);
    }
}
