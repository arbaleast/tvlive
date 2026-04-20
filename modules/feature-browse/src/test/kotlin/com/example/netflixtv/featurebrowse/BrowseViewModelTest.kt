package com.example.netflixtv.featurebrowse

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.netflixtv.data.Category
import com.example.netflixtv.data.Content
import com.example.netflixtv.data.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class BrowseViewModelTest {

    private lateinit var viewModel: BrowseViewModel

    private val testCategories = listOf(
        Category("Trending", listOf(
            Content("t1", "Movie A", "Desc", "", "", "", "Trending", 2024, "PG-13", "2h", false)
        )),
        Category("Comedy", listOf(
            Content("c1", "Movie B", "Desc", "", "", "", "Comedy", 2023, "PG", "1h50m", false)
        ))
    )

    private val fakeRepository = object : ContentRepository {
        override val catalogId: String = "default"
        override suspend fun loadCategories(): List<Category> = testCategories
        override suspend fun getContentById(contentId: String): Content? =
            testCategories.flatMap { it.items }.find { it.id == contentId }
        override fun getContentByIdSync(contentId: String): Content? =
            testCategories.flatMap { it.items }.find { it.id == contentId }
        override fun getAllContent(): List<Content> = testCategories.flatMap { it.items }
        override fun getAvailableCatalogs(): List<String> = listOf("default")
        override fun getItemsByCategory(category: String): List<Content> =
            testCategories.find { it.name == category }?.items ?: emptyList()
        override fun observeCategories(): Flow<List<Category>> = MutableStateFlow(testCategories)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = BrowseViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load populates categories`() = runBlocking {
        delay(200)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.catalogSwitchInProgress)
        assertNull(state.error)
        assertEquals(2, state.categories.size)
    }

    @Test
    fun `switchCatalog reloads current catalog`() = runBlocking {
        delay(200)

        viewModel.switchCatalog("anyCatalogId")
        delay(200)

        val state = viewModel.uiState.value
        assertEquals("default", state.currentCatalog)
        assertFalse(state.catalogSwitchInProgress)
    }

    @Test
    fun `switchCatalog with same id does not reload`() = runBlocking {
        delay(200)

        viewModel.switchCatalog("default")
        delay(100)

        assertEquals("default", viewModel.uiState.value.currentCatalog)
    }

    @Test
    fun `refresh reloads current catalog`() = runBlocking {
        delay(200)

        viewModel.refresh()
        delay(200)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.categories.size)
    }

    @Test
    fun `getAvailableCatalogs returns repository catalogs`() {
        assertEquals(listOf("default"), viewModel.getAvailableCatalogs())
    }

    @Test
    fun `error state when repository throws`() = runBlocking {
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

        val errorViewModel = BrowseViewModel(errorRepo)
        delay(200)

        val state = errorViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }
}
