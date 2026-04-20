package com.example.netflixtv.featurehome

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.netflixtv.data.Category
import com.example.netflixtv.data.Content
import com.example.netflixtv.uicommon.DpadFocusable
import com.example.netflixtv.uicommon.HeroBanner
import com.example.netflixtv.uicommon.NetflixCard
import com.example.netflixtv.uicommon.TvliveColors

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onContentClick: (Content) -> Unit,
    onHeroCtaClick: (Content) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val firstCardFocusRequester = remember { FocusRequester() }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(TvliveColors.BackgroundPrimary),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = TvliveColors.Primary,
                    strokeWidth = 3.dp
                )
            }
        }
        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize().background(TvliveColors.BackgroundPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = TvliveColors.AccentLive,
                    fontSize = 16.sp
                )
            }
        }
        else -> {
            LaunchedEffect(uiState.categories) {
                if (uiState.categories.isNotEmpty()) {
                    try {
                        firstCardFocusRequester.requestFocus()
                    } catch (_: IllegalStateException) {
                        // FocusRequester not yet attached — safe to ignore
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TvliveColors.BackgroundPrimary)
                    .padding(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                item {
                    HomeHeader()
                }

                uiState.heroes.firstOrNull()?.let { hero ->
                    item {
                        HeroBanner(
                            imageUrl = hero.backdropUrl.ifEmpty { hero.thumbnailUrl },
                            title = hero.title,
                            description = hero.description,
                            onCtaClick = { onHeroCtaClick(hero) }
                        )
                    }
                }

                itemsIndexed(uiState.categories) { index, category ->
                    HomeCategoryRow(
                        category = category,
                        onContentClick = onContentClick,
                        isFirstRow = index == 0,
                        firstCardFocusRequester = firstCardFocusRequester
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TV Live",
            color = TvliveColors.Primary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Search icon
            DpadFocusable(
                modifier = Modifier.size(44.dp),
                cornerRadius = 10.dp,
                focusBorderColor = TvliveColors.Primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🔍",
                        fontSize = 18.sp
                    )
                }
            }

            // Menu icon
            DpadFocusable(
                modifier = Modifier.size(44.dp),
                cornerRadius = 10.dp,
                focusBorderColor = TvliveColors.Primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "☰",
                        color = TvliveColors.TextPrimary,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeCategoryRow(
    category: Category,
    onContentClick: (Content) -> Unit,
    isFirstRow: Boolean,
    firstCardFocusRequester: FocusRequester
) {
    Column {
        Text(
            text = category.name,
            color = TvliveColors.TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            itemsIndexed(
                items = category.items,
                key = { _, content -> content.id },
                contentType = { _, _ -> "content_card" }
            ) { _, content ->
                NetflixCard(
                    content = content,
                    onClick = { onContentClick(content) },
                    modifier = if (isFirstRow && category.items.firstOrNull()?.id == content.id) {
                        Modifier.focusRequester(firstCardFocusRequester)
                    } else {
                        Modifier
                    }
                )
            }
        }
    }
}
