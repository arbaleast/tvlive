package com.example.netflixtv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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

    val firstCardFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        firstCardFocusRequester.requestFocus()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
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

        itemsIndexed(categories) { index, category ->
            CategoryRow(
                category = category,
                onContentClick = onContentClick,
                isFirstRow = index == 0,
                firstCardFocusRequester = firstCardFocusRequester
            )
        }
    }
}

@Composable
private fun CategoryRow(
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
            itemsIndexed(category.items) { index, content ->
                NetflixCard(
                    content = content,
                    onClick = { onContentClick(content) },
                    modifier = if (isFirstRow && index == 0) {
                        Modifier.focusRequester(firstCardFocusRequester)
                    } else {
                        Modifier
                    }
                )
            }
        }
    }
}
