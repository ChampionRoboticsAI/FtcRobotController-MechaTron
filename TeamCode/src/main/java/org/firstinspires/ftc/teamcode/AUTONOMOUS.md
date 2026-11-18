# Autonomous Game plan

## Scenario 1
Here are the steps for scenario 1 autonoous game plan

1.  Launch catapult and pull catapult arm down
2.  Run intake motors in revers for 5 seconds so the flower hood can pop up. Then run intake continuously forward direction
3.  Move the flower arm down 90 degrees
4.  Drive straight 24 inches, left 24 inches, right 12 inches and left 36 inches. This step will allow pollens in through the intake
5. Go back 36 inches, turn right, go straight 60 inches, turn right, go straight 24 inches, turn right, go reverse 24 inches
6.  Launch catapult and pull catapult arm down
7. Go straight 24 inches, turn right and go forward 40 inches.

## Implementation

| Driver Station name | File | Use |
|---|---|---|
| BioBuzz: Scenario 1 | `auto/Scenario1Auto.java` | Scenario 1 exactly as written above |
| BioBuzz: Scenario 1 (Mirrored) | `auto/Scenario1MirroredAuto.java` | Same route from the other side of the field (every left/right swapped) |

Both pre-select **BioBuzz: TeleOp** for the driver period. Enable the 30 s AUTO timer on the Driver Station.

### Before the first run (tuning)

1. **Pedro tuning must be finished.** The Foresight values in `pedro/Constants.java` are marked STALE; re-run the Foresight Tuner first, then the Line/Curve/Hold tests.
2. **Catapult launch timing** (`subsystems/Catapult.java`): `LAUNCH_SECONDS` (arm reaches the top) and `PULL_DOWN_SECONDS` (arm back down). Same timing is used in both launch steps.
3. **Scenario 1 knobs** (`auto/Scenario1Auto.java`): `HOOD_POP_SECONDS` (5 s), `FLOWER_ARM_SETTLE_SECONDS`, `SWEEP_SPEED` (slow the step 4 sweep if Pollen is pushed away).
4. Test the route with the catapult and intake running at slow speed first, then full speed. Telemetry shows the current step, time, measured pose and target pose.

### Time budget (30 s AUTO)

Steps 1-3 take about 7 s (5 s is the hood pop), leaving about 22 s for ~300 in of driving, 4 turns and the second launch. This is tight. If the robot runs out of time, the routine stops itself at 29.5 s (no powered movement into the TeleOp transition, rule G403). Fastest wins: overlap the hood pop with the first launch, or shorten it.

### Adding a new scenario

1. Create `auto/ScenarioNAuto.java` extending `BioBuzzAutoBase`, with `@Autonomous(name = "BioBuzz: Scenario N", group = "BioBuzz", preselectTeleOp = "BioBuzz: TeleOp")`.
2. Write the steps in `runRoutine()` using: `step("name")`, `forward(in)`, `back(in)`, `strafeLeft(in)`, `strafeRight(in)`, `move(forwardIn, leftIn, maxSpeed)`, `turnLeft(deg)`, `turnRight(deg)`, `driveTo(pose, maxSpeed)`, `launchCatapult()`, `waitSeconds(s)`, `waitUntil(condition, timeout)`, and the mechanisms on `robot` (`robot.intake`, `robot.catapult`, `robot.flowerArms`).
3. For the other side of the field, add a subclass that overrides `mirrored()` to return `true`.

Every step waits until it finishes while the robot keeps holding its position and the catapult keeps its hold power. Moves are robot-relative: "forward" is the way the robot faces at that moment.
