package com.example.netflixtv.ui

import androidx.compose.runtime.Composable
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

    fun detailRoute(contentId: String) = "detail/$contentId"
    fun playerRoute(contentId: String) = "player/$contentId"
}

@Composable
fun AppNav(
    repository: ContentRepository,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                repository = repository,
                onContentClick = { content ->
                    navController.navigate(Routes.detailRoute(content.id))
                },
                onHeroCtaClick = { content ->
                    navController.navigate(Routes.playerRoute(content.id))
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
            val content = repository.getContentById(contentId)

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
            val content = repository.getContentById(contentId)

            content?.let {
                PlayerScreen(
                    videoUrl = it.videoUrl,
                    title = it.title,
                    posterUrl = it.backdropUrl.ifEmpty { it.thumbnailUrl },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}