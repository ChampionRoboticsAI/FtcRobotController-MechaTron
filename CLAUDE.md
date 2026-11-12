# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

A team fork of the FIRST Tech Challenge `FtcRobotController` SDK (v12.0, team branch `biobuzz-2026-27`). It builds a single Android app (the Robot Controller) that runs on the Control Hub / RC phone.

**All robot code you create (OpModes, hardware classes, subsystems, utilities) must go in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`** (subpackages under it are fine). Files placed elsewhere — e.g. directly in `TeamCode/` — are not on the source path and will not compile into the app. Everything outside TeamCode is SDK scaffolding that is periodically re-synced from upstream (`FIRST-Tech-Challenge/FtcRobotController`), so avoid editing it to keep upstream merges clean.

## Build commands

Gradle 9.1 wrapper + Android Gradle Plugin 8.13.2 (requires Android Studio Narwhal 3 Feature Drop or later; do not let an older Studio downgrade AGP). On Windows use `gradlew.bat` / `./gradlew` from Git Bash.

- Compile/assemble debug APK: `./gradlew :TeamCode:assembleDebug`
- Install to a connected Control Hub/phone (via adb): `./gradlew :TeamCode:installDebug`
- Clean: `./gradlew clean`

There are no unit tests or lint configuration in this project. "Testing" means deploying to the robot and running the OpMode from the Driver Station; a successful `assembleDebug` is the only local verification available.

## Architecture

- **`TeamCode/`** — the application module (`com.android.application`, namespace `org.firstinspires.ftc.teamcode`). All team OpModes go in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`. Its `build.gradle` is where team-specific dependencies/customization belong.
- **`FtcRobotController/`** — SDK library module containing the RC activity and the sample OpModes in `src/main/java/org/firstinspires/ftc/robotcontroller/external/samples/`. Samples are the reference for SDK usage (hardware, IMU, vision/AprilTag, etc.). To use one, copy it into TeamCode, rename it, and remove `@Disabled`.
- **`build.common.gradle`** — shared Android config (compileSdk 30, minSdk 24, signing with `libs/ftc.debug.keystore`, versionCode/Name derived from `FtcRobotController/src/main/AndroidManifest.xml`). Don't edit; customize in `TeamCode/build.gradle` instead.
- **`build.dependencies.gradle`** — the actual SDK is pulled as Maven artifacts (`org.firstinspires.ftc:RobotCore`, `Hardware`, `Vision`, … all pinned to `12.0.0`). SDK source is not in this repo; see the Javadoc at https://javadoc.io/doc/org.firstinspires.ftc.

### OpMode registration

OpModes are discovered at runtime by annotation — there is no central registry to update:
- `@TeleOp(name=..., group=...)` or `@Autonomous(...)` on a class extending `LinearOpMode` (sequential `runOpMode()`) or `OpMode` (iterative `init()`/`loop()`).
- `@Disabled` hides it from the Driver Station list.
- Hardware is fetched by the names configured on the robot: `hardwareMap.get(DcMotor.class, "left_drive")`. Device names must match the robot's active configuration file.

Sample naming convention (from `TeamCode/.../readme.md`): prefix `Basic`, `Sensor` (Sensor-Company-Type), `Robot` (Robot-Mode-Action-OpModeType), or `Concept` (Concept-Topic-OpModeType).

## SDK 12.0 gotcha

AprilTag detection now supports clusters: code iterating `AprilTagDetection` objects as single tags no longer compiles. Detections must be checked for cluster vs. singleton and cast to the right type — see the v12.0 "Breaking Changes" section of `README.md` for the exact pattern.
