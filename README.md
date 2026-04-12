# tvlive

Netflix-style Android TV application for TV program playback.

## Tech Stack

- **Language**: Kotlin 1.9.20
- **UI Framework**: Jetpack Compose (TV), Compose Material3
- **Media**: Media3/ExoPlayer
- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34
- **Build Tool**: Gradle 8.5 (Kotlin DSL)
- **Android Gradle Plugin**: 8.2.0

## Project Structure

```
tvlive/
├── app/                          # Main application module
│   ├── build.gradle.kts           # App-level build config
│   └── src/main/
│       ├── AndroidManifest.xml    # TV manifest (Leanback launcher)
│       ├── kotlin/com/example/netflixtv/
│       │   ├── MainActivity.kt    # Entry point
│       │   ├── data/             # Data models & repository
│       │   └── ui/              # Compose screens
│       └── assets/
│           └── content_data.json # Sample content data
├── build.gradle.kts               # Root build config
├── settings.gradle.kts            # Project settings
├── gradle.properties             # AndroidX enabled
├── AGENTS.md                    # Project standards
└── README.md                   # This file
```

## Build Commands

```bash
# Build debug APK
ANDROID_HOME=/home/al/Android/Sdk ./gradlew assembleDebug

# Build release APK
ANDROID_HOME=/home/al/Android/Sdk ./gradlew assembleRelease

# Run tests
./gradlew test

# Lint
./gradlew lint

# Clean
./gradlew clean
```

## Plan Documents

See `.sisyphus/plans/` for execution plans:

- `netflix-clone.md` - Original Netflix clone implementation
- `netflix-clone-ui.md` - UI revamp with Netflix-style components
- `netflix-clone-progress.md` - Progress tracking plan
- `tvlive-initial.md` - Initial project setup

## Quick Start

1. Clone the repository
2. Ensure Android SDK is at `/home/al/Android/Sdk`
3. Run `./gradlew assembleDebug`
4. Install APK on Android TV device

## License

Private - All rights reserved.