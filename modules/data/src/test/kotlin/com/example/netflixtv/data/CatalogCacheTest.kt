package com.example.netflixtv.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Unit tests for CatalogCache.
 * Tests cache operations: get, put, invalidate, observe, and TTL.
 */
@RunWith(AndroidJUnit4::class)
class CatalogCacheTest {

    private lateinit var cache: CatalogCache

    @Before
    fun setUp() {
        cache = CatalogCacheImpl()
    }

    @After
    fun tearDown() {
        runBlocking { cache.invalidateAll() }
    }

    @Test
    fun `testPutAndGet_returnsStoredValue`() = runBlocking {
        // Given: an empty cache
        val testData = listOf(Category("TestCategory", listOf()))
        val entry = CatalogEntry(
            catalogId = "test_catalog",
            categories = testData,
            loadedAt = System.currentTimeMillis(),
            ttlMs = TimeUnit.MINUTES.toMillis(30)
        )
        // When: we put data into the cache
        cache.put("test_catalog", entry)
        // Then: we should be able to retrieve it
        val result = cache.get("test_catalog")
        assertNotNull(result)
        assertEquals("test_catalog", result!!.catalogId)
        assertEquals(1, result.categories.size)
        assertEquals("TestCategory", result.categories[0].name)
    }

    @Test
    fun `testGet_returnsNullForMissingKey`() = runBlocking {
        // Given: an empty cache
        // When: we get a non-existent key
        val result = cache.get("missing_key")
        // Then: we should get null
        assertNull(result)
    }

    @Test
    fun `testInvalidate_removesKey`() = runBlocking {
        // Given: cache with data
        val testData = listOf(Category("TestCategory", listOf()))
        val entry = CatalogEntry(
            catalogId = "test_key",
            categories = testData,
            loadedAt = System.currentTimeMillis(),
            ttlMs = TimeUnit.MINUTES.toMillis(30)
        )
        cache.put("test_key", entry)
        // When: we invalidate the key
        cache.invalidate("test_key")
        // Then: the key should no longer be accessible
        val result = cache.get("test_key")
        assertNull(result)
    }

    @Test
    fun `testInvalidateAll_clearsAllData`() = runBlocking {
        // Given: cache with multiple keys
        val cat1 = CatalogEntry(
            catalogId = "key1",
            categories = listOf(Category("Cat1", listOf())),
            loadedAt = System.currentTimeMillis(),
            ttlMs = TimeUnit.MINUTES.toMillis(30)
        )
        val cat2 = CatalogEntry(
            catalogId = "key2",
            categories = listOf(Category("Cat2", listOf())),
            loadedAt = System.currentTimeMillis(),
            ttlMs = TimeUnit.MINUTES.toMillis(30)
        )
        cache.put("key1", cat1)
        cache.put("key2", cat2)
        // When: we invalidate all
        cache.invalidateAll()
        // Then: both keys should return null
        assertNull(cache.get("key1"))
        assertNull(cache.get("key2"))
    }

    @Test
    fun `testObserve_emitsValueOnPut`() = runBlocking {
        // Given: cache with data
        val testData = listOf(Category("TestCategory", listOf()))
        val entry = CatalogEntry(
            catalogId = "test_key",
            categories = testData,
            loadedAt = System.currentTimeMillis(),
            ttlMs = TimeUnit.MINUTES.toMillis(30)
        )
        cache.put("test_key", entry)
        // When: we collect the observe flow
        val collected = cache.observe("test_key").first()
        // Then: we should get an update notification
        assertNotNull(collected)
        assertEquals("test_key", collected!!.catalogId)
    }

    @Test
    fun `testTTL_expiration`() = runBlocking {
        // Given: cache with very short TTL data
        val testData = listOf(Category("TestCategory", listOf()))
        val entry = CatalogEntry(
            catalogId = "test_key",
            categories = testData,
            loadedAt = System.currentTimeMillis(),
            ttlMs = 50 // Very short TTL for testing
        )
        cache.put("test_key", entry)
        // Wait for TTL to expire
        kotlinx.coroutines.delay(100)
        // When: we try to get the expired key
        val result = cache.get("test_key")
        // Then: we should get null (expired)
        assertNull(result)
    }
}