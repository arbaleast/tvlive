package com.example.netflixtv.media

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages ExoPlayer lifecycle — single instance per video URL.
 * Replaceable for testing (mock or fake).
 */
class PlayerManager(private val context: Context) {

    private var _player: ExoPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    private val _currentPosition = MutableStateFlow(0L)
    private val _duration = MutableStateFlow(0L)

    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    val duration: StateFlow<Long> = _duration.asStateFlow()

    fun getPlayer(): ExoPlayer {
        return _player ?: ExoPlayer.Builder(context).build().also {
            _player = it
            it.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    _isPlaying.value = playing
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _duration.value = it.duration.coerceAtLeast(0L)
                    }
                }
            })
        }
    }

    fun prepare(videoUrl: String) {
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
