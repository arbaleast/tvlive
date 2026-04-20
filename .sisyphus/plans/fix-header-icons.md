# Fix HomeScreen Header Icons

## TL;DR

> **Quick Summary**: Replace emoji icons (🔍, ☰) with styled text labels (Search, Menu) and add background to header
> **Estimated Effort**: Quick (5 min)
> **Parallel Execution**: NO

---

## Context

### Original Request
User says "最上面的白条能不能去掉,有些icon太老旧了" - asking to remove the white bar at the top and update old icons.

### Root Cause
- Header has no background, causing white bar visibility
- Emoji icons (🔍, ☰) look outdated on TV

---

## Work Objectives

### Core Objective
Fix HomeScreen header to be visually consistent.

### Concrete Deliverables
- File: `modules/feature-home/src/main/kotlin/com/example/netflixtv/featurehome/HomeScreen.kt`
- Changes: 1) Add background to Row, 2) Replace emojis with text labels

---

## TODOs

- [x] 1. Fix HomeHeader - add background and replace emojis

  **What to do**:
  - Line 115: Change `.padding(horizontal = 20.dp)` to include `.background(TvliveColors.BackgroundPrimary).padding(horizontal = 20.dp, vertical = 12.dp)`
  - Lines 134-137: Replace `🔍` with "Search" text (12sp, FontWeight.Medium, TvliveColors.TextPrimary)
  - Lines 148-152: Replace `☰` with "Menu" text (12sp, FontWeight.Medium, TvliveColors.TextPrimary)

  **Must NOT do**:
  - Don't change the DpadFocusable wrapper logic

  **References**:
  - `modules/feature-home/.../HomeScreen.kt:111-157` - HomeHeader function

  **Acceptance Criteria**:
  - [ ] Build succeeds: `./gradlew assembleDebug`
  - [ ] Header has background color

  **QA Scenarios**:

  ```
  Scenario: Build verification
    Tool: Bash
    Preconditions: None
    Steps:
      1. ./gradlew assembleDebug
    Expected Result: BUILD SUCCESSFUL

  Scenario: Install and screenshot
    Tool: Bash
    Preconditions: Build succeeds
    Steps:
      1. adb install -r app/build/outputs/apk/debug/app-debug.apk
      2. adb shell am start -n com.example.netflixtv/.MainActivity
      3. adb shell screencap -p /sdcard/screen.png
      4. adb pull /sdcard/screen.png /tmp/header_fix.png
    Expected Result: App launches without crash, screenshot captured
  ```

---

## Final Verification Wave

- [x] F1. **Build & Install** — `quick`
  Build APK, install to device, verify app launches.

---

## Commit Strategy

- **1**: `fix(ui): replace emoji icons with text labels in HomeScreen header`

---

## Success Criteria

```bash
./gradlew assembleDebug  # BUILD SUCCESSFUL
```