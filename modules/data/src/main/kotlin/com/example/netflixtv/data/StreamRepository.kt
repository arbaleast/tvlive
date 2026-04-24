package com.example.netflixtv.data

import android.util.Log

private const val TAG = "StreamRepo"

class StreamRepository {

    private val liveStreamRepo = LiveStreamRepository()
    private val cache = mutableMapOf<String, String>()

    suspend fun fetchLiveStreamUrl(channelId: String): String? {
        cache[channelId]?.let { return it }
        val result = liveStreamRepo.getLiveStreamUrl(channelId)
        result.getOrNull()?.let { url ->
            cache[channelId] = url
            return url
        }
        Log.w(TAG, "Failed to fetch live stream for $channelId")
        return null
    }
}
