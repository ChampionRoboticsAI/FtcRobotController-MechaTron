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
| R1 (hold) | `right_bumper` | Catapult **launch** |
| R2 (hold) | `right_trigger` | Catapult **down** |
| Cross (press) | `a` | Flower arms one stage **out** (UP → BLOCK → FLAT) |
| Circle (press) | `b` | Flower arms one stage **in** (FLAT → BLOCK → UP) |
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
| R1 (hold) | **LAUNCH**: full power up while held |
| R2 (hold) | **DOWN** at full power |
| Neither | **HOLD**: 20% power keeps the arm down against the rubber band |

- **Start of TeleOp:** the catapult is **IDLE** (no power, braked) so it stays in its initial position.
  Holding power only begins after R1 or R2 is pressed for the first time.
- **Hold R1 until the arm reaches the top.** Releasing early switches to hold power, which pulls the arm back down mid-swing.
- **R2 wins** if both R1 and R2 are pressed.
- The rubber band holds the arm up after a launch. Use R2 to bring it down; the hold power then keeps it down.

## Flower Arms

The arms have three stages, measured outward from vertical:

| Stage | Angle | Use |
|---|---|---|
| **UP** | 0° | Arm points straight up. Position when TeleOp starts. |
| **BLOCK** | 45° | Blocks Nectar balls from the intake; Pollen passes through. |
| **FLAT** | 90° | Arm lies flat on the field, for pulling Pollen out of the Flower section. |

| Input | Action |
|---|---|
| Cross (press) | One stage **out**: UP → BLOCK → FLAT |
| Circle (press) | One stage **in**: FLAT → BLOCK → UP |

- Each press moves one stage; holding the button does not keep moving. Cross at FLAT or Circle at UP does nothing.
- Both arms move to **UP** when TeleOp starts.
- Servo positions are set in `FlowerArms.java`. Use the **BioBuzz: Flower Arm Calibration** OpMode to find each arm's UP position.

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
- **Flower Arms**: `UP`, `BLOCK` or `FLAT`, with angle and both servo positions

## Tuning Values

These constants are at the top of `BioBuzzTeleOp.java`:

| Constant | Value | What it does |
|---|---|---|
| `SLOW_MODE_SCALE` | 0.4 | Drive power in slow mode |
| `INTAKE_IN_POWER` / `INTAKE_OUT_POWER` | 1.0 / -0.9 | Intake speeds |
| `CATAPULT_UP_POWER` | -1.0 | Launch power (already maximum) |
| `CATAPULT_DOWN_POWER` | 1.0 | R2 return power |
| `CATAPULT_HOLD_POWER` | 0.2 | Power that keeps the arm down against the rubber band |

Flower arm positions are in `FlowerArms.java`:

| Constant | Value | What it does |
|---|---|---|
| `LEFT_UP_POSITION` / `RIGHT_UP_POSITION` | 0.20 / 0.20 (calibrate) | Servo position where each arm points straight up |
| `Stage` angles | 0° / 45° / 90° | UP / BLOCK / FLAT, measured out from vertical |

## Free Buttons

Not yet mapped on gamepad1: **Square**, **Triangle**, **D-pad**, **Options**, **R3**, and the touchpad.
