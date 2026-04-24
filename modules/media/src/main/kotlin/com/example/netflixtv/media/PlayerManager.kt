package com.example.netflixtv.media

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Trace
import android.util.Log
import com.example.netflixtv.media.R
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.netflixtv.data.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PlayerManager"
private const val MAX_RETRY_COUNT = 3
private const val RETRY_DELAY_MS = 2000L
private const val CDN_REFERER = "https://tv.cctv.com/"
private const val CDN_ORIGIN = "https://tv.cctv.com"

@UnstableApi
@Singleton
class PlayerManager @Inject constructor(private val context: Context) {

    private var _player: ExoPlayer? = null
    private var _playerListener: Player.Listener? = null
    private val _isPlaying = MutableStateFlow(false)
    private val _currentPosition = MutableStateFlow(0L)
    private val _duration = MutableStateFlow(0L)
    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    private val _error = MutableStateFlow<AppError?>(null)
    private val _mainHandler = Handler(Looper.getMainLooper())
    private var _retryRunnable: Runnable? = null

    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    val duration: StateFlow<Long> = _duration.asStateFlow()
    val playbackState: StateFlow<Int> = _playbackState.asStateFlow()
    val error: StateFlow<AppError?> = _error.asStateFlow()

    private var currentVideoUrl: String? = null
    private var retryCount = 0
    // Track buffering duration to detect stuck playback (expired HLS segments returning 404)
    private var lastBufferingStartMs = 0L

    private val tvLoadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            /* minBufferMs = */ 8_000,
            /* maxBufferMs = */ 30_000,
            /* bufferForPlaybackMs = */ 1_000,
            /* bufferForPlaybackAfterRebufferMs = */ 2_000
        )
        .setPrioritizeTimeOverSizeThresholds(true)
        .build()

    // Force 360P to avoid adaptive streaming bandwidth fragmentation on weak WiFi
    private val fixedTrackSelector = DefaultTrackSelector(context).apply {
        setParameters(buildUponParameters()
            .setMaxVideoSize(640, 360)
            .setForceLowestBitrate(true)
            .build())
    }

    /**
     * Wraps DefaultHttpDataSource to inject Referer header into every HTTP request,
     * including HLS variant playlist and TS segment requests made by ExoPlayer's internal loaders.
     * ExoPlayer's HLS loader creates DataSource instances for segment fetching that don't inherit
     * the factory's default headers — this wrapper ensures every open() call adds the Referer.
     */
    private class CdnAwareHttpDataSource(
        private val upstream: DefaultHttpDataSource
    ) : DataSource by upstream {
        override fun open(dataSpec: DataSpec): Long {
            val host = dataSpec.uri.host ?: "unknown"
            val path = dataSpec.uri.path ?: ""
            val existing = dataSpec.httpRequestHeaders ?: emptyMap()
            val merged = existing.toMutableMap()
            merged["Referer"] = CDN_REFERER
            merged["Origin"] = CDN_ORIGIN
            val newSpec = dataSpec.withRequestHeaders(merged)
            Log.d(TAG, "CdnAwareHttpDataSource.open: $host$path")
            return upstream.open(newSpec)
        }
    }

    fun getPlayer(): ExoPlayer {
        val baseFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(15_000)
            .setDefaultRequestProperties(
                mapOf(
                    "Referer" to CDN_REFERER,
                    "Origin" to CDN_ORIGIN,
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                    "Accept" to "*/*",
                    "Accept-Language" to "zh-CN,zh;q=0.9,en;q=0.8"
                )
            )

        // Wrap so every createDataSource() uses CdnAwareHttpDataSource
        val cdnAwareDataSourceFactory = DataSource.Factory {
            CdnAwareHttpDataSource(baseFactory.createDataSource())
        }

        // Use hardware decoders with forced BT.709 color standard via custom renderer.
        // Hardware decoders (c2.qti.avc.decoder) render frames correctly (renderFps>0)
        // but assume BT.601 when H.264 color_space metadata is missing, producing green tint.
        // Bt709VideoRenderer overrides getMediaCodecConfiguration() to inject
        // KEY_COLOR_STANDARD=COLOR_StandardBT709 into the MediaFormat before codec init.
        Log.d(TAG, "Creating Bt709VideoRenderer")
        val bt709Renderer = Bt709VideoRenderer(context, MediaCodecSelector.DEFAULT)
        val renderersFactory = RenderersFactory { handler, videoListener, audioListener, textOutput, metadataOutput ->
            Log.d(TAG, "RenderersFactory called")
            val defaultFactory = DefaultRenderersFactory(context)
            val defaultRenderers = defaultFactory.createRenderers(handler, videoListener, audioListener, textOutput, metadataOutput)
            Log.d(TAG, "Default renderers: ${defaultRenderers.map { it.javaClass.simpleName }}")

            // Replace the MediaCodecVideoRenderer with our BT.709-corrected one
            val replaced = defaultRenderers.map { renderer ->
                if (renderer is androidx.media3.exoplayer.video.MediaCodecVideoRenderer && renderer !is Bt709VideoRenderer) {
                    Log.d(TAG, "Replacing MediaCodecVideoRenderer with Bt709VideoRenderer")
                    bt709Renderer
                } else {
                    renderer
                }
            }.toTypedArray()
            Log.d(TAG, "Final renderers: ${replaced.map { it.javaClass.simpleName }}")
            replaced
        }

        return _player ?: ExoPlayer.Builder(context, renderersFactory)
            .setLoadControl(tvLoadControl)
            .setTrackSelector(fixedTrackSelector)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context)
                .setDataSourceFactory(cdnAwareDataSourceFactory))
            .build()
            .also { player ->
                _player = player
                if (_playerListener == null) {
                    _playerListener = object : Player.Listener {
                        override fun onIsPlayingChanged(playing: Boolean) {
                            Log.d(TAG, "onIsPlayingChanged: $playing")
                            _isPlaying.value = playing
                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            val stateName = when (playbackState) {
                                Player.STATE_IDLE -> "IDLE"
                                Player.STATE_BUFFERING -> "BUFFERING"
                                Player.STATE_READY -> "READY"
                                Player.STATE_ENDED -> "ENDED"
                                else -> "UNKNOWN"
                            }
                            Log.d(TAG, "onPlaybackStateChanged: $stateName")
                            _playbackState.value = playbackState
                            if (playbackState == Player.STATE_READY) {
                                lastBufferingStartMs = 0L
                                _duration.value = player.duration.coerceAtLeast(0L)
                                _error.value = null
                                retryCount = 0
                            } else if (playbackState == Player.STATE_BUFFERING && lastBufferingStartMs == 0L) {
                                // Record when buffering started
                                lastBufferingStartMs = System.currentTimeMillis()
                            } else if (playbackState == Player.STATE_BUFFERING && lastBufferingStartMs > 0L) {
                                // Check if we've been buffering too long (stuck on expired HLS segments)
                                val bufferingDuration = System.currentTimeMillis() - lastBufferingStartMs
                                if (bufferingDuration > 10_000L) {
                                    Log.w(TAG, "Stuck in buffering for ${bufferingDuration}ms, forcing live-edge seek")
                                    lastBufferingStartMs = 0L
                                    // Force seek to live edge (end of stream) to skip expired segments
                                    player.seekTo(player.duration)
                                }
                            }
                        }

                        override fun onPlayerError(e: PlaybackException) {
                            Log.e(TAG, "Playback error: ${e.errorCodeName}", e)
                            // Cancel any pending retry
                            _retryRunnable?.let { _mainHandler.removeCallbacks(it) }
                            _retryRunnable = null
                            
                            if (retryCount < MAX_RETRY_COUNT && currentVideoUrl != null) {
                                retryCount++
                                Log.w(TAG, "Retry $retryCount/$MAX_RETRY_COUNT")
                                // Use stored handler instead of creating new one
                                _retryRunnable = Runnable {
                                    currentVideoUrl?.let { url ->
                                        player.setMediaItem(MediaItem.fromUri(url))
                                        player.prepare()
                                    }
                                }
                                _mainHandler.postDelayed(_retryRunnable!!, RETRY_DELAY_MS)
                            } else {
                                // Convert PlaybackException to AppError.Player
                                _error.value = AppError.fromPlaybackError(e.errorCode, e.localizedMessage ?: context.getString(R.string.playback_failed))
                            }
                        }
                    }
                }
                _playerListener?.let { listener ->
                    player.addListener(listener)
                }
            }
    }

    fun prepare(videoUrl: String) {
        // Cancel any pending retry before preparing new video
        _retryRunnable?.let { _mainHandler.removeCallbacks(it) }
        _retryRunnable = null
        
        currentVideoUrl = videoUrl
        retryCount = 0
        _error.value = null
        val player = getPlayer()

        Log.d(TAG, "prepare: url=$videoUrl, player=$player")
        player.setMediaItem(MediaItem.fromUri(videoUrl))
        player.prepare()
    }

    fun play() {
        Log.d(TAG, "play() called, player=${_player != null}")
        _player?.play()
    }

    fun pause() {
        Log.d(TAG, "pause() called, player=${_player != null}")
        _player?.pause()
    }

    fun seekTo(position: Long) {
        _player?.seekTo(position)
    }

    fun seekBack(ms: Long = 10_000L) {
        _player?.let {
            it.seekTo((it.currentPosition - ms).coerceAtLeast(0L))
        }
    }

    fun seekForward(ms: Long = 10_000L) {
        _player?.let {
            it.seekTo((it.currentPosition + ms).coerceAtLeast(0L))
        }
    }

    fun updatePosition() {
        _player?.let {
            _currentPosition.value = it.currentPosition.coerceAtLeast(0L)
        }
    }

    fun getCurrentPosition(): Long = _player?.currentPosition?.coerceAtLeast(0L) ?: 0L
    fun getDuration(): Long = _player?.duration?.coerceAtLeast(0L) ?: 0L

    fun release() {
        Log.d(TAG, "release() called, player=${_player != null}")
        _player?.let { player ->
            _playerListener?.let { listener ->
                player.removeListener(listener)
            }
            player.release()
        }
        _player = null
        _playerListener = null
        _retryRunnable?.let { _mainHandler.removeCallbacks(it) }
        _retryRunnable = null
        _mainHandler.removeCallbacksAndMessages(null)
        _isPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
        _playbackState.value = Player.STATE_IDLE
        _error.value = null
    }
}
