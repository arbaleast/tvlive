Task 2: Created mock data source with 4 categories (Trending, Action, Comedy, Drama) with 3 items each (12 total). Used org.json JSONObject/JSONArray parsing with fallback hardcoded data.

Task 3: Implemented HomeScreen with 4 category rows (LazyColumn/LazyRow), DetailScreen with backdrop image and metadata, PlayerScreen with ExoPlayer. Navigation via Compose Navigation.

Task 4: PlayerScreen implemented with ExoPlayer, lifecycle-aware playback (pause on lifecycle pause, resume on resume, release on dispose).

Task (Coil): Added Coil image loading library (coil-compose:2.5.0) to build.gradle.kts. Updated HomeScreen.kt and DetailScreen.kt to use AsyncImage for network thumbnail loading.

Task (Navigation): Integrated PlayerScreen into navigation. Added PLAYER route to Routes object, added onPlayClick callback to DetailScreen, added red "Play" button that navigates to PlayerScreen with BigBuckBunny video URL.

Task (Back Button): Added "← Back" button at top-left of DetailScreen. Uses onBackClick callback to navigate back via popBackStack(). Gray semi-transparent button for visibility without obstructing content.

Task (Per-Content Video): Added videoUrl field to Content data class. Each content now has its own video URL (BigBuckBunny, Sintel, ElephantsDream, ForBiggerBlazes, etc.). Updated ContentRepository and content_data.json.

Task (Player Back Button): Added "← Back" button at top-left of PlayerScreen. Navigates back to DetailScreen via onBackClick.

Task (Poster Image): Added posterUrl parameter to PlayerScreen. Shows poster/thumbnail image at 30% opacity before video starts playing. Uses AsyncImage with Coil.

Task (Skip Controls): Added skip backward (⏪ -10s) and skip forward (⏩ +10s) buttons at bottom center. Added play/pause (▶/⏸) button. Red circular buttons for play/pause, gray for skip controls.

Netflix Clone UI Plan Updates:
- Added HeroBanner.kt: Full-width backdrop with gradient overlays, title, description, red "Play" button, gray "More Info" button. Netflix-style hero presentation.
- Added NetflixCard.kt: Poster card with scale animation (1.08x on focus), red 3dp border on focus. Uses MutableInteractionSource for focus detection.
- Updated Content.kt: Added backdropUrl field (separate from thumbnailUrl for hero/backdrop images).
- Updated content_data.json: Updated category names (Trending Shows, TV Shows, Movies, Live TV) and added backdropUrl to all 12 items.
- Updated HomeScreen.kt: Integrated HeroBanner at top, 4 horizontal carousels with NetflixCard items, focus management with FocusRequester.
- Updated DetailScreen.kt: Changed to use backdropUrl (fallback to thumbnailUrl) for large backdrop image.
- Updated AppNav.kt: Player receives backdropUrl as posterUrl parameter.

** 2026-04-12: All Netflix UI components complete - build passes.

---

## Remote Data Strategy (T6)

### API Surface
- `GET /categories` → Returns `List<Category>` with name and items
- `GET /content/{id}` → Returns single `Content` by ID
- `GET /content/featured` → Returns featured/trending content

### Caching Approach
- **Room Database**: Local SQLite for offline-first storage
- **TTL-based invalidation**: Cache expires after 24 hours
- **On-demand refresh**: Pull-to-refresh or on app resume

### Mock Strategy (Development)
- Use `MockWebServer` (OkHttp) for local API mocking
- Or in-memory fake repository for UI testing
- Production: Replace with real API endpoint

### Data Class Evolution
- Keep existing `videoUrl` as local fallback
- Add `remoteVideoUrl` for remote content
- Prefer remote if available, fallback to local