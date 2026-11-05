# FTC Robot Configuration

This documents contains all of the hardware and mechanical configuraiton details for the FTC robot.

Primary documentation for FTC is available at https://ftc-docs.firstinspires.org/en/latest/index.html

## StarterBot details

The robot base structure is build using [goBuilda StarterBot](https://www.gobilda.com/ftc-starter-bot-resource-guide-2026-2027-season/).
The robot has been further modified to use the Mecanum wheel described in [this instructions](https://www.gobilda.com/ftc-starterbot-with-mecanum-wheels-resource-guide-2026-2027/).

Mecannum Drive example code is available at https://ftc-tech-toolbox.vercel.app/docs/Commonly%20Programmed%20Modules/Mec

## Robot Controller and Driver devices

The robot uses the official REV Robotics devices as described below

- REV Robotics [Driver Hub](https://docs.revrobotics.com/duo-control/control-system-overview/driver-hub-specifications)
- A REV Robotics [Control Hub](https://docs.revrobotics.com/duo-control/control-system-overview/control-hub-basics)
- A REV Robotics [Expansion Hub](https://docs.revrobotics.com/duo-control/control-system-overview/expansion-hub-basics)

All the devices are configured and connected as per the [official documentation](https://docs.revrobotics.com/duo-control/menu/control-hub-gs/connect-to-the-control-hub-robot-control-console)

## Gamepad Controller

We are using two [Logitech F310](https://www.amazon.com/dp/B003VAHYQY) wired controller gamepad. These two Gamepads will be connected via USB to the REV Driver Hub.

## Controller Hub port mapping
- Motors
    - Port 0: Back Left (GoBuilda 5202/3/4 series)
    - Port 1: Front Left (GoBuilda 5202/3/4 series)
    - Port 2: Front Right (GoBuilda 5202/3/4 series)
    - Port 3: Back Right (GoBuilda 5202/3/4 series)

<!-- - Motors
  - Port 0: Shooter (GoBuilda 5202/3/4 series)
  - Port 1: Back Left (GoBuilda 5202/3/4 series)
  - Port 2: Front Left (GoBuilda 5202/3/4 series)

- Servos
  - port 0: Left Flower (Servo)

- I2C Bus 0
  - Port 0: imu (REV internal IMU (BHI260AP))



## Expansion Hub port mapping

- Motors 
  - Port 0: Intake (GoBuilda 5202/3/4 series)
  - Port 1: Back Right (GoBuilda 5202/3/4 series)
  - Port 2: Front Right (GoBuilda 5202/3/4 series)
  - Port 3: Midtake (GoBuilda 5202/3/4 series)

- Servos
  - Port 0: Right Flower (Servo) -->

## PedroPathing

Pedro Pathing [documentation](https://pedropathing.com/docs/pathing)
Pedro Pathing [GitHub](https://github.com/Pedro-Pathing/PedroPathing)

[goBuilda 4-Bar Odometry Pod](https://www.gobilda.com/4-bar-odometry-pod-32mm-wheel/)

### Odometry

The robot uses two goBILDA 4-Bar Odometry Pods (32mm omni wheel, 2000 counts per revolution, 4-pos JST PH)
with the Control Hub IMU, which is Pedro Pathing's **Two Wheel** localizer.

- Forward pod: mounted parallel to the wheels, measures forward/backward travel.
- Strafe pod: mounted perpendicular to the wheels, measures left/right travel.

The pods plug into motor **encoder** ports and are read through the name of the motor on that port.
The drive motors run without encoders, so their encoder ports are free. Both pods go on the Control Hub,
the same hub as the IMU:

- Control Hub encoder port 2 ("Front Left"): Forward pod
- Control Hub encoder port 1 ("Back Left"): Strafe pod

The Shooter's encoder port (Control Hub port 0) is kept free for flywheel velocity control.
No change to the robot configuration file is needed for the pods.