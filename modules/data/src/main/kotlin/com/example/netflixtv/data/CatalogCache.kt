package com.example.netflixtv.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

data class CatalogEntry(
    val catalogId: String,
    val categories: List<Category>,
    val loadedAt: Long,
    val ttlMs: Long,
    val accessCount: Int = 0,
    val lastAccessAt: Long = loadedAt
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() - loadedAt > ttlMs
}

data class CacheMetrics(
    val hits: Long = 0,
    val misses: Long = 0,
    val evictions: Long = 0,
    val size: Int = 0,
    val lastRefresh: Long = 0
)

interface CatalogCache {
    suspend fun get(catalogId: String): CatalogEntry?
    suspend fun put(catalogId: String, entry: CatalogEntry)
    suspend fun invalidate(catalogId: String)
    suspend fun invalidateAll()
    fun observe(catalogId: String): Flow<CatalogEntry?>
    fun getMetrics(): CacheMetrics
}

class CatalogCacheImpl(
    private val maxEntries: Int = 1000,
    private val defaultTtlMs: Long = 30 * 60 * 1000L
) : CatalogCache {

    private val map = ConcurrentHashMap<String, CatalogEntry>()
    private val _metrics = CacheMetrics()
    private val _flows = mutableMapOf<String, MutableStateFlow<CatalogEntry?>>()

    override suspend fun get(catalogId: String): CatalogEntry? {
        val entry = map[catalogId] ?: return null
        if (entry.isExpired) {
            map.remove(catalogId)
            notifyFlow(catalogId, null)
            return null
        }
        val updated = entry.copy(
            accessCount = entry.accessCount + 1,
            lastAccessAt = System.currentTimeMillis()
        )
        map[catalogId] = updated
        notifyFlow(catalogId, updated)
        return updated
    }

    override suspend fun put(catalogId: String, entry: CatalogEntry) {
        if (map.size >= maxEntries && !map.containsKey(catalogId)) {
            val lruKey = map.keys.firstOrNull()
            lruKey?.let { map.remove(it) }
        }
        map[catalogId] = entry
        notifyFlow(catalogId, entry)
    }

    override suspend fun invalidate(catalogId: String) {
        map.remove(catalogId)
        notifyFlow(catalogId, null)
    }

    override suspend fun invalidateAll() {
        map.clear()
        _flows.values.forEach { entry -> entry.value = null }
    }

    override fun observe(catalogId: String): Flow<CatalogEntry?> {
        return _flows.getOrPut(catalogId) { MutableStateFlow(map[catalogId]) }.asStateFlow()
    }

    override fun getMetrics(): CacheMetrics = _metrics.copy(size = map.size)

    private fun notifyFlow(catalogId: String, entry: CatalogEntry?) {
        _flows.getOrPut(catalogId) { MutableStateFlow(entry) }.value = entry
    }
}

object CatalogCacheManager {
    private var instance: CatalogCache? = null
    fun getInstance(): CatalogCache = instance ?: CatalogCacheImpl().also { instance = it }
    fun reset() { instance = null }
}
