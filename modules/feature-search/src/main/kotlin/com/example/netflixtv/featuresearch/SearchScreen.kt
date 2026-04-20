package com.example.netflixtv.featuresearch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.netflixtv.data.Content
import com.example.netflixtv.uicommon.NetflixCard
import com.example.netflixtv.uicommon.TvliveColors

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onContentClick: (Content) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val inputFocusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TvliveColors.BackgroundPrimary)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search",
                    color = TvliveColors.TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                // Issue 1: Back button missing focusable - Added MutableInteractionSource pattern
                val backInteractionSource = remember { MutableInteractionSource() }
                val isBackFocused by backInteractionSource.collectIsFocusedAsState()
                val backBorderColor = if (isBackFocused) TvliveColors.Primary else TvliveColors.TextTertiary

                Text(
                    text = "← Back",
                    color = if (isBackFocused) TvliveColors.Primary else TvliveColors.TextSecondary,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .border(
                            width = if (isBackFocused) 1.dp else 0.dp,
                            color = backBorderColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .focusable(interactionSource = backInteractionSource)
                        .clickable { onBackClick() }
                )
            }

            // Issue 2: TextField missing focusable - Added .focusable() modifier
            TextField(
                value = uiState.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(inputFocusRequester)
                    .focusable(),
                placeholder = {
                    Text("Search titles...", color = TvliveColors.TextTertiary)
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = TvliveColors.TextPrimary,
                    unfocusedTextColor = TvliveColors.TextPrimary,
                    focusedContainerColor = TvliveColors.BackgroundElevated,
                    unfocusedContainerColor = TvliveColors.BackgroundSecondary,
                    cursorColor = TvliveColors.Primary,
                    focusedIndicatorColor = TvliveColors.Primary,
                    unfocusedIndicatorColor = TvliveColors.BackgroundElevated
                )
            )

            if (uiState.isSearching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TvliveColors.Primary)
                }
            } else if (uiState.query.isNotBlank()) {
                Text(
                    text = "${uiState.results.size} results",
                    color = TvliveColors.TextSecondary,
                    fontSize = 14.sp
                )

                if (uiState.results.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found",
                            color = TvliveColors.TextSecondary,
                            fontSize = 18.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.results,
                            key = { it.id }
                        ) { content ->
                            NetflixCard(
                                content = content,
                                onClick = { onContentClick(content) }
                            )
                        }
                    }
                }
            }
        }
    }
}
