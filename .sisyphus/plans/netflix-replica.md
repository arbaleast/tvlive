# Netflix Replica MVP Plan

## TL;DR
Build a mainstream Android TV native app MVP inspired by Netflix with a home screen of content rows, a detail page, and an ExoPlayer-based player. Data will be mocked (two datasets) with no authentication. UI/UX targets are TV-optimized and automation-driven QA is required. Proceed with Metis review and plan execution.

## Context

### Original Request
- Create a Netflix-like Android TV MVP with native UI, no authentication, 2 mock catalogs, and general TV streaming capabilities. Accepts a mainstream TV app quality bar and requires automation. Execute plan with minimal questions.

### Interview Summary
- Platform: Android TV, Kotlin, Jetpack Compose for TV
- Data: Two mock catalogs to simulate content metadata and playback URLs
- Auth: No authentication required
- Data source: Mock catalogs initially; real data integration later
- UX: TV-focused, remote navigation, focus management, accessibility in mind
- Timeline: Time budget deemed sufficient; proceed with execution
- Constraints: No cross-platform requirement at MVP stage

### Metis Findings (to be incorporated after review)
- Placeholder for Metis feedback

## Work Objectives

### Core Objective
- Deliver a polished Android TV MVP featuring home screen content rails, detail pages, and playback with a clean, TV-optimized UX, underpinned by automation for QA.

### Concrete Deliverables
- Android TV app MVP (native) with Home, Detail, and Player screens
- Two mock catalogs for content metadata and streaming URLs
- TV-optimized navigation (D-pad focus, keyboard-friendly)
- UI skeletons for Search and Browse (non-blocking in MVP)
- Automated QA scaffolding (agent-executed tests)
- Plan for integrating real data sources in future phase

### Definition of Done (DoD)
- All MVP screens accessible and navigable with remote control
- Video playback starts and stops with correct UI states
- Mock catalog data loads deterministically
- Automated QA scenarios execute without human intervention
- Codebase adheres to TV UX guidelines and accessibility basics

### Must Have (Guardrails)
- No authentication in MVP
- 2 mock catalogs supplied
- ExoPlayer-based playback or platform equivalent
- TV remote navigation patterns implemented
- Automated QA coverage for happy-path and error paths

### Must NOT Have
- Real user authentication/authorization logic
- DRM/licensing work not in MVP scope
- Production telemetry that requires backend changes

## Verification Strategy (Agent-Executed QA)
- QA scenarios will cover UI flow from Home -> Detail -> Play, plus a search path.
- Each scenario includes exact steps, selectors, data, and verification outcomes.
- Evidence artifacts to be stored under .sisyphus/evidence/task-NetflixReplica*.

## Execution Strategy
- Parallel waves to maximize throughput while keeping scope manageable.
- Wave 1: Scaffolding, data models, navigation skeletons, mock data loaders, and player wrapper.
- Wave 2: Home/Detail UI, Search UI, and mock playback integration.
- Wave 3: QA scaffolding, automated test generation, and CI hooks.

### Plan Continuity & Versioning
- All tasks captured in this single plan file. Updates are appended as the work progresses.

## Metis Review & Open Questions
- Placeholder for acknowledged questions and guardrails from Metis.

## Final Verification Wave
- Plan will undergo multi-agent review (oracle, Momus, etc.) before completion.

## Commit Strategy
- type(scope): desc

## Success Criteria
- Acceptance criteria and evidence for each task verifiable via agent-executed tests.
