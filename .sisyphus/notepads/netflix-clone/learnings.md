Task 2: ContentRepository loads from assets/content_data.json. Falls back to hardcoded sample data if asset missing or JSON parse fails. Data classes: Content (id, title, description, thumbnailUrl, category, releaseYear, rating, duration) and Category (name, items).

Task 3: HomeScreen uses ContentRepository to load categories and items. DetailScreen receives content via navigation argument. PlayerScreen uses ExoPlayer with lifecycle-aware playback.

Task 4: ExoPlayer integration with Media3. Mock video URLs: BigBuckBunny and Sintel from Google sample bucket. PlayerView embedded via AndroidView.

Coil Integration: Added coil-compose:2.5.0 dependency. AsyncImage replaces gray placeholder Box in HomeScreen (thumbnail) and DetailScreen (backdrop). ContentScale.Crop for proper aspect ratio handling.

Navigation Integration: Routes object holds HOME, DETAIL, PLAYER routes. DetailScreen passes onPlayClick callback. PlayerScreen receives contentId from navigation argument and loads BigBuckBunny video. Back navigation via popBackStack().

Back Button: Added "← Back" Button at top-left corner of DetailScreen. Positioned via Box + align(Alignment.TopStart). Uses onBackClick to trigger popBackStack(). Gray semi-transparent background for visibility on dark backdrop.

Per-Content Video: Content data class now includes videoUrl field. Each content item has unique video URL from Google's sample bucket. Repository parses videoUrl from JSON, falls back to hardcoded URLs if parsing fails.

Player Back Button: PlayerScreen now has "← Back" Button at top-left alongside title. Navigates back via onBackClick callback.

Poster Image: PlayerScreen accepts posterUrl parameter. AsyncImage shows poster at 30% opacity when isPlaying=false. Hides when video plays (alpha=0). Provides visual context before playback.

Skip Controls: PlayerScreen has Row of circular buttons at bottom center: skip backward (⏪ -10s), play/pause (▶/⏸), skip forward (⏩ +10s). Uses exoPlayer.seekTo(currentPosition ± 10000). isPlaying state tracks playback status.

** 2026-04-12 Netflix Clone UI Updates:
- HeroBanner: 400dp height, vertical gradient (transparent to 70% black), horizontal gradient (80% black to transparent), 48sp bold title, 16sp description, red Play button with focusable modifier.
- NetflixCard: 150dp x 280dp (2:3 ratio), animateFloatAsState 1.08x scale on focus, 3dp red border via Modifier.border() on isFocused, interactionSource.collectIsFocusedAsState() for focus detection.
- Content model: backdropUrl added as separate field from thumbnailUrl - allows distinct hero/backdrop (16:9) vs poster (2:3) images.
- Category names: Trending Shows, TV Shows, Movies, Live TV - matches Netflix content organization.
- HomeScreen: HeroBanner first item in LazyColumn, LazyRow per category with NetflixCard, FocusRequester on first card for initial focus.
- DetailScreen: backdropUrl fallback to thumbnailUrl for large background image.
- Navigation: PlayerScreen receives backdropUrl as posterUrl param.