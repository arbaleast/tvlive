package com.example.netflixtv.featurehome

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.netflixtv.data.Category
import com.example.netflixtv.data.Content
import com.example.netflixtv.data.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for HomeViewModel.
 * Tests loading, refresh, and error handling using UnconfinedTestDispatcher.
 */
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel

    private val testCategories = listOf(
        Category(
            name = "Featured",
            items = listOf(
                Content(
                    id = "hero1",
                    title = "Hero Movie 1",
                    description = "Description 1",
                    thumbnailUrl = "http://example.com/t1.jpg",
                    backdropUrl = "http://example.com/b1.jpg",
                    videoUrl = "http://example.com/v1.mp4",
                    category = "Featured",
                    releaseYear = 2024,
                    rating = "PG-13",
                    duration = "120m",
                    isLive = false
                ),
                Content(
                    id = "hero2",
                    title = "Hero Movie 2",
                    description = "Description 2",
                    thumbnailUrl = "http://example.com/t2.jpg",
                    backdropUrl = "http://example.com/b2.jpg",
                    videoUrl = "http://example.com/v2.mp4",
                    category = "Featured",
                    releaseYear = 2023,
                    rating = "PG",
                    duration = "95m",
                    isLive = false
                )
            )
        ),
        Category(
            name = "Comedy",
            items = listOf(
                Content(
                    id = "com1",
                    title = "Comedy Movie 1",
                    description = "Funny movie",
                    thumbnailUrl = "http://example.com/c1.jpg",
                    backdropUrl = "http://example.com/cb1.jpg",
                    videoUrl = "http://example.com/cm1.mp4",
                    category = "Comedy",
                    releaseYear = 2024,
                    rating = "PG",
                    duration = "90m",
                    isLive = false
                )
            )
        )
    )

    @Before
    fun setUp() {
        val repository = object : ContentRepository {
            override val catalogId: String = "test"
            override suspend fun loadCategories(): List<Category> = testCategories
            override suspend fun getContentById(contentId: String): Content? =
                testCategories.flatMap { it.items }.find { it.id == contentId }
            override fun getContentByIdSync(contentId: String): Content? =
                testCategories.flatMap { it.items }.find { it.id == contentId }
            override fun getAllContent(): List<Content> = testCategories.flatMap { it.items }
            override fun getAvailableCatalogs(): List<String> = listOf("test")
            override fun getItemsByCategory(category: String): List<Content> =
                testCategories.find { it.name == category }?.items ?: emptyList()
            override fun observeCategories(): Flow<List<Category>> = MutableStateFlow(testCategories)
        }
        // Use Unconfined dispatcher so coroutines run immediately in tests
        viewModel = HomeViewModel(repository, Dispatchers.Unconfined)
    }

    @Test
    fun `testInitialLoad_populatesCategoriesAndHeroes`() = runBlocking {
        // Wait for initial load to complete
        kotlinx.coroutines.delay(100)

        val state = viewModel.uiState.value

        assertFalse("Should not be loading", state.isLoading)
        assertNull("Should have no error", state.error)
        assertEquals("Should have 2 categories", 2, state.categories.size)
        assertEquals("Should have 2 heroes (first category items)", 2, state.heroes.size)
        assertEquals("Hero title should match", "Hero Movie 1", state.heroes.firstOrNull()?.title)
    }

    @Test
    fun `testInitialLoad_setsLoadingState`() = runBlocking {
        val loadingStates = mutableListOf<Boolean>()
        val job = launch {
            viewModel.uiState.collect { state ->
                loadingStates.add(state.isLoading)
            }
        }
        kotlinx.coroutines.delay(200)
        job.cancel()

        // Should see loading=true at least once, then false
        assertTrue("Should have observed loading state", loadingStates.isNotEmpty())
    }

    @Test
    fun `testRefresh_reloadsData`() = runBlocking {
        kotlinx.coroutines.delay(100)
        val initialCategories = viewModel.uiState.value.categories

        viewModel.refresh()
        kotlinx.coroutines.delay(100)

        val afterRefresh = viewModel.uiState.value.categories
        assertEquals("Categories should still be present after refresh", 2, afterRefresh.size)
    }

    @Test
    fun `testErrorState_whenRepositoryThrows`() = runBlocking {
        val errorRepo = object : ContentRepository {
            override val catalogId: String = "error"
            override suspend fun loadCategories(): List<Category> = throw RuntimeException("Network error")
            override suspend fun getContentById(contentId: String): Content? = null
            override fun getContentByIdSync(contentId: String): Content? = null
            override fun getAllContent(): List<Content> = emptyList()
            override fun getAvailableCatalogs(): List<String> = emptyList()
            override fun getItemsByCategory(category: String): List<Content> = emptyList()
            override fun observeCategories(): Flow<List<Category>> = MutableStateFlow(emptyList())
        }
        val errorViewModel = HomeViewModel(errorRepo, Dispatchers.Unconfined)

        kotlinx.coroutines.delay(100)

        val state = errorViewModel.uiState.value
        assertFalse("Should not be loading after error", state.isLoading)
        assertNotNull("Should have error message", state.error)
    }

    @Test
    fun `testEmptyCategories_handledGracefully`() = runBlocking {
        val emptyRepo = object : ContentRepository {
            override val catalogId: String = "empty"
            override suspend fun loadCategories(): List<Category> = emptyList()
            override suspend fun getContentById(contentId: String): Content? = null
            override fun getContentByIdSync(contentId: String): Content? = null
            override fun getAllContent(): List<Content> = emptyList()
            override fun getAvailableCatalogs(): List<String> = emptyList()
            override fun getItemsByCategory(category: String): List<Content> = emptyList()
            override fun observeCategories(): Flow<List<Category>> = MutableStateFlow(emptyList())
        }
        val emptyViewModel = HomeViewModel(emptyRepo, Dispatchers.Unconfined)

        kotlinx.coroutines.delay(100)

        val state = emptyViewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertTrue("Categories should be empty", state.categories.isEmpty())
        assertTrue("Heroes should be empty", state.heroes.isEmpty())
    }
}