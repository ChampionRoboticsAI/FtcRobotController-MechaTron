# Autonomous Game plan

## Scenario 1
Here are the steps for scenario 1 autonoous game plan

1.  Launch catapult and pull catapult arm down
2.  Run intake motors in revers for 5 seconds so the flower hood can pop up. Then run intake continuously forward direction
3.  Move the flower arm down 90 degrees
4.  Run Path1 from Scenario1Path.java 
5.  Run Path2 from Scenario1Path.java 
6.  Move the flower arm up 90 degrees
7.  Launch catapult and pull catapult arm down
8.  Run Path3 from Scenario1Path.java 

## Implementation

| Driver Station name | File | Use |
|---|---|---|
| BioBuzz: Scenario 1 | `auto/Scenario1Auto.java` | Scenario 1 exactly as written above |
| BioBuzz: Scenario 1 (Mirrored) | `auto/Scenario1MirroredAuto.java` | Same route from the other alliance side (paths flipped across the field centerline x = 72) |

Path1, Path2 and Path3 are in `auto/Scenario1Path.java` (Pedro Visualizer field coordinates: inches on the 144 x 144 field, degrees). To change the route, redraw it in the Visualizer and paste the new numbers into the poses there.

**Start position:** Scenario 1 at x 59.1, y 6.5, facing 90°. Mirrored at x 84.9, y 6.5, facing 90°. The INIT telemetry shows the pose the robot thinks it is at.

Both pre-select **BioBuzz: TeleOp** for the driver period. Enable the 30 s AUTO timer on the Driver Station.

### Before the first run (tuning)

1. **Pedro tuning must be finished.** The Foresight values in `pedro/Constants.java` are marked STALE; re-run the Foresight Tuner first, then the Line/Curve/Hold tests.
2. **Catapult launch timing** (`subsystems/Catapult.java`): `LAUNCH_SECONDS` (arm reaches the top) and `PULL_DOWN_SECONDS` (arm back down). Same timing is used in both launch steps.
3. **Scenario 1 knobs** (`auto/Scenario1Auto.java`): `HOOD_POP_SECONDS` (5 s), `FLOWER_ARM_SETTLE_SECONDS`, and `PATH1_SPEED` / `PATH2_SPEED` / `PATH3_SPEED` (top speed in inches/second; lower one if the robot overshoots or pushes Pollen away).
4. Test the route with the catapult and intake running at slow speed first, then full speed. Telemetry shows the current step, time, measured pose and target pose.

### Time budget (30 s AUTO)

Steps 1-3 take about 7 s (5 s is the hood pop), leaving about 22 s for the three paths (roughly 250 in of curved driving) and the second launch. This is tight. If the robot runs out of time, the routine stops itself at 29.5 s (no powered movement into the TeleOp transition, rule G403). Fastest wins: overlap the hood pop with the first launch, or shorten it.

### Adding a new scenario

1. Create `auto/ScenarioNAuto.java` extending `BioBuzzAutoBase`, with `@Autonomous(name = "BioBuzz: Scenario N", group = "BioBuzz", preselectTeleOp = "BioBuzz: TeleOp")`.
2. Write the steps in `runRoutine()` using: `step("name")`, `forward(in)`, `back(in)`, `strafeLeft(in)`, `strafeRight(in)`, `move(forwardIn, leftIn, maxSpeed)`, `turnLeft(deg)`, `turnRight(deg)`, `driveTo(pose, maxSpeed)`, `followPath(path)` / `followPath(path, maxSpeed)` for Visualizer paths, `launchCatapult()`, `waitSeconds(s)`, `waitUntil(condition, timeout)`, and the mechanisms on `robot` (`robot.intake`, `robot.catapult`, `robot.flowerArms`).
3. For the other side of the field, add a subclass that overrides `mirrored()` to return `true`. Relative moves (strafes and turns) are mirrored automatically. Field paths are not: build them with `PoseFactory.degrees().mirrorX(72)` when mirrored, as `Scenario1Path` does.

Every step waits until it finishes while the robot keeps holding its position and the catapult keeps its hold power. Moves are robot-relative: "forward" is the way the robot faces at that moment.
