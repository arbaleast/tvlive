package com.example.netflixtv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.netflixtv.data.StreamRepository
import com.example.netflixtv.featurehome.HomeScreen
import com.example.netflixtv.featurehome.HomeViewModel
import com.example.netflixtv.featureplayer.PlayerScreenRefactored
import com.example.netflixtv.media.PlayerManager
import com.example.netflixtv.uicommon.ErrorContent
import com.example.netflixtv.uicommon.LoadingContent
import com.example.netflixtv.uicommon.R
import com.example.netflixtv.uicommon.Routes

@Composable
fun AppNav(
    repository: StreamRepository,
    playerManager: PlayerManager,
    onPipClick: () -> Unit = {}
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onContentClick = { channel ->
                    navController.navigate(Routes.playerRoute(channel.id))
                }
            )
        }

        composable(
            route = Routes.PLAYER,
            arguments = listOf(navArgument("contentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
            var resolvedVideoUrl by remember { mutableStateOf<String?>(null) }
            var fetchAttempted by remember { mutableStateOf(false) }

            LaunchedEffect(contentId) {
                resolvedVideoUrl = repository.fetchLiveStreamUrl(contentId)
                fetchAttempted = true
            }

            when {
                resolvedVideoUrl == null && !fetchAttempted -> LoadingContent()
                resolvedVideoUrl == null && fetchAttempted -> {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ErrorContent(message = "Unable to load live stream")
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { navController.popBackStack() },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(stringResource(R.string.go_back))
                            }
                        }
                    }
                }
                resolvedVideoUrl != null -> {
                    PlayerScreenRefactored(
                        videoUrl = resolvedVideoUrl!!,
                        title = if (contentId == "cctv1") "CCTV-1 综合" else "CCTV-2 财经",
                        onBackClick = { navController.popBackStack() },
                        playerManager = playerManager,
                        onPipClick = onPipClick
                    )
                }
            }
        }
    }
}
