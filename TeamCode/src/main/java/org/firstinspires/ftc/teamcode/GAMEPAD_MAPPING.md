# Gamepad Mapping

Controls for the **BioBuzz: TeleOp** OpMode (`BioBuzzTeleOp.java`), BioBuzz 2026-27 season.
Hardware and port details are in [ROBOT_CONFIGURATION.md](ROBOT_CONFIGURATION.md).
Mechanism controls are based on the [Everybot reference code](https://github.com/Robonauts-Everybot/FTC-Everybot-Code-2025-DECODE).

All controls are on **gamepad1**, a PlayStation DualSense (wired). Gamepad2 is not used yet.

## Quick Reference

| DualSense | SDK name | Action |
|---|---|---|
| Left stick | `left_stick_x/y` | Drive forward/back and strafe |
| Right stick (left/right) | `right_stick_x` | Turn |
| L3 (click left stick) | `left_stick_button` | Toggle slow mode |
| L2 (hold) | `left_trigger` | Intake **in** |
| L1 (hold) | `left_bumper` | Intake **out** |
| R1 (press once) | `right_bumper` | Catapult **launch** |
| R2 (hold) | `right_trigger` | Catapult **down** |
| Cross | `a` | Flower arms **out** |
| Circle | `b` | Flower arms **in** |
| Create (hold) + Square / Cross / Triangle / Circle | `back` + `x/a/y/b` | Motor test |

## Driving

| Input | Action |
|---|---|
| Left stick up / down | Drive forward / backward |
| Left stick left / right | Strafe left / right |
| Right stick left / right | Turn left / right |
| L3 (click left stick) | Toggle slow mode on/off (40% drive power) |

- Driving is robot-centric: "forward" is whichever way the robot is facing.
- All three stick motions can be combined (for example, drive diagonally while turning).
- Slow mode only affects driving; mechanism speeds are unchanged.

## Intake

| Input | Action |
|---|---|
| L2 (hold) | Intake **in** at 100% |
| L1 (hold) | Intake **out** at 90% |
| Neither | Intake stops (brakes) |

- If both are pressed, **L1 (out) wins**.

## Catapult

| Input | Action |
|---|---|
| R1 (press once) | **LAUNCH**: full power up for 0.3 s, even if R1 is released early |
| R2 (hold) | **DOWN** at 35% power, for a gentle return |
| Neither | **HOLD**: 20% power keeps the arm down while driving |

- **Start of TeleOp:** the catapult is **IDLE** (no power, braked) so it stays in its initial position.
  Holding power only begins after R1 or R2 is pressed for the first time.
- **One press = one shot.** Holding R1 does not extend the launch; release and press again for the next shot.
- **R2 always wins:** pressing R2 during a launch cancels it immediately (use it as an emergency stop).
- After a launch finishes, the hold power brings the arm back down.

## Flower Arms

| Input | Action |
|---|---|
| Cross (press) | Flower arms **OUT** |
| Circle (press) | Flower arms **IN** |

- One press is enough; the arms stay in position until the other button is pressed.
- Both arms move to **IN** when TeleOp starts.

## Motor Test Mode

Hold **Create** and lift the robot so the wheels are off the ground.

| Combination | Runs |
|---|---|
| Create + Square | Front Left wheel |
| Create + Cross | Back Left wheel |
| Create + Triangle | Front Right wheel |
| Create + Circle | Back Right wheel |

- Each wheel runs at 40% and should spin in the direction that drives the robot forward.
- While Create is held, the sticks do not drive, and Cross/Circle do not move the flower arms.
- Intake and catapult controls still work in motor test mode.

## Telemetry

- **Status**: run time
- **Speed**: `FULL`, `SLOW`, or `MOTOR TEST`
- **Front/Back left/Right**: power sent to each wheel
- **Intake**: intake power (negative = out)
- **Catapult**: `IDLE`, `UP` (launching), `DOWN`, or `HOLD`, with power
- **Flower Arms**: `IN` or `OUT`, with servo position

## Tuning Values

These constants are at the top of `BioBuzzTeleOp.java`:

| Constant | Value | What it does |
|---|---|---|
| `SLOW_MODE_SCALE` | 0.4 | Drive power in slow mode |
| `INTAKE_IN_POWER` / `INTAKE_OUT_POWER` | 1.0 / -0.9 | Intake speeds |
| `CATAPULT_UP_POWER` | -1.0 | Launch power (already maximum) |
| `LAUNCH_TIME_SECONDS` | 0.3 | How long one R1 press drives the launch |
| `CATAPULT_DOWN_POWER` | 0.35 | R2 return speed (lower = gentler) |
| `CATAPULT_HOLD_POWER` | 0.2 | Power that keeps the arm down |
| `FLOWER_ARM_IN_POSITION` / `FLOWER_ARM_OUT_POSITION` | 0.0 / 0.5 | Flower arm servo positions |

## Free Buttons

Not yet mapped on gamepad1: **Square**, **Triangle**, **D-pad**, **Options**, **R3**, and the touchpad.
