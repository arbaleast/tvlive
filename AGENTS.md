# AGENTS.md — Project Standards

## Identity

| Field | Value |
|-------|-------|
| **Name** | tvlive |
| **Role** | Android TV Application |
| **Specialization** | TV program playback |
| **Tech Stack** | Kotlin, Jetpack Compose (TV), Gradle (Kotlin DSL) |
| **Audience** | TV viewers |

## Commands

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

## Project Knowledge

### File Structure

```
tvlive/
├── app/                          # Main application module
│   ├── build.gradle.kts           # App-level build config with Compose TV deps
│   ├── proguard-rules.pro         # ProGuard rules
│   └── src/main/
│       ├── AndroidManifest.xml    # TV manifest (Leanback launcher)
│       ├── java/com/tvlive/app/
│       │   └── MainActivity.kt   # Main entry point with Compose TV UI
│       └── res/
│           ├── drawable/         # App icon (vector)
│           └── values/           # strings, themes (Leanback)
├── build.gradle.kts               # Root build config (AGP 8.2.0, Kotlin 1.9.20)
├── settings.gradle.kts            # Project settings
├── gradle.properties             # AndroidX, Jetifier enabled
├── gradlew / gradlew.bat         # Gradle wrapper scripts
├── gradle/wrapper/               # Gradle wrapper (8.5)
└── AGENTS.md
```

### Framework & Versions

- **Language**: Kotlin 1.9.20
- **UI**: Jetpack Compose + Compose for TV (androidx.tv:tv-foundation, tv-material)
- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34
- **Gradle**: 8.5 (Kotlin DSL)
- **Android Gradle Plugin**: 8.2.0
- **Compose Compiler**: 1.5.4

### Key Dependencies

- **Compose BOM**: 2023.10.01 (ui, material3, foundation, runtime)
- **TV Foundation**: androidx.tv:tv-foundation:1.0.0-alpha05
- **TV Material**: androidx.tv:tv-material:1.0.0-alpha05
- **Navigation**: androidx.navigation:navigation-compose:2.7.5
- **Media3/ExoPlayer**: androidx.media3:media3-exoplayer:1.2.0, media3-ui:1.2.0
- **Lifecycle**: lifecycle-viewmodel-compose, lifecycle-runtime-compose:2.6.2

### Maven Repository Configuration

**Aliyun Maven mirror** is configured in `settings.gradle.kts`:
- `https://maven.aliyun.com/repository/public` (main)
- `https://maven.aliyun.com/repository/google`
- `https://maven.aliyun.com/repository/gradle-plugin`

### TV-Specific Configuration

- **Launcher**: Leanback launcher intent filter in AndroidManifest.xml
- **Theme**: android:Theme.Black (fallback; Theme.Leanback is runtime-only on TV devices)
- **Remote Control**: D-pad navigation via Compose focusable components

## Responsibilities

- TV program playback via Media3/ExoPlayer
- Remote control (D-pad) navigation
- Channel management
- Leanback-optimized UI

## Boundaries

### Ask First

- New dependencies (especially media playback libraries)
- Architecture changes (MVVM, navigation graph)
- Native code or platform channels

### Never Do

- Hardcode API keys or credentials
- Commit sensitive data to version control
- Use mobile-specific UI patterns (touch-first design)

## Quality Checklist

- [x] APK builds successfully (`./gradlew assembleDebug`) — verified 2026-04-12
- [x] Tests pass (`./gradlew test`) — verified 2026-04-12
- [x] Lint passes (`./gradlew lint`) — verified 2026-04-12
- [x] Leanback launcher intent filter present
- [ ] Remote control navigation works (requires physical TV device)

## OpenCode Sisyphus Protocol

- Use plan-based execution for multi-step tasks
- Decompose plan items into granular, verifiable sub-tasks
- Verify with diagnostics and build/test evidence
- Never commit without explicit user request

## Development Notes

### Build Environment

- Requires Android SDK at `$ANDROID_HOME` or `/home/al/Android/Sdk`
- Java 17+ required
- Maven mirror (Aliyun) configured for China network access

### Common Issues

- **Theme.Leanback not found**: Use `android:Theme.Black` as parent in themes.xml (Theme.Leanback is runtime-only on actual TV devices)
- **TvMaterialTheme unresolved**: Currently not used — basic Compose theming works without it
- **Compose TV alpha APIs**: Use OptIn annotations for experimental TV foundation APIs
