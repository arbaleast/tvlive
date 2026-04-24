package com.example.netflixtv.uicommon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Shared loading content component displayed while data is being fetched.
 * Used across HomeScreen, BrowseScreen, and other screens with async data loading.
 */
@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TvliveColors.BackgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = TvliveColors.Primary,
            strokeWidth = 5.dp,
            modifier = Modifier.size(56.dp)
        )
    }
}