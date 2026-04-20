package com.example.netflixtv.featureplayer

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.netflixtv.data.AppConstants
import com.example.netflixtv.media.PlayerManager
import com.example.netflixtv.uicommon.DpadFocusable
import com.example.netflixtv.uicommon.TvliveColors
import androidx.compose.ui.graphics.painter.ColorPainter
import kotlinx.coroutines.delay

private val ControlsHideDelayMs = 4000L

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    videoUrl: String,
    title: String,
    posterUrl: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val playerManager = remember { PlayerManager(context) }
    val isPlaying by playerManager.isPlaying.collectAsState()
    var showControls by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    // Auto-hide controls after delay
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(ControlsHideDelayMs)
            showControls = false
        }
    }

    // Update position periodically
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = playerManager.getCurrentPosition()
            duration = playerManager.getDuration()
            delay(1000)
        }
    }

    LaunchedEffect(videoUrl) {
        playerManager.prepare(videoUrl)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> playerManager.pause()
                Lifecycle.Event.ON_RESUME -> playerManager.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            playerManager.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        // Player view
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = playerManager.getPlayer()
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                view.player = playerManager.getPlayer()
            }
        )

        // Poster overlay (shown when paused)
        AsyncImage(
            model = posterUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = if (isPlaying) 0f else 0.4f,
            placeholder = ColorPainter(Color.Black),
            error = ColorPainter(Color.Black)
        )

        // Top gradient scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Top bar
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DpadFocusable(
                    cornerRadius = 8.dp,
                    focusBorderColor = TvliveColors.FocusBorder
                ) {
                    GlassButton(onClick = onBackClick) {
                        Text(
                            text = "\u2190 Back",
                            color = TvliveColors.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = title,
                    color = TvliveColors.TextPrimary.copy(alpha = 0.9f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }

        // Bottom controls overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                animationSpec = tween(300),
                initialOffsetY = { it }
            ),
            exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                animationSpec = tween(300),
                targetOffsetY = { it }
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(horizontal = 48.dp, vertical = 24.dp)
            ) {
                // Progress bar
                if (duration > 0) {
                    ProgressBar(
                        currentPosition = currentPosition,
                        duration = duration,
                        onSeek = { playerManager.seekTo(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Control buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SeekButton(
                        onClick = { playerManager.seekBack(AppConstants.SEEK_BACK_MS) },
                        label = "\u23EA",
                        description = "-10s"
                    )

                    Spacer(modifier = Modifier.width(32.dp))

                    PlayPauseButton(
                        isPlaying = isPlaying,
                        onClick = {
                            if (isPlaying) playerManager.pause() else playerManager.play()
                            showControls = true
                        }
                    )

                    Spacer(modifier = Modifier.width(32.dp))

                    SeekButton(
                        onClick = { playerManager.seekForward(AppConstants.SEEK_FORWARD_MS) },
                        label = "\u23E9",
                        description = "+10s"
                    )
                }

                // Time display
                if (duration > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = formatTime(currentPosition),
                            color = TvliveColors.TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = " / ",
                            color = TvliveColors.TextTertiary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = formatTime(duration),
                            color = TvliveColors.TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit
) {
    val progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val progressWidthScale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "progressWidth"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .focusable(interactionSource = interactionSource)
            .clickable { onSeek((duration * 0.5f).toLong()) }
            .padding(vertical = 8.dp)
    ) {
        // Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(TvliveColors.TextTertiary.copy(alpha = 0.4f))
        )

        // Progress fill
        Box(
            modifier = Modifier
                .fillMaxWidth(progress * progressWidthScale)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(TvliveColors.Primary)
        )

        // Thumb (visible on focus)
        AnimatedVisibility(
            visible = isFocused,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .offset(x = (progress * 100).dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(TvliveColors.Primary)
            )
        }
    }
}

@Composable
private fun SeekButton(
    onClick: () -> Unit,
    label: String,
    description: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (isFocused) Modifier.border(
                    2.dp,
                    TvliveColors.FocusBorder,
                    RoundedCornerShape(8.dp)
                ) else Modifier
            )
            .clickable(onClick = onClick)
            .focusable(interactionSource = interactionSource)
            .padding(8.dp)
    ) {
        androidx.compose.material3.Button(
            onClick = onClick,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = TvliveColors.BackgroundElevated.copy(alpha = 0.8f)
            ),
            shape = CircleShape,
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier.size(52.dp)
        ) {
            Text(
                text = label,
                color = TvliveColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = description,
            color = TvliveColors.TextTertiary,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (isFocused) Modifier.border(
                    2.5.dp,
                    Color.White,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .clickable(onClick = onClick)
            .focusable(interactionSource = interactionSource)
            .padding(4.dp)
    ) {
        androidx.compose.material3.Button(
            onClick = onClick,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = TvliveColors.Primary
            ),
            shape = CircleShape,
            contentPadding = PaddingValues(18.dp),
            modifier = Modifier.size(72.dp)
        ) {
            Text(
                text = if (isPlaying) "\u23F8" else "\u25B6",
                color = TvliveColors.TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = if (isPlaying) "Pause" else "Play",
            color = TvliveColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun GlassButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = TvliveColors.BackgroundElevated.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        content()
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}
