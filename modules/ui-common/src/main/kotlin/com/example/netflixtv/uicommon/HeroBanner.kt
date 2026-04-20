package com.example.netflixtv.uicommon

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

private const val KenBurnsScaleEnd = 1.02f
private const val KenBurnsDurationMs = 20000

@Composable
fun HeroBanner(
    imageUrl: String,
    title: String,
    description: String,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playInteractionSource = remember { MutableInteractionSource() }
    val isPlayFocused by playInteractionSource.collectIsFocusedAsState()

    val moreInfoInteractionSource = remember { MutableInteractionSource() }
    val isMoreInfoFocused by moreInfoInteractionSource.collectIsFocusedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "kenBurns")
    // Single animation for both scale and offset (combined for performance)
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = KenBurnsScaleEnd,
        animationSpec = infiniteRepeatable(
            animation = tween(KenBurnsDurationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "kbScale"
    )
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(KenBurnsDurationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "kbOffsetX"
    )

    val contentAlpha = 1f  // No animation

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
    ) {
        // Ken Burns background image
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .graphicsLayer { translationX = offsetX },
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(TvliveColors.BackgroundPrimary),
            error = ColorPainter(TvliveColors.BackgroundPrimary)
        )

        // Full-screen gradient overlay (top to bottom)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Left-side scrim for text readability
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(700.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.9f),
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Content column
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 56.dp, bottom = 56.dp)
                .width(560.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Title with gradient shadow
            Text(
                text = title,
                color = TvliveColors.TextPrimary,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = description,
                color = TvliveColors.TextSecondary,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                // Play button with gradient
                HeroButton(
                    isFocused = isPlayFocused,
                    interactionSource = playInteractionSource,
                    onClick = onCtaClick,
                    useGradient = true
                ) {
                    Text(
                        text = "▶ Play",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvliveColors.TextPrimary
                    )
                }

                // More Info button
                HeroButton(
                    isFocused = isMoreInfoFocused,
                    interactionSource = moreInfoInteractionSource,
                    onClick = {},
                    useGradient = false
                ) {
                    Text(
                        text = "ℹ More Info",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TvliveColors.TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroButton(
    isFocused: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    useGradient: Boolean,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh),
        label = "buttonScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = 2.5.dp,
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp)
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(8.dp))
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (useGradient) 
                    TvliveColors.Primary else Color.White.copy(alpha = 0.25f)
            ),
            interactionSource = interactionSource
        ) {
            content()
        }
    }
}
