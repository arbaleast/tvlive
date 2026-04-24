package com.example.netflixtv.featureplayer

import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import com.example.netflixtv.media.PlayerManager

/**
 * ExoPlayer PlayerView wrapper for Jetpack Compose.
 * Attaches PlayerManager's ExoPlayer instance to PlayerView.
 */
@Composable
internal fun PlayerViewComposable(
    playerManager: PlayerManager,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> playerManager.getPlayer().pause()
                Lifecycle.Event.ON_RESUME -> playerManager.getPlayer().play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                useController = false
                player = playerManager.getPlayer()
            }
        },
        modifier = modifier
    )
}
