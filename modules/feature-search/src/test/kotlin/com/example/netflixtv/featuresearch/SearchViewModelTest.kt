package com.example.netflixtv.featuresearch

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
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel

    private val testContent = listOf(
        Content("m1", "Iron Man", "Hero", "", "", "", "Movies", 2024, "PG-13", "2h", false),
        Content("m2", "Spider Man", "Hero", "", "", "", "Movies", 2023, "PG", "1h50m", false),
        Content("m3", "Batman", "Dark", "", "", "", "Movies", 2022, "R", "2h30m", false)
    )

    private val fakeRepository = object : ContentRepository {
        override val catalogId: String = "test"
        override suspend fun loadCategories(): List<Category> = listOf(Category("Movies", testContent))
        override suspend fun getContentById(contentId: String): Content? = testContent.find { it.id == contentId }
        override fun getContentByIdSync(contentId: String): Content? = testContent.find { it.id == contentId }
        override fun getAllContent(): List<Content> = testContent
        override fun getAvailableCatalogs(): List<String> = listOf("test")
        override fun getItemsByCategory(category: String): List<Content> = testContent
        override fun observeCategories(): Flow<List<Category>> = MutableStateFlow(listOf(Category("Movies", testContent)))
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = SearchViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty query and no results`() = runBlocking {
        delay(100)
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.results.isEmpty())
        assertFalse(state.isSearching)
    }

    @Test
    fun `search filters results by title`() = runBlocking {
        delay(100)
        viewModel.onQueryChange("Iron")
        delay(500)

        val state = viewModel.uiState.value
        assertEquals(1, state.results.size)
        assertEquals("Iron Man", state.results[0].title)
    }

    @Test
    fun `search is case insensitive`() = runBlocking {
        delay(100)
        viewModel.onQueryChange("spider")
        delay(500)

        val state = viewModel.uiState.value
        assertEquals(1, state.results.size)
        assertEquals("Spider Man", state.results[0].title)
    }

    @Test
    fun `empty query clears results`() = runBlocking {
        delay(100)
        viewModel.onQueryChange("Iron")
        delay(500)
        assertTrue(viewModel.uiState.value.results.isNotEmpty())

        viewModel.onQueryChange("")
        delay(100)

        assertTrue(viewModel.uiState.value.results.isEmpty())
    }

    @Test
    fun `clearSearch resets to initial state`() = runBlocking {
        delay(100)
        viewModel.onQueryChange("man")
        delay(500)

        viewModel.clearSearch()
        delay(100)

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.results.isEmpty())
    }

    @Test
    fun `no match returns empty results`() = runBlocking {
        delay(100)
        viewModel.onQueryChange("zzzzz")
        delay(500)

        assertTrue(viewModel.uiState.value.results.isEmpty())
        assertFalse(viewModel.uiState.value.isSearching)
    }
}
