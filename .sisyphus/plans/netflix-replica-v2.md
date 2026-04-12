# Netflix Replica V2 - Search & Browse MVP

## TL;DR

Add Search screen, Browse screen, and second content catalog to the existing Netflix-style Android TV app. Focus on TV-optimized D-pad navigation with minimal UI.

## Context

### Already Implemented (via netflix-clone, netflix-clone-ui)
- HomeScreen with HeroBanner + 4 category rows
- DetailScreen with backdrop + metadata
- PlayerScreen with ExoPlayer
- 24 content items (4 categories)
- CI/CD, unit tests

### Remaining from Original Vision
- Search UI skeleton
- Browse UI skeleton
- Second mock catalog (for multi-source simulation)

## Work Objectives

### Core Objective
Complete the Netflix Replica MVP by adding Search and Browse discovery paths plus a second content catalog.

### Concrete Deliverables
- SearchScreen with text input and live filtering
- BrowseScreen showing all categories as browsable sections
- Second content catalog (content_data_v2.json) with different genre mix
- Navigation integration (Search/Browse accessible from Home)

### Definition of Done
- Search filters content by title in real-time
- Browse shows all categories with horizontal scrolling
- Second catalog provides alternative content source
- All screens support D-pad navigation

### Must Have
- Search accessible via on-screen button or key press
- Browse accessible via on-screen button or key press
- Back navigation returns to previous screen

### Must NOT Have
- Real search backend/API
- Content filtering by genre/metadata
- User preferences or watch history

---

## TODOs

---

- [x] **Task 1.1**: Add Search icon button to HomeScreen top bar ✅
  - File: `app/src/main/kotlin/com/example/netflixtv/ui/HomeScreen.kt`
  - Position: Top-right corner, gray magnifying glass icon
  - On click: Navigate to SearchScreen

- [x] **Task 1.2**: Create SearchScreen.kt composable ✅
  - File: `app/src/main/kotlin/com/example/netflixtv/ui/SearchScreen.kt`
  - State: `searchQuery: String`, `filteredContent: List<Content>`
  - Layout: Top search input field, grid of matching content below
  - Live filter: `repository.getAllContent().filter { it.title.contains(query, ignoreCase=true) }`

- [x] **Task 1.3**: Add SearchScreen to navigation ✅
  - File: `app/src/main/kotlin/com/example/netflixtv/ui/AppNav.kt`
  - Route: `SEARCH` with `searchQuery` argument (optional pre-fill)
  - Back: Returns to HomeScreen

---

- [x] **Task 2.1**: Add Browse icon button to HomeScreen top bar ✅
  - File: `app/src/main/kotlin/com/example/netflixtv/ui/HomeScreen.kt`
  - Position: Next to Search icon, grid/list icon
  - On click: Navigate to BrowseScreen

- [x] **Task 2.2**: Create BrowseScreen.kt composable ✅
  - File: `app/src/main/kotlin/com/example/netflixtv/ui/BrowseScreen.kt`
  - Layout: Vertical list of all categories, each expandable to show all items
  - Each category: Title + "See All" row that expands to full LazyRow
  - Use existing NetflixCard for items

- [x] **Task 2.3**: Add BrowseScreen to navigation ✅
  - File: `app/src/main/kotlin/com/example/netflixtv/ui/AppNav.kt`
  - Route: `BROWSE`
  - Back: Returns to HomeScreen

---

- [x] **Task 3.1**: Create second content catalog ✅ (13 items)
  - File: `app/src/main/assets/content_data_v2.json`
  - Structure: Same as content_data.json (categories array)
  - Categories: "Documentaries", "Anime", "International", "Originals"
  - Items: 3-4 items per category (12-16 total)
  - Use placeholder images + sample videos

- [x] **Task 3.2**: Update ContentRepository to support multiple catalogs ✅
  - Add `catalogName: String = "default"` parameter
  - Load from `content_data_{catalogName}.json`
  - Add `getAvailableCatalogs(): List<String>` method

- [x] **Task 3.3**: Add catalog switcher to BrowseScreen ✅
  - Show catalog names as chips/tabs at top
  - On select: Reload content from selected catalog
  - Default to "default" catalog

---

## Final Verification Wave

- [x] **F1. Build verification** ✅
  - `./gradlew assembleDebug` → BUILD SUCCESSFUL
  - `./gradlew test` → BUILD SUCCESSFUL
  - `./gradlew lint` → BUILD SUCCESSFUL

- [x] **F2. Navigation test** ✅
  - Home → Search → type "The" → results show matching titles
  - Home → Browse → scroll through categories → all content visible
  - Back from Search/Browse → returns to Home

- [x] **F3. Catalog switch test** ✅
  - Browse → switch to "v2" catalog → content changes to second catalog
  - Verify 4 different category names appear ✅

---

## Commit Strategy

Single commit: `feat(netflix-replica): add search, browse, and second content catalog`

---

## Success Criteria

1. Search screen filters content by title in real-time
2. Browse screen shows all categories with expandable rows
3. Second content catalog loads independently
4. D-pad navigation works on all new screens
5. All builds pass