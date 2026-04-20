package com.example.netflixtv.uicommon

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.painter.ColorPainter
import com.example.netflixtv.data.Content

private val FocusScale = 1.1f
private val FocusElevation = 16.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NetflixCard(
    content: Content,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    LaunchedEffect(isFocused) {
        if (isFocused) bringIntoViewRequester.bringIntoView()
    }

    // Use @Stable for animation state to reduce recompositions
    val scale by animateFloatAsState(
        targetValue = if (isFocused) FocusScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(800),
        label = "glowAlpha"
    )

    val elevation by animateDpAsState(
        targetValue = if (isFocused) FocusElevation else 4.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "elevation"
    )

    Card(
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .width(160.dp)
            .height(240.dp)
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(12.dp),
                ambientColor = TvliveColors.FocusGlow.copy(alpha = glowAlpha * 0.3f),
                spotColor = TvliveColors.FocusGlow.copy(alpha = glowAlpha * 0.5f)
            )
            .then(
                if (isFocused) {
                    Modifier
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    TvliveColors.FocusBorder,
                                    TvliveColors.PrimaryVariant,
                                    TvliveColors.FocusBorder
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                } else Modifier
            )
            .focusable(interactionSource = interactionSource),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = TvliveColors.BackgroundSecondary
        ),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = content.thumbnailUrl,
                contentDescription = content.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(TvliveColors.BackgroundSecondary),
                error = ColorPainter(TvliveColors.BackgroundSecondary)
            )

            // Gradient overlay from bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                if (isFocused) TvliveColors.Primary.copy(alpha = 0.9f) 
                                       else TvliveColors.CardOverlay
                            )
                        )
                    )
            )

            // Title at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = content.title,
                    color = TvliveColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // LIVE badge
            if (content.isLive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .background(TvliveColors.AccentLive, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = TvliveColors.TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quality badge (example)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "HD",
                    color = TvliveColors.TextPrimary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
