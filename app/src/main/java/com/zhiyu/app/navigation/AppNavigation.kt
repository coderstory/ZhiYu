package com.zhiyu.app.navigation

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
        composable<ZhiYuRoutes.Info> {
            InfoScreen()
        }
        composable<ZhiYuRoutes.Knowledge> {
            KnowledgeScreen(
                onNavigateToArticle = { articleId ->
                    navController.navigate(ZhiYuRoutes.ArticleDetail(articleId))
                },
                onNavigateToNewArticle = {
                    navController.navigate(ZhiYuRoutes.ArticleEditor(articleId = 0L))
                }
            )
        }
        composable<ZhiYuRoutes.ArticleDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<ZhiYuRoutes.ArticleDetail>()
            ArticleDetailScreen(
                articleId = route.articleId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditor = { articleId ->
                    navController.navigate(ZhiYuRoutes.ArticleEditor(articleId))
                }
            )
        }
        composable<ZhiYuRoutes.ArticleEditor> { backStackEntry ->
            val route = backStackEntry.toRoute<ZhiYuRoutes.ArticleEditor>()
            ArticleEditorScreen(
                articleId = route.articleId,
                onNavigateBack = { navController.popBackStack() },
                onSaved = { _ ->
                    // After saving, pop back to knowledge list
                    navController.popBackStack(ZhiYuRoutes.Knowledge, inclusive = false)
                }
            )
        }
        composable<ZhiYuRoutes.Discover> {
            DiscoverScreen()
        }
        composable<ZhiYuRoutes.Profile> {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(ZhiYuRoutes.Settings)
                }
            )
        }
        composable<ZhiYuRoutes.Settings> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = {
                    navController.navigate(ZhiYuRoutes.About)
                }
            )
        }
        composable<ZhiYuRoutes.About> {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
