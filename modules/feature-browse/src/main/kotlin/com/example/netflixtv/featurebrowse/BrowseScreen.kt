package com.example.netflixtv.featurebrowse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.netflixtv.data.Category
import com.example.netflixtv.data.Content
import com.example.netflixtv.uicommon.NetflixCard
import com.example.netflixtv.uicommon.TvliveColors

@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    onContentClick: (Content) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val catalogs = viewModel.getAvailableCatalogs()
    val firstCardFocusRequester = remember { FocusRequester() }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(TvliveColors.BackgroundPrimary),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TvliveColors.Primary)
            }
        }
        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize().background(TvliveColors.BackgroundPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: ${uiState.error}", color = TvliveColors.AccentLive)
            }
        }
        else -> {
            LaunchedEffect(uiState.categories) {
                if (uiState.categories.isNotEmpty()) {
                    try {
                        firstCardFocusRequester.requestFocus()
                    } catch (_: IllegalStateException) {
                        // FocusRequester not yet attached
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TvliveColors.BackgroundPrimary)
                    .padding(20.dp)
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(28.dp)) {
                    item {
                        BrowseHeader(
                            catalogs = catalogs,
                            currentCatalog = uiState.currentCatalog,
                            isSwitching = uiState.catalogSwitchInProgress,
                            onBackClick = onBackClick,
                            onCatalogSwitch = { viewModel.switchCatalog(it) }
                        )
                    }

                    itemsIndexed(
                        items = uiState.categories,
                        key = { _, cat -> cat.name }
                    ) { index, category ->
                        BrowseCategoryRow(
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
}

@Composable
private fun BrowseHeader(
    catalogs: List<String>,
    currentCatalog: String,
    isSwitching: Boolean,
    onBackClick: () -> Unit,
    onCatalogSwitch: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onBackClick = onBackClick)

            Text(
                text = "Browse",
                color = TvliveColors.TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(60.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            catalogs.forEach { catalog ->
                CatalogChip(
                    catalog = catalog,
                    isSelected = catalog == currentCatalog,
                    enabled = !isSwitching,
                    onClick = { onCatalogSwitch(catalog) }
                )
            }
        }
    }
}

@Composable
private fun BackButton(onBackClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .then(
                if (isFocused) Modifier.border(
                    2.dp,
                    TvliveColors.Primary,
                    RoundedCornerShape(10.dp)
                ) else Modifier
            )
            .clip(RoundedCornerShape(10.dp))
            .focusable(interactionSource = interactionSource)
            .clickable { onBackClick() }
            .padding(10.dp)
    ) {
        Text(text = "← Back", color = TvliveColors.TextSecondary, fontSize = 16.sp)
    }
}

@Composable
private fun CatalogChip(
    catalog: String,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val bgColor = when {
        isFocused && isSelected -> TvliveColors.Primary
        isSelected -> TvliveColors.Primary.copy(alpha = 0.8f)
        isFocused -> TvliveColors.BackgroundElevated
        else -> TvliveColors.BackgroundSecondary
    }
    val borderColor = when {
        isFocused -> TvliveColors.Primary
        isSelected -> TvliveColors.Primary.copy(alpha = 0.5f)
        else -> TvliveColors.TextTertiary.copy(alpha = 0.5f)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(bgColor)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(22.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text = catalog.uppercase(),
            color = TvliveColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun BrowseCategoryRow(
    category: Category,
    onContentClick: (Content) -> Unit,
    isFirstRow: Boolean = false,
    firstCardFocusRequester: FocusRequester? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.name,
                color = TvliveColors.TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            SeeAllButton(category = category)
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(
                items = category.items,
                key = { it.id }
            ) { content ->
                NetflixCard(
                    content = content,
                    onClick = { onContentClick(content) },
                    modifier = if (isFirstRow && firstCardFocusRequester != null && category.items.firstOrNull()?.id == content.id) {
                        Modifier.focusRequester(firstCardFocusRequester)
                    } else Modifier
                )
            }
        }
    }
}

@Composable
private fun SeeAllButton(category: Category) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .then(
                if (isFocused) Modifier.border(
                    2.dp,
                    TvliveColors.Primary,
                    RoundedCornerShape(6.dp)
                ) else Modifier
            )
            .clip(RoundedCornerShape(6.dp))
            .focusable(interactionSource = interactionSource)
            .padding(6.dp)
    ) {
        Text(
            text = "See All (${category.items.size})",
            color = TvliveColors.Primary.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
