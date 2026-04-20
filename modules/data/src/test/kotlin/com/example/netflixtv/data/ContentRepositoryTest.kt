package com.example.netflixtv.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.lang.reflect.Field

/**
 * Unit tests for ContentRepository.
 * Tests query methods using reflection to set test data.
 */
@RunWith(AndroidJUnit4::class)
class ContentRepositoryTest {

    private lateinit var repository: ContentRepositoryImpl
    
    // Test data for setting via reflection
    private val testCategories: List<Category> = listOf(
        Category(
            name = "TestCategory",
            items = listOf(
                Content(
                    id = "tc1",
                    title = "Test Item 1",
                    description = "Test description",
                    thumbnailUrl = "http://example.com/thumb1.jpg",
                    backdropUrl = "http://example.com/backdrop1.jpg",
                    videoUrl = "http://example.com/video1.mp4",
                    category = "TestCategory",
                    releaseYear = 2024,
                    rating = "G",
                    duration = "90m",
                    isLive = false
                )
            )
        )
    )

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        repository = ContentRepositoryImpl(context, "test_catalog")
        
        // Inject test data via reflection to bypass asset loading
        setPrivateField(repository, "cachedCategories", testCategories)
        setPrivateField(repository, "_categoriesFlow", MutableStateFlow<List<Category>>(testCategories))
    }
    
    private fun setPrivateField(obj: Any, fieldName: String, value: Any) {
        val field: Field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(obj, value)
    }

    @Test
    fun `testGetContentById_returnsCorrectItem`() = runBlocking {
        // When: query for the known ID
        val content = repository.getContentById("tc1")
        // Then: should return the content
        assertNotNull(content)
        assertEquals("tc1", content!!.id)
        assertEquals("Test Item 1", content.title)
        assertEquals("Test description", content.description)
    }

    @Test
    fun `testGetContentById_returnsNullForUnknownId`() = runBlocking {
        // When: query for unknown ID
        val content = repository.getContentById("unknown")
        // Then: should return null
        assertNull(content)
    }

    @Test
    fun `testGetItemsByCategory_returnsNonEmpty`() {
        // When: get items for category
        val items = repository.getItemsByCategory("TestCategory")
        // Then: should return items
        assertEquals(1, items.size)
        assertEquals("tc1", items[0].id)
    }

    @Test
    fun `testGetItemsByCategory_returnsEmptyForUnknownCategory`() {
        // When: get items for unknown category
        val items = repository.getItemsByCategory("UnknownCategory")
        // Then: should return empty
        assertTrue(items.isEmpty())
    }

    @Test
    fun `testGetAllContent_returnsAllItems`() {
        // When: get all content
        val allContent = repository.getAllContent()
        // Then: should return all
        assertEquals(1, allContent.size)
        assertEquals("tc1", allContent[0].id)
    }

    @Test
    fun `testObserveCategories_emitsValue`() = runBlocking {
        // When: collect the flow
        val collected = repository.observeCategories().first()
        // Then: should emit test categories
        assertEquals(1, collected.size)
        assertEquals("TestCategory", collected[0].name)
    }
}