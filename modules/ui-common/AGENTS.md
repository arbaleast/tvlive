# AGENTS.md — ui-common Module

## OVERVIEW
Shared TV-optimized UI components with Netflix-style focus animations and semantic color theming.

## WHERE TO LOOK

| File | Purpose |
|------|---------|
| `NetflixCard.kt` | Netflix-style card with focus scale, glow, and border animations |
| `HeroBanner.kt` | Hero banner with Ken Burns effect and CTA buttons |
| `DpadFocusable.kt` | Deprecated wrapper (use native `.focusable()` instead) |
| `TvliveTheme.kt` | `TvliveColors` semantic palette and typography presets |
| `Routes.kt` | Navigation route constants and builders |

## CONVENTIONS

**Colors**: Always use `TvliveColors` semantic palette instead of hardcoded values.
- Background: `BackgroundPrimary`, `BackgroundSecondary`, `BackgroundElevated`
- Text: `TextPrimary`, `TextSecondary`, `TextTertiary`
- Focus: `FocusGlow`, `FocusBorder`
- Accents: `AccentLive`, `AccentNew`, `AccentPopular`

**Focus handling**: Use `MutableInteractionSource` + `collectIsFocusedAsState()` pattern.

**Animations**: Use Spring with `DampingRatioMediumBouncy` for card/button scale, `StiffnessLow` for smoother feel.

**Auto-scroll**: Attach `BringIntoViewRequester` to focusable items for D-pad navigation.

## ANTI-PATTERNS

- **DpadFocusable** is deprecated. Use native `.focusable(interactionSource = interactionSource)` instead.
- Do not hardcode colors. Always reference `TvliveColors` constants.
- Avoid `animateFloatAsState` without explicit `animationSpec` — causes janky transitions.
- Do not skip `BringIntoViewRequester` on scrollable lists with focusable items.
