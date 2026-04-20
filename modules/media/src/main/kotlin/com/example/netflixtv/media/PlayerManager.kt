package com.example.netflixtv.media

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "PlayerManager"
private const val MAX_RETRY_COUNT = 3
private const val RETRY_DELAY_MS = 2000L

class PlayerManager(private val context: Context) {

    private var _player: ExoPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    private val _currentPosition = MutableStateFlow(0L)
    private val _duration = MutableStateFlow(0L)
    private val _error = MutableStateFlow<String?>(null)

    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    val duration: StateFlow<Long> = _duration.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentVideoUrl: String? = null
    private var retryCount = 0

    private val tvLoadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            /* minBufferMs = */ 30_000,
            /* maxBufferMs = */ 120_000,
            /* bufferForPlaybackMs = */ 5_000,
            /* bufferForPlaybackAfterRebufferMs = */ 10_000
        )
        .setPrioritizeTimeOverSizeThresholds(true)
        .build()

    fun getPlayer(): ExoPlayer {
        return _player ?: ExoPlayer.Builder(context)
            .setLoadControl(tvLoadControl)
            .build()
            .also { player ->
                _player = player
                player.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) {
                        _isPlaying.value = playing
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            _duration.value = player.duration.coerceAtLeast(0L)
                            _error.value = null
                            retryCount = 0
                        }
                    }

                    override fun onPlayerError(e: PlaybackException) {
                        Log.e(TAG, "Playback error: ${e.errorCodeName}", e)
                        if (retryCount < MAX_RETRY_COUNT && currentVideoUrl != null) {
                            retryCount++
                            Log.w(TAG, "Retry $retryCount/$MAX_RETRY_COUNT")
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                currentVideoUrl?.let { url ->
                                    player.setMediaItem(MediaItem.fromUri(url))
                                    player.prepare()
                                }
                            }, RETRY_DELAY_MS)
                        } else {
                            _error.value = e.localizedMessage ?: "Playback failed"
                        }
                    }
                })
            }
    }

    fun prepare(videoUrl: String) {
        currentVideoUrl = videoUrl
        retryCount = 0
        _error.value = null
        val player = getPlayer()
        player.setMediaItem(MediaItem.fromUri(videoUrl))
        player.prepare()
    }

    fun play() {
        _player?.play()
    }

    fun pause() {
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
        _player?.release()
        _player = null
        _isPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
    }
}
