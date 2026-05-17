package com.zhiyu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zhiyu.app.data.preferences.AppPreferences
import com.zhiyu.app.model.ThemeMode
import com.zhiyu.app.navigation.AppNavigation
import com.zhiyu.app.navigation.ZhiYuRoutes
import com.zhiyu.app.ui.theme.ZhiYuTheme
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Folder
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.theme.MiuixTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val appPreferences: AppPreferences = org.koin.compose.koinInject()
            val themeMode by appPreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val items = listOf(
                NavigationItem("信息", MiuixIcons.Info),
                NavigationItem("知识库", MiuixIcons.Folder),
                NavigationItem("发现", MiuixIcons.Search),
                NavigationItem("我的", MiuixIcons.Settings),
            )
            val routes = listOf(
                ZhiYuRoutes.Info::class.qualifiedName,
                ZhiYuRoutes.Knowledge::class.qualifiedName,
                ZhiYuRoutes.Discover::class.qualifiedName,
                ZhiYuRoutes.Profile::class.qualifiedName,
            )
            val selectedIndex = routes.indexOfFirst { it != null && currentRoute?.contains(it) == true }.coerceAtLeast(0)

            ZhiYuTheme(themeMode = themeMode) {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedIndex == index,
                                    onClick = {
                                        when (index) {
                                            0 -> navController.navigate(ZhiYuRoutes.Info) { launchSingleTop = true; restoreState = true }
                                            1 -> navController.navigate(ZhiYuRoutes.Knowledge) { launchSingleTop = true; restoreState = true }
                                            2 -> navController.navigate(ZhiYuRoutes.Discover) { launchSingleTop = true; restoreState = true }
                                            3 -> navController.navigate(ZhiYuRoutes.Profile) { launchSingleTop = true; restoreState = true }
                                        }
                                    },
                                    icon = item.icon,
                                    label = item.label,
                                )
                            }
                        }
                    },
                ) { paddingValues ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}
