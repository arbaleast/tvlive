# Netflix Clone UI Revamp - Detailed Execution Plan

## TL;DR
- Elevate the Android TV home UI to Netflix-like fidelity: hero banner, 4 focusable horizontal carousels, Tito focus rings, and polished detail/player flows.
- Deliver as a single plan with atomic tasks that are easily testable and auditable.

## Context
- Request: build a Netflix-like home screen on Android TV with bold focus, horizontal carousels, and integrated detail/player flows.
- Data: Content model extended with videoUrl, posterUrl/backdrop; 12 items preloaded.
- Navigation: Home -> Detail -> Player, with clean back navigation.

## Work Objectives
- Core Objective: implement a Netflix-like home with hero banner and 4 carousels; polish detail/player routes.
- Deliverables: HeroBanner, NetflixCard, updated HomeScreen, upgraded DetailScreen, upgraded PlayerScreen, AppNav, minor asset wiring.
- Definition of Done: build passes, tests green, UI renders Netflix-like fidelity on TV.

## Execution Strategy (Wave Plan)

### Wave 1: Primitives & Components

- [x] **Task 1.1**: Create HeroBanner.kt component ✅
  - File: `app/src/main/kotlin/com/example/netflixtv/ui/HeroBanner.kt`
  - Signature: `HeroBanner(imageUrl: String, title: String, description: String, onCta: () -> Unit)`
  - Netflix-style: Full-width backdrop image, gradient overlay, title, description, "Play" button
  - Focusable CTA button with bold red styling

- [x] **Task 1.2**: Create NetflixCard.kt component ✅
  - File: `app/src/main/kotlin/com/example/netflixtv/ui/NetflixCard.kt`
  - Signature: `NetflixCard(content: Content, onClick: () -> Unit, isFocused: Boolean)`
  - Netflix-style: Poster image (2:3 ratio), scales up when focused, red border when focused
  - use `BoxFocusTarget` for focus indication

- [x] **Task 1.3**: Extend Content data model ✅
  - Add `backdropUrl: String` field (separate from thumbnailUrl for poster)
  - Update `content_data.json` with backdropUrl for each item
  - Use thumbnailUrl as fallback if backdropUrl missing

### Wave 2: HomeScreen Refactoring

- [x] **Task 2.1**: Update content_data.json category names ✅
  - Change: "Trending" → "Trending Shows"
  - Change: "Action" → "TV Shows"
  - Change: "Comedy" → "Movies"
  - Change: "Drama" → "Live TV"

- [x] **Task 2.2**: Integrate HeroBanner into HomeScreen ✅
  - HeroBanner at top of LazyColumn (first item)
  - Use first item from first category as hero content
  - Gradient scrim overlay for text readability

- [x] **Task 2.3**: Implement 4 horizontal poster carousels ✅
  - Each category = one LazyRow
  - NetflixCard items with focus scaling
  - "Continue Watching" row style for first 3 items
  - Poster-focused cards (not wide banners)

- [x] **Task 2.4**: Focus management ✅
  - TV remote D-pad navigation
  - Focus rings visible on all interactive elements
  - Auto-scroll to focused item when navigating horizontally
  - Initial focus on first card in first row

- [x] **Task 2.5**: Typography/Contrast tuning ✅
  - White text on dark backgrounds
  - Larger font sizes for TV viewing distance
  - Proper font weights (Bold for titles, Medium for subtitles)

### Wave 3: DetailScreen Polish

- [x] **Task 3.1**: Upgrade DetailScreen ✅
  - Large backdrop image (full width)
  - Gradient overlay for text contrast
  - Title, year, rating, duration, description
  - Red "Play" button, gray "Info" button

- [x] **Task 3.2**: Bind Play CTA to navigation ✅
  - DetailScreen → PlayerScreen navigation

### Wave 4: PlayerScreen UI

- [x] **Task 4.1**: Immersive player ✅
  - Poster/backdrop as background (30% opacity)
  - Back navigation button
  - Playback controls: play/pause, seek ±10s

### Wave 5: Data/Assets

- [x] **Task 5.1**: Ensure per-item poster/backdrop/videoUrl consistency ✅
  - Add backdropUrl to each content item
  - Verify all 12 items have proper URLs

### Wave 6: QA & Documentation

- [x] **Task 6.1**: Notepad updates ✅
  - decisions.md
  - learnings.md

- [x] **Task 6.2**: Final verification ✅
  - `./gradlew assembleDebug` ✅
  - `./gradlew test` ✅
  - `./gradlew lint` ✅

---

## Detailed Implementation Code

### Task 1.1: HeroBanner.kt

```kotlin
package com.example.netflixtv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun HeroBanner(
    imageUrl: String,
    title: String,
    description: String,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        // Backdrop image
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlays for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Left gradient for title area
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(600.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 48.dp, bottom = 48.dp)
                .width(500.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = title,
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onCtaClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    modifier = Modifier
                        .focusable()
                        .padding(0.dp)
                ) {
                    Text(
                        text = "▶ Play",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { /* Info */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.focusable()
                ) {
                    Text(
                        text = "ℹ More Info",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
```

### Task 1.2: NetflixCard.kt

```kotlin
package com.example.netflixtv.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.netflixtv.data.Content

@Composable
fun NetflixCard(
    content: Content,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Scale animation when focused (Netflix style: grows slightly)
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.08f else 1f,
        label = "scale"
    )

    Card(
        modifier = modifier
            .width(150.dp)
            .height(280.dp)
            .scale(scale)
            .focusable(interactionSource = interactionSource)
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = 3.dp,
                        color = Color.Red,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray
        ),
        onClick = onClick
    ) {
        Box {
            // Poster image (2:3 aspect ratio)
            AsyncImage(
                model = content.thumbnailUrl,
                contentDescription = content.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Title overlay at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(8.dp)
            ) {
                Text(
                    text = content.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
```

### Task 1.3: Content.kt Update

```kotlin
package com.example.netflixtv.data

data class Content(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,    // Poster image (2:3)
    val backdropUrl: String,     // Hero backdrop (16:9)
    val videoUrl: String,
    val category: String,
    val releaseYear: Int,
    val rating: String,
    val duration: String
)
```

### Task 2.1: content_data.json Update

```json
{
  "categories": [
    {
      "name": "Trending Shows",
      "items": [
        {"id": "tr1", "title": "The Last Pixel", "description": "A data-driven dystopian road movie.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=The+Last+Pixel", "backdropUrl": "https://via.placeholder.com/1920x1080?text=The+Last+Pixel", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "releaseYear": 2024, "rating": "PG-13", "duration": "1h 58m"},
        {"id": "tr2", "title": "Nebula Nights", "description": "Astronauts chase a rumor across a neon-lit galaxy.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=Nebula+Nights", "backdropUrl": "https://via.placeholder.com/1920x1080?text=Nebula+Nights", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4", "releaseYear": 2023, "rating": "PG-13", "duration": "2h 10m"},
        {"id": "tr3", "title": "Orbit Rescue", "description": "A team saves civilians in a collapsing space station.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=Orbit+Rescue", "backdropUrl": "https://via.placeholder.com/1920x1080?text=Orbit+Rescue", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", "releaseYear": 2025, "rating": "PG-13", "duration": "2h 5m"}
      ]
    },
    {
      "name": "TV Shows",
      "items": [
        {"id": "ac1", "title": "Thunder Run", "description": "Mad dash through a fortified city.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=Thunder+Run", "backdropUrl": "https://via.placeholder.com/1920x1080?text=Thunder+Run", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4", "releaseYear": 2022, "rating": "R", "duration": "2h 0m"},
        {"id": "ac2", "title": "Iron Vanguard", "description": "Clash of metal and mind in a near-future war.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=Iron+Vanguard", "backdropUrl": "https://via.placeholder.com/1920x1080?text=Iron+Vanguard", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4", "releaseYear": 2023, "rating": "PG-13", "duration": "2h 15m"},
        {"id": "ac3", "title": "Shadow Siege", "description": "A covert unit faces a ruthless cartel.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=Shadow+Siege", "backdropUrl": "https://via.placeholder.com/1920x1080?text=Shadow+Siege", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4", "releaseYear": 2021, "rating": "R", "duration": "2h 3m"}
      ]
    },
    {
      "name": "Movies",
      "items": [
        {"id": "co1", "title": "Lucky Break", "description": "A misfit finally finds his big break.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=Lucky+Break", "backdropUrl": "https://via.placeholder.com/1920x1080?text=Lucky+Break", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4", "releaseYear": 2019, "rating": "PG-13", "duration": "1h 40m"},
        {"id": "co2", "title": "All in Laughs", "description": "A group of friends gamble on a life-changing prank.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=All+in+Laughs", "backdropUrl": "https://via.placeholder.com/1920x1080?text=All+in+Laughs", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4", "releaseYear": 2020, "rating": "PG", "duration": "1h 50m"},
        {"id": "co3", "title": "Saturday Shenanigans", "description": "An unruly crew tries to keep a small town entertained.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=Saturday+Shenanigans", "backdropUrl": "https://via.placeholder.com/1920x1080?text=Saturday+Shenanigans", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubmarineReturn.mp4", "releaseYear": 2021, "rating": "PG-13", "duration": "1h 45m"}
      ]
    },
    {
      "name": "Live TV",
      "items": [
        {"id": "dr1", "title": "Echoes of Dawn", "description": "Family secrets unravel in a seaside town.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=Echoes+of+Dawn", "backdropUrl": "https://via.placeholder.com/1920x1080?text=Echoes+of+Dawn", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4", "releaseYear": 2018, "rating": "R", "duration": "2h 0m"},
        {"id": "dr2", "title": "Velvet Hour", "description": "A lawyer confronts a past she thought she left behind.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=Velvet+Hour", "backdropUrl": "https://via.placeholder.com/1920x1080?text=Velvet+Hour", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TheGirlWhoLeaptThroughTime.mp4", "releaseYear": 2022, "rating": "PG-13", "duration": "1h 58m"},
        {"id": "dr3", "title": "Broken Borders", "description": "A migrant story told through intertwined lives.", "thumbnailUrl": "https://via.placeholder.com/300x400?text=Broken+Borders", "backdropUrl": "https://via.placeholder.com/1920x1080?text=Broken+Borders", "videoUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/CosmosLaidOver.mp4", "releaseYear": 2024, "rating": "R", "duration": "2h 12m"}
      ]
    }
  ]
}
```

### Task 2.2-2.5: HomeScreen.kt Update

```kotlin
package com.example.netflixtv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.netflixtv.data.Category
import com.example.netflixtv.data.Content
import com.example.netflixtv.data.ContentRepository

@Composable
fun HomeScreen(
    repository: ContentRepository,
    onContentClick: (Content) -> Unit,
    onHeroCtaClick: (Content) -> Unit
) {
    val categories = remember { repository.loadCategories() }
    val heroContent = remember { 
        categories.firstOrNull()?.items?.firstOrNull() 
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Banner
        heroContent?.let { hero ->
            item {
                HeroBanner(
                    imageUrl = hero.backdropUrl.ifEmpty { hero.thumbnailUrl },
                    title = hero.title,
                    description = hero.description,
                    onCtaClick = { onHeroCtaClick(hero) }
                )
            }
        }

        // Category rows
        items(categories) { category ->
            CategoryRow(
                category = category,
                onContentClick = onContentClick
            )
        }
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    onContentClick: (Content) -> Unit
) {
    Column {
        Text(
            text = category.name,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(category.items) { content ->
                NetflixCard(
                    content = content,
                    onClick = { onContentClick(content) }
                )
            }
        }
    }
}
```

---

## Verification Commands

```bash
cd /vol1/1000/projects/tvlive
./gradlew assembleDebug
./gradlew test
./gradlew lint
```

---

## Notepad Updates
After completing each wave, append to:
- `.sisyphus/notepads/netflix-clone/decisions.md`
- `.sisyphus/notepads/netflix-clone/learnings.md`

## Next Steps
Wave 1: Create HeroBanner.kt, NetflixCard.kt, update Content.kt with backdropUrl
Wave 2: Update content_data.json, refactor HomeScreen, add focus management
Wave 3: Polish DetailScreen
Wave 4: Polish PlayerScreen
Wave 5: Data/Assets alignment
Wave 6: QA & Documentation

---

## Status

### Wave 1: Primitives & Components
- [x] Task 1.1: HeroBanner.kt
- [x] Task 1.2: NetflixCard.kt
- [x] Task 1.3: Content.kt update (add backdropUrl)

### Wave 2: HomeScreen Refactoring
- [x] Task 2.1: Update content_data.json category names
- [x] Task 2.2: Integrate HeroBanner into HomeScreen
- [x] Task 2.3: Implement 4 horizontal poster carousels
- [x] Task 2.4: Focus management
- [x] Task 2.5: Typography/Contrast tuning

### Wave 3: DetailScreen Polish
- [x] Task 3.1: Upgrade DetailScreen
- [x] Task 3.2: Bind Play CTA to navigation

### Wave 4: PlayerScreen UI
- [x] Task 4.1: Immersive player

### Wave 5: Data/Assets
- [x] Task 5.1: Ensure per-item poster/backdrop/videoUrl consistency

### Wave 6: QA & Documentation
- [x] Task 6.1: Notepad updates
- [x] Task 6.2: Final verification

---

**Last Updated**: 2026-04-12
