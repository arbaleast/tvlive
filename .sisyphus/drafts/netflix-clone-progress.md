# Netflix Clone Progress — tvlive

## Context
- This draft captures the current state of the Netflix-like Android TV clone within tvlive based on silent exploration results and repository artifacts.

## Requirements (confirmed)
- [x] Identify implemented features: video playback, channel-style browsing, Leanback TV UX.
- [x] Map key modules, directory structure, and data flow.
- [x] Catalog build/config/test infra status and documentation present in repo.

## Technical Decisions
- [Decision] Core data model uses Content and Category with a ContentRepository that reads assets/content_data.json and falls back to a local sample dataset if needed.
- [Decision] Player implemented via ExoPlayer (Media3) integrated in PlayerScreen.
- [Decision] UI navigation implemented in AppNav with HomeScreen -> DetailScreen -> PlayerScreen.
- [Decision] Content is served from local assets for MVP, enabling offline demos and planning consistency.

## Research Findings (summary)
- Implemented screens exist as: HomeScreen.kt, DetailScreen.kt, PlayerScreen.kt; main entry is MainActivity.kt and AppNav.kt.
- Data classes: Content.kt and Category.kt; Data layer: ContentRepository.kt.
- Asset data: assets/content_data.json provides mock catalog (4 categories, 12 items).
- Plans and notes located under .sisyphus/plans and .sisyphus/notepads/netflix-clone.
- AGENTS.md documents project standards.

## Open Questions
- Are there any required features missing from the MVP (search, favorites, paging)?
- Should we add unit/UI tests and CI for the Netflix clone MVP?

## Scope Boundaries
- IN: Netflix clone MVP features based on current repo artifacts (Home/Detail/Player) and asset-driven data.
- OUT: Any server-side APIs, authentication, or advanced analytics not present in the current repo.

## Next Steps (planned)
- Draft a formal Plan document (.sisyphus/plans/netflix-clone-progress.md) with execution waves.
- Validate with stakeholders and prepare for a plan review session.
- Outline QA scenarios for ContentRepository, navigation, and playback.

Associated evidence and references will be captured in the final plan.
