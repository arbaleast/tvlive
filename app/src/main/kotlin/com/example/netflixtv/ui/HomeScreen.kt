package com.example.netflixtv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onContentClick: (Content) -> Unit,
    onHeroCtaClick: (Content) -> Unit,
    onSearchClick: () -> Unit = {},
    onBrowseClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val firstCardFocusRequester = remember { FocusRequester() }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.Red)
        }
        return
    }

    uiState.error?.let { error ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error: $error", color = Color.Red)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "TV Live",
                    color = Color.Red,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "🔍",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.focusable().padding(8.dp)
                    )
                    Text(
                        text = "☰",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.focusable().padding(8.dp)
                    )
                }
            }
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

@Composable
private fun HomeCategoryRow(
    category: Category,
    onContentClick: (Content) -> Unit,
    isFirstRow: Boolean,
    firstCardFocusRequester: FocusRequester
) {
    val listState = rememberLazyListState()

    Column {
        Text(
            text = category.name,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(
                items = category.items,
                key = { it.id }
            ) { content ->
                NetflixCard(
                    content = content,
                    onClick = { onContentClick(content) },
                    modifier = if (isFirstRow && category.items.indexOf(content) == 0) {
                        Modifier.focusRequester(firstCardFocusRequester)
                    } else {
                        Modifier
                    }
                )
            }
        }
    }
}