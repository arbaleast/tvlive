# UI Navigation Fix Plan

## TL;DR

> **Quick Summary**: Fix D-pad navigation issues across 4 screens - SearchScreen, DetailScreen, PlayerScreen, BrowseScreen
> **Deliverables**: All interactive elements D-pad-focusable, consistent focus behavior
> **Estimated Effort**: Medium (2-3 hours)
> **Parallel Execution**: YES - separate fix tasks can run in parallel

---

## Context

### Original Request
UI审查发现多处D-pad导航问题，用户确认需要修复。

### 问题清单

| # | 屏幕 | 问题 | 严重度 |
|---|------|------|--------|
| 1 | SearchScreen | Back按钮无focusable | 🔴严重 |
| 2 | SearchScreen | TextField无focusable | 🔴严重 |
| 3 | SearchScreen | LazyVerticalGrid无BringIntoViewRequester | 🔴严重 |
| 4 | DetailScreen | Back按钮无focusable | 🔴严重 |
| 5 | PlayerScreen | 进度条seek逻辑错误 | 🟡中等 |
| 6 | PlayerScreen | 焦点边框颜色不一致 | 🟡中等 |
| 7 | BrowseScreen | 使用已弃用DpadFocusable | 🟡中等 |

---

## Work Objectives

### Core Objective
修复所有D-pad导航问题，确保TV遥控器可以正确聚焦所有交互元素。

### Concrete Deliverables
- SearchScreen.kt - 3处修复
- DetailScreen.kt - 1处修复
- PlayerScreen.kt - 2处修复
- BrowseScreen.kt - 1处修复

### Definition of Done
- [x] 所有按钮可D-pad聚焦
- [x] TextField可接收TV遥控器输入
- [x] 进度条点击跳转到正确位置
- [x] 焦点边框颜色统一

---

## TODOs

- [x] 1. Fix SearchScreen.kt - 3处D-pad问题

  **What to do**:
  - Line 63: Back按钮添加 `.focusable()` 修饰符
  - Line 72: TextField添加 `.focusable()` 修饰符
  - Lines 114-128: LazyVerticalGrid内NetflixCard添加BringIntoViewRequester支持
  - 同时检查SearchViewModel确保错误状态正确传递

  **Must NOT do**:
  - 不要移除现有的focusRequester逻辑

  **References**:
  - `modules/feature-search/.../SearchScreen.kt:59-72` - 当前问题代码
  - `modules/ui-common/.../NetflixCard.kt:51-55` - BringIntoViewRequester正确用法

- [x] 2. Fix DetailScreen.kt - Back按钮添加focusable

  **What to do**:
  - Line 112-136: Button modifier添加 `.focusable(interactionSource = backInteractionSource)`

  **References**:
  - `modules/feature-detail/.../DetailScreen.kt:45-48` - 现有interactionSource定义
  - `modules/feature-home/.../HomeScreen.kt:128-139` - DpadFocusable正确用法示例

- [x] 3. Fix PlayerScreen.kt - 进度条seek和焦点边框

  **What to do**:
  - Line 300: 修改为计算点击位置百分比而非固定50%
  - Line 401: `Color.White` 改为 `TvliveColors.FocusBorder`

  **References**:
  - `modules/feature-player/.../PlayerScreen.kt:296-335` - 当前进度条实现

- [x] 4. Fix BrowseScreen.kt - 移除DpadFocusable

  **What to do**:
  - Lines 118, 218: DpadFocusable包装器替换为原生 `.focusable()` 模式

  **References**:
  - `modules/feature-browse/.../BrowseScreen.kt:118-126` - 当前问题代码
  - `modules/ui-common/AGENTS.md` - DpadFocusable已弃用说明

---

## Final Verification Wave

- [x] F1. **Build Verification** — `quick`
  `./gradlew assembleDebug` 成功

- [x] F2. **Code Review** — `quick`
  检查所有修改使用正确的focusable模式

---

## Success Criteria

```bash
./gradlew assembleDebug  # BUILD SUCCESSFUL
```

---

## Commit Strategy

- **1**: `fix(ui): D-pad navigation fixes across SearchScreen, DetailScreen, PlayerScreen, BrowseScreen`

---

## Notes

搜索和详情屏幕的问题会导致TV用户完全无法使用，需要优先修复。