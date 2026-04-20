package com.example.netflixtv

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.netflixtv.data.ContentRepository
import com.example.netflixtv.data.ContentRepositoryImpl
import com.example.netflixtv.featurehome.HomeScreen
import com.example.netflixtv.featurehome.HomeViewModel
import com.example.netflixtv.featurebrowse.BrowseScreen
import com.example.netflixtv.featurebrowse.BrowseViewModel
import com.example.netflixtv.featuredetail.DetailScreen
import com.example.netflixtv.featureplayer.PlayerScreen
import com.example.netflixtv.featuresearch.SearchScreen
import com.example.netflixtv.featuresearch.SearchViewModel
import com.example.netflixtv.uicommon.Routes

@Composable
fun AppNav(context: android.content.Context) {
    val navController = rememberNavController()
    val repository: ContentRepository = remember { ContentRepositoryImpl(context, "default") }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val viewModel = remember { HomeViewModel(repository) }
            HomeScreen(
                viewModel = viewModel,
                onContentClick = { content ->
                    navController.navigate(Routes.detailRoute(content.id))
                },
                onHeroCtaClick = { content ->
                    navController.navigate(Routes.playerRoute(content.id))
                }
            )
        }

        composable(Routes.BROWSE) {
            val viewModel = remember { BrowseViewModel(repository) }
            BrowseScreen(
                viewModel = viewModel,
                onContentClick = { content ->
                    navController.navigate(Routes.detailRoute(content.id))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("contentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
            val content = remember(contentId) { repository.getContentByIdSync(contentId) }
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
            arguments = listOf(navArgument("contentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
            val content = remember(contentId) { repository.getContentByIdSync(contentId) }
            content?.let {
                PlayerScreen(
                    videoUrl = it.videoUrl,
                    title = it.title,
                    posterUrl = it.thumbnailUrl,
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
        ) { _ ->
            val viewModel = remember { SearchViewModel(repository) }
            SearchScreen(
                viewModel = viewModel,
                onContentClick = { content ->
                    navController.navigate(Routes.detailRoute(content.id))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
