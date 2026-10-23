# Gamepad Mapping

Controls for the **Robot: Teleop Mecanum Drive** OpMode (`RobotTeleopMecanumDrive.java`).
Hardware and port details are in [ROBOT_CONFIGURATION.md](ROBOT_CONFIGURATION.md).

All controls are on **gamepad1**, a Logitech F310 with the switch on the back set to **X**.
Gamepad2 is not used yet.

## Driving

| Input | Action |
|---|---|
| Left stick up / down | Drive forward / backward |
| Left stick left / right | Strafe left / right |
| Right stick left / right | Turn left / right |
| LB (hold) | Slow mode: driving at 40% power |

Driving is robot-centric: "forward" is whichever way the robot is facing.
All three stick motions can be combined (for example, drive diagonally while turning).

## Mechanisms

| Input | Intake | Midtake | Shooter |
|---|---|---|---|
| RT (push) | Forward, speed follows trigger | Forward, speed follows trigger | — |
| A (hold) | Forward, full power | — | — |
| RB (hold) | **Reverse**, full power | — | — |
| B (hold) | — | — | Forward, full power |

- All mechanism controls run only while held, and stop when released.
- The Intake and Midtake brake to a stop. The Shooter coasts to a stop.

## Combinations

| Combination | Result |
|---|---|
| A + RT | Intake at full power; Midtake follows RT |
| RB + A, RB + RT | RB wins: Intake runs in reverse; Midtake still follows RT |
| B + RT | Shooter runs while Intake and Midtake feed |
| LB + any mechanism | Only driving slows down; mechanism speeds are unchanged |
| Any mechanism + sticks | Driving and mechanisms work at the same time |

## Motor Test Mode

Hold **BACK** to enter motor test mode. Lift the robot so the wheels are off the ground first.

| Combination | Runs |
|---|---|
| BACK + X | Front Left wheel |
| BACK + A | Back Left wheel |
| BACK + Y | Front Right wheel |
| BACK + B | Back Right wheel |

- Each wheel runs at 40% power and should spin in the direction that drives the robot forward.
- While BACK is held, the sticks do not drive, and **A** and **B** do not run the Intake or Shooter.
- RT and RB still work in motor test mode.

## Telemetry

The Driver Hub shows:

- **Status**: run time
- **Speed**: `FULL`, `SLOW`, or `MOTOR TEST`
- **Front left/Right**, **Back left/Right**: power sent to each wheel
- **Intake/Midtake**: power sent to each motor (negative means reverse)
- **Shooter**: power sent to the shooter

## Free Buttons

Not yet mapped on gamepad1: **X**, **Y**, **LT**, **D-pad**, **START**, and the stick buttons.
