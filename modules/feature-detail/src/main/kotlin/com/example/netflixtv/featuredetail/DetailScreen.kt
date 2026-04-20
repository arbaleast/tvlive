package com.example.netflixtv.featuredetail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.painter.ColorPainter
import com.example.netflixtv.data.Content

import com.example.netflixtv.uicommon.TvliveColors

private val BackdropGradient = listOf(
    Color.Black.copy(alpha = 0.85f),
    Color.Black.copy(alpha = 0.4f),
    Color.Transparent,
    Color.Black.copy(alpha = 0.9f),
)

@Composable
fun DetailScreen(
    content: Content,
    onBackClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    val playInteractionSource = remember { MutableInteractionSource() }
    val isPlayFocused by playInteractionSource.collectIsFocusedAsState()
    val backInteractionSource = remember { MutableInteractionSource() }
    val isBackFocused by backInteractionSource.collectIsFocusedAsState()

    // Scale animation for play button focus
    val playScale by animateFloatAsState(
        targetValue = if (isPlayFocused) 1.06f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "playScale"
    )

    val backScale by animateFloatAsState(
        targetValue = if (isBackFocused) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "backScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TvliveColors.BackgroundPrimary)
    ) {
        // Backdrop image with fade
        AsyncImage(
            model = content.backdropUrl.ifEmpty { content.thumbnailUrl },
            contentDescription = content.title,
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.6f,
            placeholder = ColorPainter(TvliveColors.BackgroundPrimary),
            error = ColorPainter(TvliveColors.BackgroundPrimary)
        )

        // Full-screen gradient scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(colors = BackdropGradient)
                )
        )

        // Left-side scrim for text readability (same as HeroBanner)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(600.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            TvliveColors.BackgroundPrimary.copy(alpha = 0.95f),
                            TvliveColors.BackgroundPrimary.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.TopStart)
        ) {
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TvliveColors.BackgroundElevated.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .scale(backScale)
                    .then(
                        if (isBackFocused) Modifier.border(
                            width = 2.dp,
                            color = TvliveColors.FocusBorder,
                            shape = RoundedCornerShape(8.dp)
                        ) else Modifier
                    ),
                interactionSource = backInteractionSource
            ) {
                Text(
                    text = "\u2190 Back",
                    color = TvliveColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Main content — left-aligned
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(580.dp)
                .padding(start = 56.dp, bottom = 64.dp)
                .align(Alignment.CenterStart),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.15f))

            // Title
            Text(
                text = content.title,
                color = TvliveColors.TextPrimary,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Metadata row
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (content.isLive) {
                    Box(
                        modifier = Modifier
                            .background(TvliveColors.AccentLive, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = TvliveColors.TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = content.releaseYear.toString(),
                    color = TvliveColors.TextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = content.rating,
                    color = TvliveColors.TextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                if (!content.isLive) {
                    Text(
                        text = content.duration,
                        color = TvliveColors.TextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Description
            Text(
                text = content.description,
                color = TvliveColors.TextPrimary.copy(alpha = 0.9f),
                fontSize = 17.sp,
                lineHeight = 26.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onPlayClick,
                colors = ButtonDefaults.buttonColors(containerColor = TvliveColors.Primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .scale(playScale)
                    .then(
                        if (isPlayFocused) Modifier.border(
                            width = 2.dp,
                            color = TvliveColors.FocusBorder,
                            shape = RoundedCornerShape(8.dp)
                        ) else Modifier
                    ),
                interactionSource = playInteractionSource
            ) {
                Text(
                    text = if (content.isLive) "\u25B6 Watch Live" else "\u25B6 Play",
                    color = TvliveColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))
        }
    }
}
