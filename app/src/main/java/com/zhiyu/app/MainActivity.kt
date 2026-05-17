package com.zhiyu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
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
import top.yukonga.miuix.kmp.icon.extended.Settings as SettingsIcon

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val appPreferences: AppPreferences = org.koin.compose.koinInject()
            val themeMode by appPreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            val isDark = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !isDark

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val tabItems = listOf(
                NavigationItem("信息", MiuixIcons.Info) to ZhiYuRoutes.Info,
                NavigationItem("知识库", MiuixIcons.Folder) to ZhiYuRoutes.Knowledge,
                NavigationItem("发现", MiuixIcons.Search) to ZhiYuRoutes.Discover,
                NavigationItem("我的", MiuixIcons.SettingsIcon) to ZhiYuRoutes.Profile,
            )
            val tabRoutes = tabItems.map { it.second::class.qualifiedName }
            val isTabRoot = tabRoutes.any { it != null && currentRoute?.contains(it) == true }
            val selectedIndex = tabRoutes.indexOfFirst { it != null && currentRoute?.contains(it) == true }.coerceAtLeast(0)

            ZhiYuTheme(themeMode = themeMode) {
                Scaffold(
                    contentWindowInsets = WindowInsets.navigationBars,
                    bottomBar = {
                        if (isTabRoot) {
                            NavigationBar {
                                tabItems.forEachIndexed { index, (item, route) ->
                                    NavigationBarItem(
                                        selected = selectedIndex == index,
                                        onClick = {
                                            navController.navigate(route) {
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = item.icon,
                                        label = item.label,
                                    )
                                }
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
