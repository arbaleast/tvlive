package com.example.netflixtv.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.netflixtv.TestMainActivity
import com.example.netflixtv.data.Category
import com.example.netflixtv.data.Content
import com.example.netflixtv.data.ContentRepository
import com.example.netflixtv.featurehome.HomeScreen
import com.example.netflixtv.featurehome.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * HomeScreen UI tests using createAndroidComposeRule<TestMainActivity>.
 * TestMainActivity is in main sourceset (not androidTest) so it's in the
 * main app's DEX and resolvable from the main app's process.
 * Uses TestScope to avoid Android framework dependency.
 * Works on any Android device/emulator including TV (API 28).
 */
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestMainActivity>()

    // ─── Test Data ─────────────────────────────────────────────────────────────

    private val testContent1 = Content(
        id = "movie1",
        title = "Test Movie",
        description = "A test movie description",
        thumbnailUrl = "",
        backdropUrl = "",
        videoUrl = "",
        category = "Movies",
        releaseYear = 2024,
        rating = "PG-13",
        duration = "1h 50m",
        isLive = false
    )

    private val testContent2 = Content(
        id = "tv1",
        title = "Test TV Show",
        description = "A test TV show description",
        thumbnailUrl = "",
        backdropUrl = "",
        videoUrl = "",
        category = "TV Shows",
        releaseYear = 2023,
        rating = "TV-MA",
        duration = "45m",
        isLive = false
    )

    private val testContent3 = Content(
        id = "doc1",
        title = "Test Documentary",
        description = "A test documentary",
        thumbnailUrl = "",
        backdropUrl = "",
        videoUrl = "",
        category = "Documentaries",
        releaseYear = 2024,
        rating = "PG",
        duration = "1h 30m",
        isLive = false
    )

    // ─── Fake Repository ─────────────────────────────────────────────────────

    private fun successRepository(): ContentRepository = object : ContentRepository {
        override val catalogId: String = "test"

        override suspend fun loadCategories(): List<Category> = listOf(
            Category("Movies", listOf(testContent1)),
            Category("TV Shows", listOf(testContent2)),
            Category("Documentaries", listOf(testContent3))
        )

        override suspend fun getContentById(contentId: String): Content? =
            getContentByIdSync(contentId)

        override fun getContentByIdSync(contentId: String): Content? =
            listOf(testContent1, testContent2, testContent3).find { it.id == contentId }

        override fun getAllContent(): List<Content> =
            listOf(testContent1, testContent2, testContent3)

        override fun getAvailableCatalogs(): List<String> = listOf("test")

        override fun getItemsByCategory(category: String): List<Content> =
            listOf(testContent1, testContent2, testContent3).filter { it.category == category }

        override fun observeCategories(): Flow<List<Category>> = flow {
            emit(loadCategories())
        }
    }

    private fun emptyRepository(): ContentRepository = object : ContentRepository {
        override val catalogId: String = "empty"

        override suspend fun loadCategories(): List<Category> = emptyList()
        override suspend fun getContentById(contentId: String): Content? = null
        override fun getContentByIdSync(contentId: String): Content? = null
        override fun getAllContent(): List<Content> = emptyList()
        override fun getAvailableCatalogs(): List<String> = emptyList()
        override fun getItemsByCategory(category: String): List<Content> = emptyList()
        override fun observeCategories(): Flow<List<Category>> = flow { emit(emptyList()) }
    }

    private fun errorRepository(): ContentRepository = object : ContentRepository {
        override val catalogId: String = "error"

        override suspend fun loadCategories(): List<Category> =
            throw RuntimeException("Network error")

        override suspend fun getContentById(contentId: String): Content? = null
        override fun getContentByIdSync(contentId: String): Content? = null
        override fun getAllContent(): List<Content> = emptyList()
        override fun getAvailableCatalogs(): List<String> = emptyList()
        override fun getItemsByCategory(category: String): List<Content> = emptyList()
        override fun observeCategories(): Flow<List<Category>> = flow { emit(emptyList()) }
    }

    // ─── Test Helpers ────────────────────────────────────────────────────────

    /** Creates a test-friendly HomeViewModel that uses TestScope instead of viewModelScope. */
    private fun testSetContent(content: @Composable () -> Unit) {
        (composeTestRule.activity as TestMainActivity).setTestContent {
            content()
        }
        // Ensure Compose measure/layout completes so semantics are populated
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()
    }

    private fun testViewModel(repo: ContentRepository): HomeViewModel {
        val testScope = CoroutineScope(Dispatchers.Unconfined)
        return HomeViewModel(repo, injectedScope = testScope)
    }

    // ─── Tests ────────────────────────────────────────────────────────────────

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun successState_showsHeaderAndCategories() {
        testSetContent {
            HomeScreen(
                viewModel = testViewModel(successRepository()),
                onContentClick = {},
                onHeroCtaClick = {}
            )
        }

        // LazyColumn items need scroll-to-node
        composeTestRule.onNodeWithText("Movies")
            .performScrollTo()
            .assertExists("Movies category header should be visible")
        composeTestRule.onNodeWithText("TV Shows")
            .performScrollTo()
            .assertExists("TV Shows category header should be visible")
        composeTestRule.onNodeWithText("Documentaries")
            .performScrollTo()
            .assertExists("Documentaries category header should be visible")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun successState_heroBannerDisplaysTitleAndDescription() {
        (composeTestRule.activity as TestMainActivity).setTestContent {
            HomeScreen(
                viewModel = testViewModel(successRepository()),
                onContentClick = {},
                onHeroCtaClick = {}
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Test Movie").assertExists("Hero title should be visible")
        composeTestRule.onNodeWithText("A test movie description").assertExists("Hero description should be visible")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun successState_showsPlayAndMoreInfoButtons() {
        (composeTestRule.activity as TestMainActivity).setTestContent {
            HomeScreen(
                viewModel = testViewModel(successRepository()),
                onContentClick = {},
                onHeroCtaClick = {}
            )
        }
        composeTestRule.waitForIdle()

        // HeroBanner uses "▶ Play" and "ℹ More Info" (icon + text)
        composeTestRule.onNodeWithText("Play", substring = true).assertExists("Play button should be visible")
        composeTestRule.onNodeWithText("More Info", substring = true).assertExists("More Info button should be visible")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun emptyState_rendersWithoutCrash() {
        (composeTestRule.activity as TestMainActivity).setTestContent {
            HomeScreen(
                viewModel = testViewModel(emptyRepository()),
                onContentClick = {},
                onHeroCtaClick = {}
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Movies").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun errorState_showsErrorMessage() {
        (composeTestRule.activity as TestMainActivity).setTestContent {
            HomeScreen(
                viewModel = testViewModel(errorRepository()),
                onContentClick = {},
                onHeroCtaClick = {}
            )
        }
        composeTestRule.waitForIdle()

        // UI shows "Error: Network error"
        composeTestRule.onNodeWithText("Error", substring = true).assertExists("Error state should show error message")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun onContentClick_isCalled() {
        var clickedContent: Content? = null
        (composeTestRule.activity as TestMainActivity).setTestContent {
            HomeScreen(
                viewModel = testViewModel(successRepository()),
                onContentClick = { clickedContent = it },
                onHeroCtaClick = {}
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Test Movie").performClick()
        assertEquals("Should receive content click callback", "Test Movie", clickedContent?.title)
    }
}
