package com.zhiyu.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.zhiyu.app.ui.screens.discover.DiscoverScreen
import com.zhiyu.app.ui.screens.info.InfoScreen
import com.zhiyu.app.ui.screens.knowledge.ArticleDetailScreen
import com.zhiyu.app.ui.screens.knowledge.ArticleEditorScreen
import com.zhiyu.app.ui.screens.knowledge.KnowledgeScreen
import com.zhiyu.app.ui.screens.profile.AboutScreen
import com.zhiyu.app.ui.screens.profile.ProfileScreen
import com.zhiyu.app.ui.screens.profile.SettingsScreen

private val tabEnter = fadeIn()
private val tabExit = fadeOut()
private val subEnter = slideInHorizontally { it } + fadeIn()
private val subExit = slideOutHorizontally { it } + fadeOut()

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ZhiYuRoutes.Info,
        modifier = modifier
    ) {
        composable<ZhiYuRoutes.Info>(
            enterTransition = { tabEnter },
            exitTransition = { tabExit },
            popEnterTransition = { tabEnter },
            popExitTransition = { tabExit },
        ) {
            InfoScreen()
        }
        composable<ZhiYuRoutes.Knowledge>(
            enterTransition = { tabEnter },
            exitTransition = { tabExit },
            popEnterTransition = { tabEnter },
            popExitTransition = { tabExit },
        ) {
            KnowledgeScreen(
                onNavigateToArticle = { articleId ->
                    navController.navigate(ZhiYuRoutes.ArticleDetail(articleId))
                },
                onNavigateToNewArticle = {
                    navController.navigate(ZhiYuRoutes.ArticleEditor(articleId = 0L))
                }
            )
        }
        composable<ZhiYuRoutes.ArticleDetail>(
            enterTransition = { subEnter },
            exitTransition = { subExit },
            popEnterTransition = { subEnter },
            popExitTransition = { subExit },
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<ZhiYuRoutes.ArticleDetail>()
            ArticleDetailScreen(
                articleId = route.articleId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditor = { articleId ->
                    navController.navigate(ZhiYuRoutes.ArticleEditor(articleId))
                }
            )
        }
        composable<ZhiYuRoutes.ArticleEditor>(
            enterTransition = { subEnter },
            exitTransition = { subExit },
            popEnterTransition = { subEnter },
            popExitTransition = { subExit },
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<ZhiYuRoutes.ArticleEditor>()
            ArticleEditorScreen(
                articleId = route.articleId,
                onNavigateBack = { navController.popBackStack() },
                onSaved = { _ ->
                    navController.popBackStack(ZhiYuRoutes.Knowledge, inclusive = false)
                }
            )
        }
        composable<ZhiYuRoutes.Discover>(
            enterTransition = { tabEnter },
            exitTransition = { tabExit },
            popEnterTransition = { tabEnter },
            popExitTransition = { tabExit },
        ) {
            DiscoverScreen()
        }
        composable<ZhiYuRoutes.Profile>(
            enterTransition = { tabEnter },
            exitTransition = { tabExit },
            popEnterTransition = { tabEnter },
            popExitTransition = { tabExit },
        ) {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(ZhiYuRoutes.Settings)
                }
            )
        }
        composable<ZhiYuRoutes.Settings>(
            enterTransition = { subEnter },
            exitTransition = { subExit },
            popEnterTransition = { subEnter },
            popExitTransition = { subExit },
        ) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = {
                    navController.navigate(ZhiYuRoutes.About)
                }
            )
        }
        composable<ZhiYuRoutes.About>(
            enterTransition = { subEnter },
            exitTransition = { subExit },
            popEnterTransition = { subEnter },
            popExitTransition = { subExit },
        ) {
            AboutScreen()
        }
    }
}
