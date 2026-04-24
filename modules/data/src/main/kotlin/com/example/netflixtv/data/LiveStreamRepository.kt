package com.example.netflixtv.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

private const val TAG = "LiveStreamRepository"

/**
 * Live stream repository that fetches CCTV live stream URLs from official API.
 * 
 * Primary source: Official CCTV API (https://vdn.live.cntv.cn/api2/liveHtml5.do)
 * Fallback: Hardcoded non-DRM CDN URLs as backup
 */
class LiveStreamRepository {
    
    companion object {
        private const val OFFICIAL_API_BASE_URL = "https://vdn.live.cntv.cn/api2/liveHtml5.do"
        private const val REQUEST_TIMEOUT_SECONDS = 10L
        private const val CACHE_DURATION_MINUTES = 5L
        private const val MAX_RETRY_ATTEMPTS = 2
        private const val RETRY_DELAY_MS = 1000L
        
        // Channel ID to API parameter mapping
        private val channelApiParamMap = mapOf(
            "cctv1" to "pw://cctv_p2p_hdcctv1",
            "cctv2" to "pw://cctv_p2p_hdcctv2"
        )
        
        // For testing purposes - can be overridden by tests
        var apiBaseUrl: String = OFFICIAL_API_BASE_URL
            internal set
    }
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()
    
    // Cache for API responses (channelId -> (timestamp, url))
    private val apiResponseCache = mutableMapOf<String, Pair<Long, String>>()
    
    /**
     * Fetch live stream URL from official CCTV API with retry mechanism.
     * API returns JSONP format: setLiveHtml5({...})
     */
    private suspend fun fetchFromOfficialApi(channelId: String): Result<String> = withContext(Dispatchers.IO) {
        val apiParam = channelApiParamMap[channelId]
        if (apiParam == null) {
            Log.w(TAG, "No API parameter mapping for channel: $channelId")
            return@withContext Result.failure(
                AppError.NotFound("Channel not supported by official API: $channelId")
            )
        }
        
        val url = "${apiBaseUrl}?channel=$apiParam"
        Log.d(TAG, "Fetching from official API: $url")
        
        // Single request execution with error handling
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
                
                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    Log.e(TAG, "Empty response from API")
                    return Result.failure(
                        AppError.Parse("Empty response from API")
                    )
                }
                
                // Parse JSONP response: var html5VideoData = '{...}';getHtml5VideoData(html5VideoData);
                val jsonpResponse = responseBody.trim()
                val startMarker = "html5VideoData = '"
                val endMarker = "';"
                
                val startIndex = jsonpResponse.indexOf(startMarker)
                if (startIndex == -1) {
                    Log.e(TAG, "Invalid JSONP format: missing html5VideoData marker")
                    return Result.failure(
                        AppError.Parse("Invalid JSONP response format: missing html5VideoData marker")
                    )
                }
                
                val jsonStart = startIndex + startMarker.length
                val endIndex = jsonpResponse.indexOf(endMarker, jsonStart)
                if (endIndex == -1) {
                    Log.e(TAG, "Invalid JSONP format: missing end marker")
                    return Result.failure(
                        AppError.Parse("Invalid JSONP response format: missing end marker")
                    )
                }
                
                // Extract JSON content
                val jsonContent = jsonpResponse.substring(jsonStart, endIndex)
                Log.d(TAG, "Extracted JSON content: $jsonContent")
                
                // Parse JSON to extract hls_url (hls1, hls2, hls4 — in order of preference)
                // hls1 = udrm stream (primary), hls2 = cdrm stream (fallback), hls4 = same as hls1
                val hlsPatterns = listOf(
                    """\"hls1\"\s*:\s*\"([^\"]+)\"""".toRegex(),
                    """\"hls4\"\s*:\s*\"([^\"]+)\"""".toRegex(),
                    """\"hls2\"\s*:\s*\"([^\"]+)\"""".toRegex()
                )

                // Try each HLS URL in order, probe to verify reachability
                val hlsUrl: String? = hlsPatterns.asSequence()
                    .mapNotNull { regex ->
                        regex.find(jsonContent)?.groupValues?.getOrNull(1)
                            ?.takeIf { it.isNotBlank() && it.startsWith("http") }
                            ?.also { Log.d(TAG, "Found HLS URL: $it") }
                    }
                    .firstOrNull { url ->
                        Log.d(TAG, "Probing HLS URL: $url")
                        val reachable = probeUrl(url)
                        if (reachable) Log.d(TAG, "HLS URL reachable: $url")
                        else Log.w(TAG, "HLS URL unreachable: $url")
                        reachable
                    }
                    ?: run {
                        Log.e(TAG, "No valid or reachable HLS URL in API response")
                        null
                    }

                if (hlsUrl == null) {
                    return Result.failure(AppError.Parse("No reachable HLS URL in API response"))
                }

                Log.d(TAG, "Successfully verified HLS URL: $hlsUrl")

                // Cache the result
                val cacheTime = System.currentTimeMillis()
                apiResponseCache[channelId] = Pair(cacheTime, hlsUrl)

                return Result.success(hlsUrl)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error in API request: ${e.message}", e)
                return Result.failure(AppError.fromThrowable(e))
            }
        }
        
        // Retry logic
        var lastError: Result<String>? = null
        
        for (attempt in 1..MAX_RETRY_ATTEMPTS) {
            Log.d(TAG, "API request attempt $attempt/$MAX_RETRY_ATTEMPTS")
            
            val result = executeRequest()
            
            if (result.isSuccess) {
                return@withContext result
            }
            
            lastError = result
            
            // Don't retry on certain errors
            val exception = result.exceptionOrNull()
            if (exception is AppError.NotFound || exception is AppError.Parse) {
                Log.d(TAG, "Not retrying on ${exception::class.simpleName} error")
                break
            }
            
            // Wait before retry (except on last attempt)
            if (attempt < MAX_RETRY_ATTEMPTS) {
                Log.d(TAG, "Waiting ${RETRY_DELAY_MS}ms before retry...")
                kotlinx.coroutines.delay(RETRY_DELAY_MS)
            }
        }
        
        Log.w(TAG, "All $MAX_RETRY_ATTEMPTS attempts failed")
        lastError ?: Result.failure(AppError.Network("All API request attempts failed"))
    }
    
    /**
     * Get cached URL if still valid (within cache duration).
     */
    private fun getCachedUrl(channelId: String): String? {
        val cached = apiResponseCache[channelId] ?: return null
        val (timestamp, url) = cached
        
        val cacheAgeMinutes = (System.currentTimeMillis() - timestamp) / (1000 * 60)
        if (cacheAgeMinutes < CACHE_DURATION_MINUTES) {
            Log.d(TAG, "Using cached URL for $channelId (age: ${cacheAgeMinutes}m)")
            return url
        }
        
        // Cache expired
        apiResponseCache.remove(channelId)
        return null
    }

    // Fallback HLS URLs for cctv1 and cctv2
    private val fallbackUrls = mapOf(
        "cctv1" to listOf(
            "https://ldncctvcpudbd.a.bdydns.com/ldncctvcpud/udrmldcctv1_1/index.m3u8?b=200-2100",
            "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"
        ),
        "cctv2" to listOf(
            "https://ldncctvcpudbd.a.bdydns.com/ldncctvcpud/udrmldcctv2_1/index.m3u8?b=200-2100",
            "http://ivi.bupt.edu.cn/hls/cctv2hd.m3u8"
        )
    )

    suspend fun getLiveStreamUrl(channelId: String): Result<String> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Getting live stream for channel: $channelId")
        try {
            getCachedUrl(channelId)?.let { cachedUrl ->
                return@withContext Result.success(cachedUrl)
            }

            val apiResult = fetchFromOfficialApi(channelId)
            if (apiResult.isSuccess) {
                return@withContext apiResult
            }

            fallbackUrls[channelId]?.forEach { url ->
                Log.d(TAG, "Probing fallback URL: $url")
                val probeResult = probeUrl(url)
                if (probeResult) {
                    Log.d(TAG, "Fallback URL reachable: $url")
                    val cacheTime = System.currentTimeMillis()
                    apiResponseCache[channelId] = Pair(cacheTime, url)
                    return@withContext Result.success(url)
                }
            }

            return@withContext Result.failure(
                AppError.NotFound("Unknown channel: $channelId")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get live stream for $channelId: ${e.message}", e)
            Result.failure(AppError.fromThrowable(e))
        }
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
