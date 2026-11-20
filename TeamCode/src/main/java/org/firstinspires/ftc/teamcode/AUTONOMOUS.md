# Autonomous Game plan

## Scenario 1
Here are the steps for the scenario 1 autonomous game plan:

1. Launch the catapult, then pull the catapult arm down
2. Run the intake motors in reverse for 5 seconds so the flower hood can pop up. Then run the intake continuously in the forward direction
3. Run Path1 from `Scenario1Path.java`
4. Stop the intake

## Scenario 2
Here are the steps for the scenario 2 autonomous game plan:

1. Launch the catapult, then pull the catapult arm down
2. Run the intake motors in reverse for 5 seconds so the flower hood can pop up. Then run the intake continuously in the forward direction
3. Move the flower arms down 90 degrees
4. Run Path1 from `Scenario2Path.java`
5. Run Path2 from `Scenario2Path.java`
6. Move the flower arms up 90 degrees
7. Launch the catapult, then pull the catapult arm down
8. Run Path3 from `Scenario2Path.java`

In both scenarios, "launch, then pull down" is one call, `launchCatapult()`. It runs the catapult's timed sequence (launch, pull down, hold) and waits until the arm is back down before the next step starts.

## Implementation

| Driver Station name | File | Use |
|---|---|---|
| BioBuzz: Scenario 1 | `auto/Scenario1Auto.java` | Scenario 1 exactly as written above |
| BioBuzz: Scenario 1 (Mirrored) | `auto/Scenario1MirroredAuto.java` | Same route from the other alliance side (paths flipped across the field centerline x = 72) |
| BioBuzz: Scenario 2 | `auto/Scenario2Auto.java` | Scenario 2 exactly as written above |
| BioBuzz: Scenario 2 (Mirrored) | `auto/Scenario2MirroredAuto.java` | Same route from the other alliance side (paths flipped across the field centerline x = 72) |

All four pre-select **BioBuzz: TeleOp** for the driver period. Enable the 30 s AUTO timer on the Driver Station.

**Start position (both scenarios):** x 58.5, y 9.2, facing 90°. Mirrored at x 85.5, y 9.2, facing 90°. The INIT telemetry shows the pose the robot thinks it is at.

**Flower arms:** the arms stay limp during INIT. At START they are powered to UP (straight up), the same as TeleOp, so every later stage change moves from a known position.

### Paths

The paths are in Pedro Visualizer field coordinates (inches on the 144 x 144 field, degrees). The Visualizer exports are kept next to the code.

| Path | File | Visualizer export | Robot heading along the path |
|---|---|---|---|
| Scenario 1 Path1 | `auto/Scenario1Path.java` | `auto/Scenario1.pp.json` | Front of the robot leads (tangent) |
| Scenario 2 Path1 | `auto/Scenario2Path.java` | `auto/Scenario2.pp.json` | Turns gradually so it ends facing the way Path2 starts, so there is no turn when switching to Path2 |
| Scenario 2 Path2 | `auto/Scenario2Path.java` | `auto/Scenario2.pp.json` | Back of the robot leads (reverse tangent) |
| Scenario 2 Path3 | `auto/Scenario2Path.java` | `auto/Scenario2.pp.json` | Holds point3's heading (about -178°) the whole way |

**Changing a route:** redraw it in the Visualizer, then copy only the new numbers into the `pose(...)` lines of the matching `ScenarioNPath.java`. Do not paste the exported Java over the whole file: that removes the package line and the mirroring, and the build breaks. In `Scenario2Path`, the heading of point1 is calculated by the code, so the heading you set for it in the Visualizer is ignored.

### Before the first run (tuning)

1. **Pedro tuning must be finished.** The Foresight values in `pedro/Constants.java` are marked STALE; re-run the Foresight Tuner first, then the Line/Curve/Hold tests.
2. **Catapult launch timing** (`subsystems/Catapult.java`): `LAUNCH_SECONDS` (arm reaches the top) and `PULL_DOWN_SECONDS` (arm back down). The same timing is used for every launch step.
3. **Scenario knobs** (top of `auto/Scenario1Auto.java` and `auto/Scenario2Auto.java`): `HOOD_POP_SECONDS` (5 s), `FLOWER_ARM_SETTLE_SECONDS` (Scenario 2 only), and `PATH1_SPEED` / `PATH2_SPEED` / `PATH3_SPEED` (top speed in inches/second; lower one if the robot overshoots or pushes Pollen away).
4. Test the route with the catapult and intake running at slow speed first, then full speed. Telemetry shows the current step, time, measured pose and target pose.

### Time budget (30 s AUTO)

With the current timing, one launch takes about 1.4 s (`LAUNCH_SECONDS` 0.6 + `PULL_DOWN_SECONDS` 0.8) and the hood pop takes 5 s.

- **Scenario 1:** launch and hood pop take about 6.5 s, leaving about 23 s for Path1 (about 105 in). Plenty of time.
- **Scenario 2:** the mechanism steps take about 9 s (launch, hood pop, two arm moves of 0.5 s, second launch), leaving about 21 s for the three paths (about 230 in of curved driving). This is tight.

If the robot runs out of time, the routine stops itself at 29.5 s (no powered movement into the TeleOp transition, rule G403). The quickest saving is the 5 s hood pop: overlap it with the first launch, or shorten it.

### Adding a new scenario

1. Create `auto/ScenarioNAuto.java` extending `BioBuzzAutoBase`, with `@Autonomous(name = "BioBuzz: Scenario N", group = "BioBuzz", preselectTeleOp = "BioBuzz: TeleOp")`.
2. Put its paths in `auto/ScenarioNPath.java`, copying the structure of `Scenario1Path.java` (constructor takes `mirrored`, poses built with `pose(...)`, a `start()` method).
3. Write the steps in `runRoutine()` using: `step("name")`, `forward(in)`, `back(in)`, `strafeLeft(in)`, `strafeRight(in)`, `move(forwardIn, leftIn, maxSpeed)`, `turnLeft(deg)`, `turnRight(deg)`, `driveTo(pose, maxSpeed)`, `followPath(path)` / `followPath(path, maxSpeed)` for Visualizer paths, `launchCatapult()`, `waitSeconds(s)`, `waitUntil(condition, timeout)`, and the mechanisms on `robot` (`robot.intake`, `robot.catapult`, `robot.flowerArms`).
4. For the other side of the field, add a subclass that overrides `mirrored()` to return `true`. Relative moves (strafes and turns) are mirrored automatically. Field paths are mirrored by the `pose(x, y, heading)` helper in the path file, which flips x -> 144 - x and heading -> 180 - heading.

Every step waits until it finishes while the robot keeps holding its position and the catapult keeps its hold power. Relative moves are robot-relative: "forward" is the way the robot faces at that moment.
