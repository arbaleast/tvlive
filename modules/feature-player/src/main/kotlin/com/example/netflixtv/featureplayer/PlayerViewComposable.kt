package com.example.netflixtv.featureplayer

import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceHolder
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.netflixtv.media.PlayerManager

/**
 * Uses VideoView (SurfaceView + MediaPlayer) instead of ExoPlayer's PlayerView.
 *
 * Background: OPlus TV firmware has a bug where MediaCodecVideoRenderer fails to
 * commit decoded frames to the BLAST BufferQueue of SurfaceView, causing black screen
 * even though audio plays. VideoView uses the native MediaPlayer with a raw SurfaceView
 * (not BLAST), which works reliably on this firmware.
 */
@Composable
internal fun PlayerViewComposable(
    playerManager: PlayerManager,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isPlayerReady by remember { mutableStateOf(false) }
    var currentVideoUrl by remember { mutableStateOf<String?>(null) }

    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )

                setMediaController(null)

                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        android.util.Log.d("PlayerViewComposable",
                            "Surface created: ${holder.surface}")
                    }

                    override fun surfaceChanged(
                        holder: SurfaceHolder,
                        format: Int,
                        width: Int,
                        height: Int
                    ) {
                        android.util.Log.d("PlayerViewComposable",
                            "Surface changed: ${width}x${height} format=$format")
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        android.util.Log.d("PlayerViewComposable", "Surface destroyed")
                    }
                })

                setOnErrorListener { _, what, extra ->
                    android.util.Log.e("PlayerViewComposable",
                        "VideoView error: what=$what extra=$extra")
                    true
                }

                setOnCompletionListener {
                    android.util.Log.d("PlayerViewComposable", "VideoView completion")
                }

                setOnInfoListener { _, what, extra ->
                    android.util.Log.d("PlayerViewComposable", "VideoView info: what=$what extra=$extra")
                    when (what) {
                        MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                            android.util.Log.d("PlayerViewComposable", "Buffering start")
                        }
                        MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                            android.util.Log.d("PlayerViewComposable", "Buffering end")
                            isPlayerReady = true
                        }
                        MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                            android.util.Log.d("PlayerViewComposable", "Video rendering start")
                        }
                    }
                    true
                }
            }
        },
        modifier = modifier,
        update = { view ->
            val newUrl = currentVideoUrl
            if (newUrl != null && newUrl != view.tag) {
                android.util.Log.d("PlayerViewComposable", "Setting video URI: $newUrl")
                view.tag = newUrl
                view.setVideoURI(Uri.parse(newUrl))
                view.start()
            }
        }
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> { }
                Lifecycle.Event.ON_RESUME -> { }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
