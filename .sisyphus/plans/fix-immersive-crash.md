# Fix MainActivity Immersive Mode Crash

## TL;DR

> **Quick Summary**: Fix `NoSuchMethodError: getInsetsController()` crash in MainActivity that blocks app launch on TV device
> **Deliverables**: Patched MainActivity.kt with proper API 30+ compatibility
> **Estimated Effort**: Quick (5 min)
> **Parallel Execution**: NO - sequential fix

---

## Context

### Original Request
App crashes on TV device `10.113.45.16` with:
```
java.lang.NoSuchMethodError: No virtual method getInsetsController()
  at com.example.netflixtv.MainActivity.enterImmersiveMode(MainActivity.kt:43)
```

### Root Cause Analysis
- The TV device runs older Android framework that lacks `Window.getInsetsController()` method
- Code checks `Build.VERSION.SDK_INT >= Build.VERSION_CODES.R` (API 30) but method still doesn't exist
- `window.insetsController` throws `NoSuchMethodError` at runtime on this device

---

## Work Objectives

### Core Objective
Fix MainActivity.kt to handle devices where `getInsetsController()` method doesn't exist even at API 30+

### Concrete Deliverables
- File: `app/src/main/kotlin/com/example/netflixtv/MainActivity.kt`
- Method: `enterImmersiveMode()` properly handles API 30+ without crashing

### Definition of Done
- [x] `adb -s 10.113.45.16 shell am start -n com.example.netflixtv/.MainActivity` succeeds without crash
- [x] App enters home screen without FATAL EXCEPTION

---

## Verification Strategy

### QA Policy - Agent Executed Only
- **Tool**: Bash (adb shell commands)
- **Scenario**: Launch app and verify no FATAL EXCEPTION in logcat

---

## TODOs

- [x] 1. Fix enterImmersiveMode() method with proper reflection-safe approach

  **What to do**:
  - Replace direct `window.insetsController` call with `WindowCompat.getInsetsController(window, decorView)` from AndroidX
  - `WindowCompat.getInsetsController()` handles all compatibility cases internally
  - Keep fallback to `systemUiVisibility` for API < 30
  - Add `@Suppress("DEPRECATION")` annotation on fallback code

  **Must NOT do**:
  - Don't use `Build.VERSION.SDK_INT >= Build.VERSION_CODES.R` alone - it doesn't guarantee method exists
  - Don't call `window.insetsController` directly on API 30+ - some devices lack it

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: None required
  - **Reason**: Simple 15-line method fix with clear pattern

  **References**:
  - `app/src/main/kotlin/com/example/netflixtv/MainActivity.kt:44-64` - Current problematic code
  - AndroidX `WindowCompat.getInsetsController()` - handles method existence internally

  **Acceptance Criteria**:
  - [ ] Build succeeds: `./gradlew assembleDebug`
  - [ ] App launches on device 10.113.45.16: `adb -s 10.113.45.16 shell am start -n com.example.netflixtv/.MainActivity`
  - [ ] Logcat shows NO `FATAL EXCEPTION` for `getInsetsController`

  **QA Scenarios**:

  \`\`\`
  Scenario: App launches without crash on TV device
    Tool: Bash (adb)
    Preconditions: App installed, device connected at 10.113.45.16
    Steps:
      1. adb -s 10.113.45.16 logcat -c (clear logs)
      2. adb -s 10.113.45.16 shell am start -n com.example.netflixtv/.MainActivity
      3. sleep 3
      4. adb -s 10.113.45.16 logcat -d | grep -E "FATAL EXCEPTION|NoSuchMethodError"
    Expected Result: No matches - app starts cleanly
    Failure Indicators: Any FATAL EXCEPTION or NoSuchMethodError in logcat
    Evidence: .sisyphus/evidence/fix-immersive-crash.log

  Scenario: Enter immersive mode works (window focus test)
    Tool: Bash (adb)
    Preconditions: App launched successfully
    Steps:
      1. adb -s 10.113.45.16 shell input keyevent 3 (home key)
      2. adb -s 10.113.45.16 shell am start -n com.example.netflixtv/.MainActivity
      3. adb -s 10.113.45.16 shell dumpsys window | grep mCurrentFocus
    Expected Result: mCurrentFocus shows MainActivity without crash
    Failure Indicators: App crash, ANR, or NoSuchMethodError
    Evidence: .sisyphus/evidence/fix-immersive-focus.log
  \`\`\`

---

## Final Verification Wave

- [x] F1. **App Launch Verification** — `quick`
  Build APK, install to device, launch with am start command, verify no FATAL EXCEPTION in logcat within 5 seconds.

---

## Commit Strategy

- **1**: `fix(MainActivity): handle getInsetsController() method not available on older devices`

---

## Success Criteria

### Verification Commands
```bash
adb -s 10.113.45.16 logcat -d | grep "FATAL EXCEPTION"  # Expected: empty (no crash)
adb -s 10.113.45.16 shell am start -n com.example.netflixtv/.MainActivity  # Expected: Starting: com.example.netflixtv
```