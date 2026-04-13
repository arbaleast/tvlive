package com.example.netflixtv.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.netflixtv.data.ContentRepository

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail/{contentId}"
    const val PLAYER = "player/{contentId}"
    const val SEARCH = "search?query={query}"
    const val BROWSE = "browse"

    fun detailRoute(contentId: String) = "detail/$contentId"
    fun playerRoute(contentId: String) = "player/$contentId"
    fun searchRoute(query: String = "") = if (query.isNotEmpty()) "search?query=$query" else "search"
    fun browseRoute() = BROWSE
}

@Composable
fun AppNav(
    context: Context,
    navController: NavHostController = rememberNavController()
) {
    val repository = remember { ContentRepository(context, "default") }
    val homeViewModel: HomeViewModel = viewModel { HomeViewModel(repository) }
    val browseViewModel: BrowseViewModel = viewModel { BrowseViewModel(context) }
    val searchViewModel: SearchViewModel = viewModel { SearchViewModel(repository) }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onContentClick = { content ->
                    navController.navigate(Routes.detailRoute(content.id))
                },
                onHeroCtaClick = { content ->
                    navController.navigate(Routes.playerRoute(content.id))
                },
                onSearchClick = {
                    navController.navigate(Routes.searchRoute())
                },
                onBrowseClick = {
                    navController.navigate(Routes.browseRoute())
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("contentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
            val content = repository.getContentByIdSync(contentId)

            content?.let {
                DetailScreen(
                    content = it,
                    onBackClick = { navController.popBackStack() },
                    onPlayClick = { navController.navigate(Routes.playerRoute(contentId)) }
                )
            }
        }

        composable(
            route = Routes.PLAYER,
            arguments = listOf(
                navArgument("contentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
            val content = repository.getContentByIdSync(contentId)

            content?.let {
                PlayerScreen(
                    videoUrl = it.videoUrl,
                    title = it.title,
                    posterUrl = it.backdropUrl.ifEmpty { it.thumbnailUrl },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Routes.SEARCH,
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchScreen(
                viewModel = searchViewModel,
                onContentClick = { content ->
                    navController.navigate(Routes.detailRoute(content.id))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.BROWSE) {
            BrowseScreen(
                viewModel = browseViewModel,
                onContentClick = { content ->
                    navController.navigate(Routes.detailRoute(content.id))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}