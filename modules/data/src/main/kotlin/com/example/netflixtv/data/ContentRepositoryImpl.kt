package com.example.netflixtv.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException

private const val TAG = "ContentRepositoryImpl"

class ContentRepositoryImpl(
    private val context: Context,
    private val _catalogId: String
) : ContentRepository {

    override val catalogId: String get() = _catalogId

    @Volatile
    private var cachedCategories: List<Category>? = null
    private val _categoriesFlow = MutableStateFlow<List<Category>>(emptyList())
    private val parseMutex = Mutex()

    private val cache = CatalogCacheManager.getInstance()

    override suspend fun loadCategories(): List<Category> {
        cachedCategories?.let { return it }

        return parseMutex.withLock {
            cachedCategories?.let { return it }

            withContext(Dispatchers.IO) {
                val entry = cache.get(_catalogId)
                if (entry != null) {
                    cachedCategories = entry.categories
                    _categoriesFlow.value = entry.categories
                    return@withContext entry.categories
                }

                try {
                    val json = loadJsonFromAssets()
                    val categories = parseCategories(json)

                    cache.put(
                        _catalogId,
                        CatalogEntry(
                            catalogId = _catalogId,
                            categories = categories,
                            loadedAt = System.currentTimeMillis(),
                            ttlMs = AppConstants.CACHE_TTL_MS
                        )
                    )

                    cachedCategories = categories
                    _categoriesFlow.value = categories
                    categories
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load catalog", e)
                    val fallback = buildFallbackCategories()
                    cachedCategories = fallback
                    _categoriesFlow.value = fallback
                    fallback
                }
            }
        }
    }

    override suspend fun getContentById(contentId: String): Content? {
        return cachedCategories?.asSequence()
            ?.flatMap { it.items.asSequence() }
            ?.find { it.id == contentId }
            ?: loadCategories().find { cat -> cat.items.any { it.id == contentId } }
                ?.items?.find { it.id == contentId }
    }

    override fun getContentByIdSync(contentId: String): Content? {
        return cachedCategories?.asSequence()
            ?.flatMap { it.items.asSequence() }
            ?.find { it.id == contentId }
    }

    override fun getAllContent(): List<Content> {
        return cachedCategories?.flatMap { it.items } ?: emptyList()
    }

    override fun getItemsByCategory(category: String): List<Content> {
        return cachedCategories?.firstOrNull { it.name.equals(category, ignoreCase = true) }?.items ?: emptyList()
    }

    override fun getAvailableCatalogs(): List<String> {
        return listOf("default")
    }

    override fun observeCategories(): Flow<List<Category>> {
        return _categoriesFlow.asStateFlow()
    }

    @Throws(AppError.Parse::class)
    private fun loadJsonFromAssets(): JSONArray {
        return try {
            val json = context.assets.open(AppConstants.CONTENT_DATA_FILE)
                .bufferedReader()
                .use { it.readText() }
            // Try object format with "categories" wrapper first
            try {
                val obj = org.json.JSONObject(json)
                if (obj.has("categories")) {
                    return obj.getJSONArray("categories")
                }
            } catch (e: Exception) {
                // Not object format, try array
            }
            // Fall back to direct array format
            JSONArray(json)
        } catch (e: Exception) {
            throw AppError.Parse("Failed to load ${AppConstants.CONTENT_DATA_FILE}: ${e.message}")
        }
    }

    @Throws(AppError.Parse::class)
    private fun parseCategories(json: JSONArray): List<Category> {
        val categories = mutableListOf<Category>()
        
        // Check if this is a wrapped format: [{"name": "...", "items": [...]}]
        if (json.length() > 0) {
            val firstItem = json.optJSONObject(0)
            if (firstItem != null && firstItem.has("name") && firstItem.has("items")) {
                // New wrapped format
                for (i in 0 until json.length()) {
                    try {
                        val catObj = json.getJSONObject(i)
                        val catName = catObj.getString("name")
                        val itemsArray = catObj.getJSONArray("items")
                        val items = mutableListOf<Content>()
                        
                        for (j in 0 until itemsArray.length()) {
                            try {
                                val itemObj = itemsArray.getJSONObject(j)
                                val content = Content(
                                    id = itemObj.optString("id", ""),
                                    title = itemObj.optString("title", ""),
                                    description = itemObj.optString("description", ""),
                                    thumbnailUrl = itemObj.optString("thumbnailUrl", ""),
                                    backdropUrl = itemObj.optString("backdropUrl", ""),
                                    videoUrl = itemObj.optString("videoUrl", ""),
                                    category = catName,
                                    releaseYear = itemObj.optInt("releaseYear", 2024),
                                    rating = itemObj.optString("rating", "PG"),
                                    duration = itemObj.optString("duration", ""),
                                    isLive = itemObj.optBoolean("isLive")
                                )
                                if (content.id.isNotBlank() && content.title.isNotBlank()) {
                                    items.add(content)
                                }
                            } catch (e: JSONException) {
                                Log.w(TAG, "Failed to parse item at index $j", e)
                            }
                        }
                        categories.add(Category(catName, items))
                    } catch (e: JSONException) {
                        Log.w(TAG, "Failed to parse category at index $i", e)
                    }
                }
                return categories
            }
        }
        
        // Legacy flat array format: direct list of content items
        val itemsByCategory = mutableMapOf<String, MutableList<Content>>()
        for (i in 0 until json.length()) {
            try {
                val itemObj = json.getJSONObject(i)
                val id = itemObj.optString("id", "")
                val title = itemObj.optString("title", "")

                if (id.isBlank() || title.isBlank()) {
                    Log.w(TAG, "Skipping item at index $i: missing id or title")
                    continue
                }

                val content = Content(
                    id = id,
                    title = title,
                    description = itemObj.optString("description", ""),
                    thumbnailUrl = itemObj.optString("thumbnailUrl", ""),
                    backdropUrl = itemObj.optString("backdropUrl", ""),
                    videoUrl = itemObj.optString("videoUrl", ""),
                    category = itemObj.optString("category", "Unknown"),
                    releaseYear = itemObj.optInt("releaseYear", 2024),
                    rating = itemObj.optString("rating", "PG"),
                    duration = itemObj.optString("duration", ""),
                    isLive = itemObj.optBoolean("isLive")
                )

                itemsByCategory.getOrPut(content.category) { mutableListOf() }.add(content)
            } catch (e: JSONException) {
                Log.w(TAG, "Failed to parse item at index $i", e)
            }
        }
        return itemsByCategory.map { (name, items) -> Category(name, items) }
    }

    private fun buildFallbackCategories(): List<Category> {
        return listOf(
            Category("Trending", emptyList()),
            Category("Live TV", emptyList())
        )
    }
}
