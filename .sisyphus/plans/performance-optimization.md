# Performance Optimization

## TL;DR

> Optimize animations for TV performance - reduce HeroBanner Ken Burns and NetflixCard animations

---

## TODOs

- [x] 1. HeroBanner - reduce Ken Burns animation
  - KenBurnsScaleEnd: 1.08f → 1.02f
  - KenBurnsDurationMs: 8000 → 20000
  - offsetX target: 40f → 10f
  - Disable contentAlpha animation

- [x] 2. NetflixCard - simplify animations
  - Reduce glow animation (tween 300→800)
  - Reduce spring stiffness

- [x] 3. Rebuild and test

---

## Verification

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell dumpsys gfxinfo com.example.netflixtv
```