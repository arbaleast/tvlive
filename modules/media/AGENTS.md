# media Module

## Overview
ExoPlayer wrapper with HLS support, retry logic, and TV-optimized buffering for live streaming playback.

## Where to Look

| File | Purpose |
|------|---------|
| `src/main/kotlin/.../PlayerManager.kt` | ExoPlayer wrapper with StateFlow state, retry logic, seek controls |

## Conventions

- **StateFlows**: Expose playback state as `StateFlow<Boolean/Long/Long/String?>` (isPlaying, currentPosition, duration, error)
- **Buffering**: TV-optimized via DefaultLoadControl (30s min, 120s max, prioritize time over size)
- **Retry**: Max 3 retries with 2s delay on playback errors, resets on successful playback
- **Seek**: seekBack/forward default to 10s increments
- **Release**: Always call release() to clean up resources

## Anti-Patterns

- Calling prepare() without checking player initialization
- Ignoring retryCount reset on new video prepare
- Not calling updatePosition() for position tracking during playback
- Using touch-specific gestures (not applicable to TV)