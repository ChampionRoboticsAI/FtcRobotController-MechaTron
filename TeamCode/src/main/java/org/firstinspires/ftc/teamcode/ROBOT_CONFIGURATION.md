# FTC Robot Configuration

This documents contains all of the hardware and mechanical configuraiton details for the FTC robot.

## StarterBot details

The robot base structure is build using [goBuilda StarterBot](https://www.gobilda.com/ftc-starter-bot-resource-guide-2026-2027-season/).
The robot has been further modified to use the Mecanum wheel described in [this instructions](https://www.gobilda.com/ftc-starterbot-with-mecanum-wheels-resource-guide-2026-2027/).

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
  - Port 0: Intake (GoBuilda 5202/3/4 series)
  - Port 1: Front Right (GoBuilda 5202/3/4 series)
  - Port 2: Back Right (GoBuilda 5202/3/4 series)

- Servos
  - port 0: Flower Thing (Servo)

- I2C Bus 0
  - Port 0: imu (REV internal IMU (BHI260AP))



## Expansion Hub port mapping

- Motors 
  - Port 0: Shooter (GoBuilda 5202/3/4 series)
  - Port 1: Front Left (GoBuilda 5202/3/4 series)
  - Port 2: Back Left (GoBuilda 5202/3/4 series)

- Servos
  - Port 0: Right Flower (Servo)