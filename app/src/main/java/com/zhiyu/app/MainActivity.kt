package com.zhiyu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Folder
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.icon.extended.Settings as SettingsIcon
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton

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
            androidx.core.view.WindowCompat.getInsetsController(
                window, window.decorView
            ).isAppearanceLightStatusBars = !isDark

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val items = listOf(
                NavigationItem("信息", MiuixIcons.Info),
                NavigationItem("知识库", MiuixIcons.Folder),
                NavigationItem("发现", MiuixIcons.Search),
                NavigationItem("我的", MiuixIcons.SettingsIcon),
            )
            val tabRoutes = listOf(
                ZhiYuRoutes.Info::class.qualifiedName,
                ZhiYuRoutes.Knowledge::class.qualifiedName,
                ZhiYuRoutes.Discover::class.qualifiedName,
                ZhiYuRoutes.Profile::class.qualifiedName,
            )
            val tabTitles = listOf("信息", "知识库", "发现", "我的")
            val selectedIndex = tabRoutes.indexOfFirst { it != null && currentRoute?.contains(it) == true }.coerceAtLeast(0)

            // Determine title and back button visibility
            val isTabRoot = tabRoutes.any { it != null && currentRoute?.contains(it) == true }
            val title = when {
                currentRoute?.contains(ZhiYuRoutes.Settings::class.qualifiedName ?: "") == true -> "设置"
                currentRoute?.contains(ZhiYuRoutes.About::class.qualifiedName ?: "") == true -> "关于"
                currentRoute?.contains(ZhiYuRoutes.ArticleDetail::class.qualifiedName ?: "") == true -> "文章详情"
                currentRoute?.contains(ZhiYuRoutes.ArticleEditor::class.qualifiedName ?: "") == true -> "编辑文章"
                isTabRoot -> tabTitles[selectedIndex]
                else -> "知屿"
            }
            ZhiYuTheme(themeMode = themeMode) {
                Scaffold(
                    contentWindowInsets = WindowInsets.navigationBars,
                    topBar = {
                        TopAppBar(
                            title = title,
                            navigationIcon = {
                                if (!isTabRoot) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = MiuixIcons.Back,
                                            contentDescription = "返回"
                                        )
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        if (isTabRoot) {
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
