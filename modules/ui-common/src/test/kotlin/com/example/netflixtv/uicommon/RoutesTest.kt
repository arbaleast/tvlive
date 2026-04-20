package com.example.netflixtv.uicommon

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Routes navigation object.
 */
class RoutesTest {

    @Test
    fun `testRouteConstants_areCorrect`() {
        assertEquals("home", Routes.HOME)
        assertEquals("detail/{contentId}", Routes.DETAIL)
        assertEquals("player/{contentId}", Routes.PLAYER)
        assertEquals("search?query={query}", Routes.SEARCH)
        assertEquals("browse", Routes.BROWSE)
    }

    @Test
    fun `testDetailRoute_generatesCorrectPath`() {
        val route = Routes.detailRoute("movie123")
        assertEquals("detail/movie123", route)
    }

    @Test
    fun `testPlayerRoute_generatesCorrectPath`() {
        val route = Routes.playerRoute("video456")
        assertEquals("player/video456", route)
    }

    @Test
    fun `testSearchRoute_withQuery`() {
        val route = Routes.searchRoute("test query")
        assertEquals("search?query=test query", route)
    }

    @Test
    fun `testSearchRoute_emptyQuery`() {
        val route = Routes.searchRoute("")
        assertEquals("search", route)
    }

    @Test
    fun `testSearchRoute_noArguments`() {
        val route = Routes.searchRoute()
        assertEquals("search", route)
    }

    @Test
    fun `testBrowseRoute_returnsBROWSE`() {
        assertEquals(Routes.BROWSE, Routes.browseRoute())
    }

    @Test
    fun `testDetailRoute_withSpecialCharacters`() {
        val route = Routes.detailRoute("movie-with-dashes_and_underscores")
        assertEquals("detail/movie-with-dashes_and_underscores", route)
    }

    @Test
    fun `testPlayerRoute_withUUID`() {
        val uuid = "550e8400-e29b-41d4-a716-446655440000"
        val route = Routes.playerRoute(uuid)
        assertEquals("player/550e8400-e29b-41d4-a716-446655440000", route)
    }
}
