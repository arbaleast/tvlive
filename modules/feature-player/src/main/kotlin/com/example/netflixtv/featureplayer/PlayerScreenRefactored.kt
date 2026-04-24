package com.example.netflixtv.featureplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.netflixtv.media.PlayerManager
import com.example.netflixtv.uicommon.R as CommonR
import com.example.netflixtv.uicommon.UiConstants
import kotlinx.coroutines.delay

@Composable
fun PlayerScreenRefactored(
    videoUrl: String,
    title: String,
    onBackClick: () -> Unit,
    playerManager: PlayerManager,
    onPipClick: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val isPlaying by playerManager.isPlaying.collectAsState()
    val playerError by playerManager.error.collectAsState()
    var showControls by remember { mutableStateOf(true) }

    val seekForwardMs = remember { UiConstants.Dimens.SEEK_FORWARD_MS }
    val seekBackwardMs = remember { -UiConstants.Dimens.SEEK_BACKWARD_MS }

    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(UiConstants.Animation.CONTROLS_HIDE_DELAY_MS)
            showControls = false
        }
    }

    LaunchedEffect(Unit) {
        playerManager.prepare(videoUrl)
        playerManager.play()
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

    val onPlayPauseClick: () -> Unit = {
        if (isPlaying) playerManager.pause() else playerManager.play()
        showControls = true
    }

    val onSeekForward: () -> Unit = {
        playerManager.seekTo(playerManager.getCurrentPosition() + seekForwardMs)
        showControls = true
    }

    val onSeekBackward: () -> Unit = {
        val newPos = playerManager.getCurrentPosition() + seekBackwardMs
        playerManager.seekTo(if (newPos > 0) newPos else 0)
        showControls = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        PlayerViewComposable(
            playerManager = playerManager,
            modifier = Modifier.fillMaxSize()
        )

        PlayerControlsOverlay(
            title = title,
            isPlaying = isPlaying,
            showControls = showControls,
            onBackClick = onBackClick,
            onPipClick = onPipClick,
            onPlayPauseClick = onPlayPauseClick,
            onSeekForward = onSeekForward,
            onSeekBackward = onSeekBackward,
            modifier = Modifier.fillMaxSize()
        )

        if (playerError != null) {
            PlayerErrorOverlayRefactored(
                errorMessage = playerError?.message ?: context.getString(CommonR.string.error_playback),
                onRetry = {
                    playerError?.let {
                        playerManager.prepare(videoUrl)
                        playerManager.play()
                    }
                },
                onBack = onBackClick
            )
        }
    }
}
