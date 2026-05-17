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
import com.zhiyu.app.ui.screens.knowledge.KnowledgeScreen
import com.zhiyu.app.ui.screens.profile.ProfileScreen

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
            KnowledgeScreen()
        }
        composable<ZhiYuRoutes.Discover> {
            DiscoverScreen()
        }
        composable<ZhiYuRoutes.Profile> {
            ProfileScreen()
        }
    }
}
