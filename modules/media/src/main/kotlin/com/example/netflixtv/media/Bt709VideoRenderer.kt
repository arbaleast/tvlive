package com.example.netflixtv.media

import android.content.Context
import android.media.MediaFormat
import android.os.Handler
import android.util.Log
import androidx.media3.common.Format
import androidx.media3.exoplayer.mediacodec.MediaCodecAdapter
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.mediacodec.MediaCodecInfo as Media3CodecInfo
import androidx.media3.exoplayer.video.MediaCodecVideoRenderer
import androidx.media3.exoplayer.video.VideoRendererEventListener

private const val TAG = "Bt709VideoRenderer"

/**
 * Custom MediaCodecVideoRenderer that forces BT.709 color standard on the decoder output.
 *
 * H.264 streams without VUI color_space metadata default to BT.601 in some hardware decoders,
 * causing green-tinted output. This renderer injects KEY_COLOR_STANDARD=COLOR_StandardBT709
 * into the MediaFormat before codec configuration, telling the decoder to use BT.709 YUV→RGB
 * conversion, which matches the actual HDTV content.
 */
class Bt709VideoRenderer : MediaCodecVideoRenderer {

    // Constructors matching MediaCodecVideoRenderer (for DefaultRenderersFactory compatibility)

    constructor(context: Context, mediaCodecSelector: MediaCodecSelector) : super(context, mediaCodecSelector)

    constructor(
        context: Context,
        mediaCodecSelector: MediaCodecSelector,
        allowedJoiningTimeMs: Long
    ) : super(context, mediaCodecSelector, allowedJoiningTimeMs)

    constructor(
        context: Context,
        mediaCodecSelector: MediaCodecSelector,
        allowedJoiningTimeMs: Long,
        eventHandler: Handler?,
        eventListener: VideoRendererEventListener?,
        maxDroppedFramesToNotify: Int
    ) : super(context, mediaCodecSelector, allowedJoiningTimeMs, eventHandler, eventListener, maxDroppedFramesToNotify)

    constructor(
        context: Context,
        mediaCodecSelector: MediaCodecSelector,
        allowedJoiningTimeMs: Long,
        enableDecoderFallback: Boolean,
        eventHandler: Handler?,
        eventListener: VideoRendererEventListener?,
        maxDroppedFramesToNotify: Int
    ) : super(context, mediaCodecSelector, allowedJoiningTimeMs, enableDecoderFallback, eventHandler, eventListener, maxDroppedFramesToNotify)

    constructor(
        context: Context,
        codecAdapterFactory: MediaCodecAdapter.Factory,
        mediaCodecSelector: MediaCodecSelector,
        allowedJoiningTimeMs: Long,
        enableDecoderFallback: Boolean,
        eventHandler: Handler?,
        eventListener: VideoRendererEventListener?,
        maxDroppedFramesToNotify: Int
    ) : super(context, codecAdapterFactory, mediaCodecSelector, allowedJoiningTimeMs, enableDecoderFallback, eventHandler, eventListener, maxDroppedFramesToNotify)

    override fun getMediaCodecConfiguration(
        codecInfo: Media3CodecInfo,
        format: Format,
        crypto: android.media.MediaCrypto?,
        operatingRate: Float
    ): MediaCodecAdapter.Configuration {
        val config = super.getMediaCodecConfiguration(codecInfo, format, crypto, operatingRate)

        Log.d(TAG, "BT709 before: ${config.mediaFormat}")
        // Force BT.709 color standard on the decoder output format
        // This tells the hardware decoder to use BT.709 YUV→RGB conversion matrix
        config.mediaFormat.apply {
            Log.d(TAG, "Inject BT709: codec=${codecInfo.name} " +
                    "size=${format.width}x${format.height}")

            // COLOR_StandardBT709=1, COLOR_RangeLimited=2, COLOR_TransferBT709=1
            // These are stable Android API constants (MediaCodecInfo.CodecCapabilities)
            setInteger(MediaFormat.KEY_COLOR_STANDARD, 1)   // COLOR_StandardBT709
            setInteger(MediaFormat.KEY_COLOR_RANGE, 2)       // COLOR_RangeLimited
            setInteger(MediaFormat.KEY_COLOR_TRANSFER, 1)   // COLOR_TransferBT709
        }
        Log.d(TAG, "BT709 after: ${config.mediaFormat}")

        return config
    }
}
