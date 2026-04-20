# AGENTS.md — Project Standards

Generated: 2026-04-20

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

## File Structure

```
tvlive/
├── app/                          # Main application (MainActivity, AppNav)
├── modules/
│   ├── data/                     # Content, ContentRepository, Category
│   ├── ui-common/                # NetflixCard, TvliveTheme, Routes, DpadFocusable
│   ├── media/                    # PlayerManager (ExoPlayer + HLS)
│   ├── feature-home/             # HomeScreen, HomeViewModel
│   ├── feature-browse/           # BrowseScreen, BrowseViewModel
│   ├── feature-detail/           # DetailScreen
│   ├── feature-player/          # PlayerScreen
│   └── feature-search/          # SearchScreen, SearchViewModel
├── build.gradle.kts              # Root build (AGP 8.2.0, Kotlin 1.9.20)
├── settings.gradle.kts           # Aliyun Maven mirror
└── AGENTS.md
```

## Framework & Versions

- **Language**: Kotlin 1.9.20
- **UI**: Jetpack Compose + Compose for TV (androidx.tv:tv-foundation, tv-material)
- **Min SDK**: 21 | **Target SDK**: 34
- **Gradle**: 8.5 | **AGP**: 8.2.0 | **Compose Compiler**: 1.5.10
- **Compose BOM**: 2023.10.01
- **Media3/ExoPlayer**: 1.2.0

## Where To Look

| Need | Look In |
|------|---------|
| TV manifest / Leanback launcher | `app/src/main/AndroidManifest.xml` |
| Colors / Theme | `modules/ui-common/.../TvliveTheme.kt` |
| Content data models | `modules/data/.../Content.kt` |
| Media playback (HLS) | `modules/media/.../PlayerManager.kt` |
| Home screen | `modules/feature-home/...` |
| Browse screen | `modules/feature-browse/...` |
| Detail screen | `modules/feature-detail/...` |
| Player screen | `modules/feature-player/...` |
| Search screen | `modules/feature-search/...` |
| D-pad navigation (focusable) | `modules/ui-common/.../DpadFocusable.kt` |
| Navigation routes | `modules/ui-common/.../Routes.kt` |
| Sample content | `app/src/main/assets/content_data.json` |

## TV-Specific Configuration

- **Launcher**: Leanback intent filter in AndroidManifest.xml
- **Theme**: android:Theme.Black (Theme.Leanback is runtime-only on TV devices)
- **Remote Control**: D-pad navigation via Compose focusable components

## Responsibilities

- TV program playback via Media3/ExoPlayer
- Remote control (D-pad) navigation
- Channel management
- Leanback-optimized UI

## Boundaries

### Ask First
- New dependencies (media playback libraries)
- Architecture changes (MVVM, navigation graph)
- Native code or platform channels

### Never Do
- Hardcode API keys or credentials
- Commit sensitive data to version control
- Use mobile-specific UI (touch-first design)

## Quality Checklist

- [x] APK builds successfully — verified 2026-04-12
- [x] Tests pass — verified 2026-04-12
- [x] Lint passes — verified 2026-04-12
- [x] Leanback launcher intent filter present

## Common Issues

- **Theme.Leanback not found**: Use `android:Theme.Black` in themes.xml (Theme.Leanback runtime-only on actual TV devices)
- **TvMaterialTheme unresolved**: Not used — basic Compose theming works without it
- **Compose TV alpha APIs**: Use OptIn annotations for experimental TV foundation APIs