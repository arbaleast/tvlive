package com.example.netflixtv.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

private const val TAG = "LiveStreamRepository"

private const val OFFICIAL_API_BASE_URL = "https://vdn.live.cntv.cn/api2/liveHtml5.do"
private const val REQUEST_TIMEOUT_SECONDS = 10L
private const val CACHE_DURATION_MINUTES = 5L
private const val MAX_RETRY_ATTEMPTS = 2
private const val RETRY_DELAY_MS = 1_000L

/**
 * Live stream repository that fetches CCTV live stream URLs from official API.
 *
 * Primary source: Official CCTV API (https://vdn.live.cntv.cn/api2/liveHtml5.do)
 * Fallback: BUPT university CDN URLs (no DRM)
 */
class LiveStreamRepository {

    companion object {
        /** For testing purposes — override by tests */
        var apiBaseUrl: String = OFFICIAL_API_BASE_URL
            internal set
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    /** Cache: channelId -> (timestamp_ms, resolvedUrl) */
    private val cache = mutableMapOf<String, Pair<Long, String>>()

    /**
     * Fetch live stream URL from official CCTV API with retry.
     * API returns JSONP: var html5VideoData = '{...}';getHtml5VideoData(html5VideoData);
     */
    private suspend fun fetchFromOfficialApi(channel: ChannelDefinitions.Channel): Result<String> =
        withContext(Dispatchers.IO) {
            val url = "$apiBaseUrl?channel=${channel.apiParam}"
            Log.d(TAG, "Fetching from official API: $url")

            suspend fun executeRequest(): Result<String> {
                try {
                    val request = Request.Builder()
                        .url(url)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .header("Accept", "*/*")
                        .header("Referer", "https://tv.cctv.com/")
                        .build()

                    val response = httpClient.newCall(request).execute()

                    if (!response.isSuccessful) {
                        Log.e(TAG, "API request failed: ${response.code} ${response.message}")
                        return Result.failure(
                            AppError.Network("API request failed: ${response.code}", response.code)
                        )
                    }

                    val body = response.body?.string()
                    if (body.isNullOrEmpty()) {
                        Log.e(TAG, "Empty response from API")
                        return Result.failure(AppError.Parse("Empty response from API"))
                    }

                    // Parse JSONP: var html5VideoData = '{...}';getHtml5VideoData(html5VideoData);
                    val jsonp = body.trim()
                    val startMarker = "html5VideoData = '"
                    val endMarker = "';"

                    val jsonStart = jsonp.indexOf(startMarker)
                    if (jsonStart == -1) {
                        Log.e(TAG, "Invalid JSONP format")
                        return Result.failure(
                            AppError.Parse("Invalid JSONP response format: missing html5VideoData marker")
                        )
                    }

                    val jsonStartIdx = jsonStart + startMarker.length
                    val jsonEnd = jsonp.indexOf(endMarker, jsonStartIdx)
                    if (jsonEnd == -1) {
                        Log.e(TAG, "Invalid JSONP format: missing end marker")
                        return Result.failure(AppError.Parse("Invalid JSONP format: missing end marker"))
                    }

                    val jsonContent = jsonp.substring(jsonStartIdx, jsonEnd)

                    // Extract HLS URLs: hls1 (udrm, primary) -> hls4 -> hls2 (cdrm, fallback)
                    val hlsPatterns = listOf(
                        """"hls1"\s*:\s*"([^"]+)"""".toRegex(),
                        """"hls4"\s*:\s*"([^"]+)"""".toRegex(),
                        """"hls2"\s*:\s*"([^"]+)"""".toRegex()
                    )

                    val hlsUrl: String? = hlsPatterns.asSequence()
                        .mapNotNull { regex -> regex.find(jsonContent)?.groupValues?.getOrNull(1) }
                        .firstOrNull { it.isNotBlank() && it.startsWith("http") }
                        ?.also { Log.d(TAG, "Found HLS URL: $it") }
                        ?.takeIf { probeUrl(it) }
                        ?: run {
                            Log.w(TAG, "No reachable HLS URL found in API response")
                            null
                        }

                    if (hlsUrl == null) {
                        return Result.failure(AppError.Parse("No reachable HLS URL in API response"))
                    }

                    // Cache valid result
                    cache[channel.id] = Pair(System.currentTimeMillis(), hlsUrl)
                    return Result.success(hlsUrl)

                } catch (e: Exception) {
                    Log.e(TAG, "Error in API request: ${e.message}", e)
                    return Result.failure(AppError.fromThrowable(e))
                }
            }

            // Retry loop
            var lastError: Result<String>? = null
            for (attempt in 1..MAX_RETRY_ATTEMPTS) {
                Log.d(TAG, "API request attempt $attempt/$MAX_RETRY_ATTEMPTS")
                val result = executeRequest()
                if (result.isSuccess) return@withContext result
                lastError = result

                val ex = result.exceptionOrNull()
                // Don't retry on parse/not-found errors
                if (ex is AppError.NotFound || ex is AppError.Parse) {
                    Log.d(TAG, "Not retrying on ${ex::class.simpleName}")
                    break
                }
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    kotlinx.coroutines.delay(RETRY_DELAY_MS)
                }
            }
            lastError ?: Result.failure(AppError.Network("All API attempts failed"))
        }

    private fun getCachedUrl(channelId: String): String? {
        val cached = cache[channelId] ?: return null
        val (timestamp, url) = cached
        val ageMinutes = (System.currentTimeMillis() - timestamp) / (1000 * 60)
        if (ageMinutes < CACHE_DURATION_MINUTES) {
            Log.d(TAG, "Using cached URL for $channelId (age: ${ageMinutes}m)")
            return url
        }
        cache.remove(channelId)
        return null
    }

    suspend fun getLiveStreamUrl(channelId: String): Result<String> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Getting live stream for channel: $channelId")

        val channel = ChannelDefinitions.find(channelId)
        if (channel == null) {
            Log.w(TAG, "Unknown channel ID: $channelId")
            return@withContext Result.failure(AppError.NotFound("Unknown channel: $channelId"))
        }

        // Return cached URL if still fresh
        getCachedUrl(channelId)?.let { return@withContext Result.success(it) }

        // Try official API first
        val apiResult = fetchFromOfficialApi(channel)
        if (apiResult.isSuccess) return@withContext apiResult

        // Fallback to BUPT CDN
        for (url in channel.fallbackUrls) {
            Log.d(TAG, "Probing fallback URL: $url")
            if (probeUrl(url)) {
                Log.d(TAG, "Fallback URL reachable: $url")
                cache[channelId] = Pair(System.currentTimeMillis(), url)
                return@withContext Result.success(url)
            }
        }

        Result.failure(AppError.NotFound("All sources failed for channel: $channelId"))
    }

    private fun probeUrl(url: String): Boolean {
        return try {
            val request = Request.Builder()
                .url(url)
                .head()
                .header("User-Agent", "Mozilla/5.0")
                .header("Referer", "https://tv.cctv.com/")
                .build()
            val response = httpClient.newCall(request).execute()
            response.code in 200..499
        } catch (e: Exception) {
            Log.w(TAG, "Probe failed for $url: ${e.message}")
            false
        }
    }
}
