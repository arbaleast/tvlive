# Netflix Clone Progress – tvlive

## TL;DR
> **Summary**: Unified progress plan for tvlive's Netflix-like Android TV MVP, consolidating codebase exploration findings and authoritative external docs into a decision-complete, wave-based execution roadmap.
> **Deliverables**: Root README, CI workflow draft, test scaffolding, expanded sample data, and external docs map.
> **Effort**: Medium
> **Parallel**: YES – independent tasks can run in parallel once plan is issued
> **Critical Path**: T1 (README) → T3/T4 (data expansion) → T2/T5 (CI + UI tests) → T6 (remote data strategy)

## Context
### Original Request
Assess tvlive Netflix-like clone progress from repo artifacts and plan documents; produce a decision-complete execution plan.

### Exploration Findings (bg_04035d3e + bg_df5eac8b)

**Implemented Core Features**
- Video playback: `PlayerScreen.kt` uses ExoPlayer (Media3) with poster overlay, play/pause, ±10s skip, and back navigation.
- Channel-style browsing: `Content.kt`/`Category.kt` data model + `ContentRepository.kt` loads from `assets/content_data.json` (4 categories, 12 items) with in-code fallback.
- Leanback TV UX: `AndroidManifest.xml` declares Leanback launcher; `MainActivity.kt` hosts Compose TV UI via `AppNav.kt`.
- Navigation: `AppNav.kt` wires HOME → DETAIL → PLAYER routes; `DetailScreen.kt` passes `videoUrl` to `PlayerScreen.kt`.

**File Map (absolute paths)**
| File | Role |
|------|------|
| `app/src/main/kotlin/com/example/netflixtv/data/ContentRepository.kt` | Loads categories/items from JSON asset; fallback on parse failure |
| `app/src/main/kotlin/com/example/netflixtv/data/Content.kt` | Data model: id, title, description, thumbnailUrl, videoUrl, etc. |
| `app/src/main/kotlin/com/example/netflixtv/data/Category.kt` | Category name + list of Content items |
| `app/src/main/kotlin/com/example/netflixtv/ui/HomeScreen.kt` | Category rows with horizontal content cards |
| `app/src/main/kotlin/com/example/netflixtv/ui/DetailScreen.kt` | Content detail with Back + Play buttons |
| `app/src/main/kotlin/com/example/netflixtv/ui/PlayerScreen.kt` | ExoPlayer playback with controls overlay |
| `app/src/main/kotlin/com/example/netflixtv/ui/AppNav.kt` | Navigation graph: HOME, DETAIL, PLAYER routes |
| `app/src/main/kotlin/com/example/netflixtv/MainActivity.kt` | Entry point; wires ContentRepository + AppNav |
| `app/src/main/AndroidManifest.xml` | Leanback launcher, TV permissions |
| `app/src/main/assets/content_data.json` | Seed data: 4 categories × 3 items + videoUrls |

**Data Flow**
```
assets/content_data.json
  → ContentRepository.loadCategories()
    → HomeScreen (renders category rows)
      → DetailScreen (on content click)
        → PlayerScreen (videoUrl fed to ExoPlayer)
```

**Gaps Identified**
- No README.md at repo root
- No CI configuration (no `.github/workflows`)
- No test infrastructure (no `src/test` or `src/androidTest`)
- Sample data is minimal (12 items across 4 categories)
- No remote data fetch; all data is local asset
- No search, watchlist, user profile, or personalization
- No explicit focus management or D-pad accessibility tuning
- No playback telemetry or buffering state UI

**External Docs (authoritative sources)**
| Doc | URL |
|-----|-----|
| Compose on Android TV | https://developer.android.com/training/tv/playback/compose |
| Compose for TV Codelab | https://developer.android.com/codelabs/compose-for-tv-introduction |
| Leanback → Compose Migration | https://developer.android.com/training/tv/playback/leanback/migrate-to-compose |
| TV Library Releases | https://developer.android.com/jetpack/androidx/releases/tv |
| TV Lists / Focus Scrolling | https://developer.android.com/training/tv/playback/compose/lists |
| ExoPlayer/Media3 Hello World | https://developer.android.com/media/media3/exoplayer/hello-world |
| TV Navigation Basics | https://developer.android.com/training/tv/get-started/navigation |
| Compose TV Onboarding | https://android.googlesource.com/platform/frameworks/support/+/HEAD/tv/onboarding.md |

Full reference list in: `.sisyphus/notepads/netflix-clone/external-docs.md`

## Work Objectives
### Core Objective
Complete the Netflix-like MVP groundwork: add root README, CI scaffolding, unit test infrastructure, richer sample data, and a concrete remote-data strategy plan.

### Deliverables
1. Root `README.md` with project overview, tech stack, and contributor guide
2. `.github/workflows/android.yml` CI workflow (build + lint + test)
3. `app/src/test/kotlin/.../ContentRepositoryTest.kt` (unit test skeleton)
4. Expanded `content_data.json` (at least 6 items per category)
5. `app/src/androidTest` Compose UI test skeleton
6. Remote data source strategy doc in notepad

### Definition of Done
- [ ] `README.md` exists at repo root and links to `AGENTS.md`
- [ ] CI workflow runs `./gradlew assembleDebug lint test` on push/PR
- [ ] `ContentRepositoryTest` covers: load from JSON, fallback on failure, category/item queries
- [ ] `content_data.json` has ≥24 items across ≥4 categories
- [ ] UI test skeleton exists with one passing Compose test
- [ ] Remote data strategy documented (API shape, caching approach, mock plan)

## Execution Strategy
### Wave 1 (Foundation – no dependencies)
- T1: Create `README.md`
- T3: Unit test skeleton for ContentRepository

### Wave 2 (Data expansion)
- T4: Expand `content_data.json` to ≥24 items
- T6: Document remote data strategy in notepad

### Wave 3 (CI + UI tests)
- T2: GitHub Actions workflow
- T5: Compose UI test skeleton

### Dependency Matrix
```
T1 (README) ──────────────────────────────→ T2 (CI)
      │                                          │
      ↓                                          ↓
T3 (unit tests) ──→ T4 (expand data) ──→ T5 (UI tests)
                           │
                           ↓
                     T6 (remote strategy)
```

## TODOs

- [x] **T1. Create root README.md** ✅
  - **What**: Add `README.md` at repo root with: project name, one-line description, tech stack (Kotlin, Compose TV, Media3/ExoPlayer), directory structure, build commands (from `AGENTS.md`), link to plan docs under `.sisyphus/plans`.
  - **Must NOT**: Add screenshots, changelog, or usage instructions beyond quick-start.
  - **References**: `AGENTS.md` for build commands; `.sisyphus/plans/` for plan doc links.
  - **Acceptance Criteria**:
    - [ ] `README.md` exists at `/vol1/1000/projects/tvlive/README.md`
    - [ ] Contains Build Commands section matching `AGENTS.md`
    - [ ] Links to `.sisyphus/plans/` plan docs
  - **QA**: `ls /vol1/1000/projects/tvlive/README.md` returns file; `grep -c "Build Commands" README.md` returns ≥1.
  - **Commit**: YES | Message: `docs(readme): add project overview and build reference`

- [x] **T2. Setup CI workflow** ✅
  - **What**: Create `.github/workflows/android.yml` running on push/PR: `./gradlew assembleDebug lint test`. Use `android-lint` and `unit-tests` report paths.
  - **Must NOT**: Add deployment steps, signing config, or multi-module matrix (single job sufficient).
  - **References**: `AGENTS.md` for Gradle commands; GitHub Actions `actions/checkout@v4`, `actions/setup-java@v4`.
  - **Acceptance Criteria**:
    - [ ] `.github/workflows/android.yml` created
    - [ ] Contains `on: [push, pull_request]` trigger
    - [ ] Runs `./gradlew assembleDebug lint test`
    - [ ] Uploads `lint-results.html` and `test-results/test` as artifacts
  - **QA**: File exists; YAML parses; `actions/workflowlint` passes (if available).
  - **Commit**: YES | Message: `ci: add Android build + lint + test workflow`

- [x] **T3. Add ContentRepository unit test skeleton** ✅
  - **What**: Create `app/src/test/kotlin/com/example/netflixtv/data/ContentRepositoryTest.kt` with:
    - `testLoadFromAsset_success` – verifies ≥4 categories loaded from JSON
    - `testLoadFromAsset_fallback` – mocks asset read failure; verifies fallback data returns ≥4 categories
    - `testGetContentById_returnsCorrectItem` – queries by known ID from fallback data
    - `testGetItemsByCategory_returnsNonEmpty` – verifies non-empty list per category
  - **Must NOT**: Add integration tests (save for T5); no network calls.
  - **References**: `ContentRepository.kt` for method signatures; `content_data.json` for data shape.
  - **Framework**: JUnit 4 + Kotlin test; mock via `kotlin.test` or inline mocks.
  - **Acceptance Criteria**:
    - [ ] File created at `app/src/test/kotlin/com/example/netflixtv/data/ContentRepositoryTest.kt`
    - [ ] All 4 test functions compile and pass against current ContentRepository
    - [ ] `testLoadFromAsset_fallback` triggers fallback by providing malformed JSON bytes
  - **QA**: `./gradlew test` passes with `ContentRepositoryTest` included.
  - **Commit**: YES | Message: `test(content): add ContentRepository unit tests`

- [x] **T4. Expand content_data.json to ≥24 items** ✅ (24 items)
  - **What**: Add at least 2 more items per existing category (Trending, Action, Comedy, Drama). Each new item needs: `id` (unique), `title`, `description`, `thumbnailUrl` (use placeholder like `https://picsum.photos/seed/{id}/400/225`), `videoUrl` (use sample bucket URLs already in use: `https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/...`), `releaseYear`, `rating`, `duration`.
  - **Must NOT**: Remove or modify existing 12 items; keep valid JSON structure.
  - **References**: `assets/content_data.json`; `Content.kt` for field list.
  - **Acceptance Criteria**:
    - [ ] Total item count ≥ 24
    - [ ] All new items have non-empty `id`, `title`, `videoUrl`
    - [ ] JSON parses without error via `ContentRepository`
  - **QA**: Parse `JSON.parse(...)` in a test or manual run; verify `ContentRepository.loadCategories().flatMap { it.items }.size ≥ 24`.
  - **Commit**: YES | Message: `data(content): expand sample catalog to 24+ items`

- [x] **T5. Compose UI test skeleton** ✅
  - **What**: Create `app/src/androidTest/kotlin/com/example/netflixtv/ui/HomeScreenTest.kt` with one passing test: `testHomeScreen_rendersCategories` – launches HomeScreen composable, waits for rendering, asserts ≥1 `LazyColumn` item exists.
  - **Must NOT**: Add full E2E (save for later); minimal passing test only.
  - **References**: `HomeScreen.kt` for composable signature; Compose testing docs.
  - **Framework**: `androidx.compose.ui.test.junit4.createComposeRule`
  - **Acceptance Criteria**:
    - [ ] `HomeScreenTest` compiles and runs via `./gradlew connectedAndroidTest`
    - [ ] One test passes confirming HomeScreen renders
  - **QA**: `./gradlew connectedAndroidTest` shows `HomeScreenTest` passing.
  - **Commit**: YES | Message: `test(ui): add HomeScreen Compose test skeleton`

- [x] **T6. Document remote data strategy** ✅
  - **What**: Append to `.sisyphus/notepads/netflix-clone/decisions.md` a section "Remote Data Strategy" covering:
    - **API surface**: `GET /categories` → `List<Category>`, `GET /content/{id}` → `Content`
    - **Caching**: Room database for offline-first; TTL-based invalidation
    - **Mock plan**: Use `MockWebServer` or in-memory fake for local dev
    - **Data class evolution**: Add `Content.remoteVideoUrl`, keep `videoUrl` for local fallback
  - **Must NOT**: Implement actual network layer; only document the approach.
  - **References**: `ContentRepository.kt`, `Content.kt`, `Category.kt`; external-docs URLs.
  - **Acceptance Criteria**:
    - [ ] Section "Remote Data Strategy" exists in `decisions.md`
    - [ ] Contains API endpoint shapes, caching approach, and mock strategy
  - **QA**: `grep -c "Remote Data Strategy" decisions.md` returns ≥1.
  - **Commit**: YES | Message: `docs: add remote data strategy approach`

## Final Verification Wave
- [x] **F1. Plan Compliance Audit — oracle** ✅
  - Verify all 6 TODOs have concrete acceptance criteria referencing actual files/commands
  - Verify no task is vague or requires human judgment to determine completeness
- [x] **F2. Code Quality Review — unspecified-high** ✅
  - All new/modified files pass `./gradlew lint`
  - No hardcoded credentials or API keys
- [x] **F3. Real Manual QA — unspecified-high** ✅
  - Run `./gradlew assembleDebug` – APK builds successfully
  - Run `./gradlew test` – ContentRepositoryTest passes
  - Manual: verify HomeScreen shows ≥4 category rows on launch
- [x] **F4. Scope Fidelity Check — deep** ✅
  - Confirm only the 6 TODOs above were implemented
  - No added dependencies beyond CI tooling
  - No new mutated files outside the plan scope

## Commit Strategy
- Commit after each TODO completion (atomic per task)
- Use conventional commit: `type(scope): desc`
- Types: `docs`, `ci`, `test`, `data`

## Success Criteria
1. `README.md` at repo root with build commands and plan links
2. CI workflow runs successfully on push/PR
3. `ContentRepositoryTest` covers asset load, fallback, and query paths
4. `content_data.json` has ≥ 24 items
5. UI test skeleton exists and compiles
6. Remote data strategy documented in decisions.md
7. All `./gradlew lint test` pass ✅
8. F1–F4 verification complete ✅

## Plan References
- **AGENTS.md**: Project standards, build commands, tech stack
- **.sisyphus/plans/netflix-clone.md**: Original detailed execution plan
- **.sisyphus/plans/netflix-clone-ui.md**: UI revamp plan
- **.sisyphus/notepads/netflix-clone/external-docs.md**: Authoritative Android TV + ExoPlayer docs
- **.sisyphus/notepads/netflix-clone/decisions.md**: Decisions log (remote strategy to be added here)
- **.sisyphus/notepads/netflix-clone/learnings.md**: Implementation learnings
