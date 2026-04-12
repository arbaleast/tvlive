# Netflix Replica MVP Draft

This draft captures the high-level plan for building a Netflix-inspired Android TV app MVP. It is intentionally technology-agnostic at this stage and focuses on scope, goals, and constraints. Once requirements are clarified, we will convert this draft into a formal plan.

## Goals
- Create a Netflix-like Android TV experience with: home screen featuring multiple content rows, content detail page, and a playable video player.
- Support remote control navigation (D-pad), focus handling, and smooth content discovery.
- Provide a minimal content catalog (mock or public data) and a lightweight backend integration (REST/GraphQL) for metadata and playback URLs.
- Ensure accessibility and responsive UI across a range of TV sizes.

## Scope
### IN
- Home screen with multiple rows (Continue Watching, Popular Now, Categories).
- Content detail page with synopsis, cast, and playback action.
- Video playback using ExoPlayer (or platform equivalent).
- Search and basic filtering by genre.
- Basic user profile/state management (local, optional cloud sync).
- Local/mock data ingestion and simple data model layer.
- Leanback-friendly navigation pattern (focus order, DPAD handling).

### OUT
- Licensing/content rights and real Netflix catalog integration.
- DRM/4K streaming specifics, Widevine licenses, or platform DRM integration.
- Multi-user profiles, parental controls, or advanced parental gating.
- Monetization, recommendations engine beyond basic metadata.
- Full production-grade telemetry, monitoring, and security hardening (to be added in a later phase).

## Core MVP Features (high level)
- Home screen with horizontal carousels for different categories.
- Content detail page showing metadata and a prominent Play action.
- Video playback experience with basic playback controls and progress tracking.
- Search interface and basic filtering by genre or year.

## Non-Functional Considerations
- Performance: smooth UI transitions, responsive navigation, and stable playback.
- Accessibility: focus traversal, content labeling, and visible focus indicators.
- Testing: plan for agent-executed tests (UI flows, playback happy path, error paths).
- Data: scaffolding for a catalog with mock data; plan for switching to real data sources later.

## Architecture & Choices (high level)
- UI layer: Compose TV (Leanback-friendly) with a clear separation of screens and navigation.
- Data layer: local catalog models with an adapter to fetch remote metadata when available.
- Playback: ExoPlayer (or equivalent) wrapped in a repository interface.
- Navigation: simple router supporting back stack and focus management for TV.

## Risks, Assumptions & Constraints
- Assumption: No real Netflix catalog is used in MVP; catalog is mocked or sourced from public datasets.
- Risk: TV-specific UI patterns may require iteration for optimal focus handling.
- Constraint: No licensing-related work is in scope for MVP.

## Open Questions (decision points)
- data source: mock catalog vs public API (e.g., TMDB) for MVP data?
- authentication: require user login in MVP or proceed with anonymous profile?
- UI style: how closely should we mirror Netflix visual design (card sizes, typography, color accents)?
- acceptance criteria: what constitutes a “Done” MVP (screens, playback success, search works, etc.)?

## Research & References (placeholders)
- Netflix-like UI patterns for TV apps (general guidance)
- ExoPlayer integration patterns for TV apps
- Leanback/navigation patterns for Android TV
- Candidate public data sources for catalog metadata (e.g., TMDB)

## Next Steps (when requirements are clarified)
- Align on MVP scope and deliverables.
- Generate a formal plan in .sisyphus/plans/{plan-name}.md with detailed tasks and QA scenarios.
- Create initial sample catalog data and a minimal playback flow.

"Draft in-progress. Awaiting clarifications and Metis review before finalizing plan."
