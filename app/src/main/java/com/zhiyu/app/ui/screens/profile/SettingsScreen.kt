package com.zhiyu.app.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhiyu.app.data.preferences.AppPreferences
import com.zhiyu.app.model.ThemeMode
import org.koin.compose.koinInject
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Help
import top.yukonga.miuix.kmp.icon.extended.Theme as ThemeIcon
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.RadioButtonLocation
import top.yukonga.miuix.kmp.preference.RadioButtonPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {}
) {
    val appPreferences: AppPreferences = koinInject()
    val themeMode by appPreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

    Column(modifier = Modifier.fillMaxSize()) {
        SmallTopAppBar(
            title = "设置",
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = MiuixIcons.Back,
                        contentDescription = "返回",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SmallTitle(text = "主题模式")

            RadioButtonPreference(
                title = "跟随系统",
                selected = themeMode == ThemeMode.SYSTEM,
                onClick = { appPreferences.setThemeMode(ThemeMode.SYSTEM) },
                radioButtonLocation = RadioButtonLocation.End,
                startAction = {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MiuixIcons.ThemeIcon,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                        )
                    }
                }
            )

            RadioButtonPreference(
                title = "浅色模式",
                selected = themeMode == ThemeMode.LIGHT,
                onClick = { appPreferences.setThemeMode(ThemeMode.LIGHT) },
                radioButtonLocation = RadioButtonLocation.End,
                startAction = {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MiuixIcons.ThemeIcon,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                        )
                    }
                }
            )

            RadioButtonPreference(
                title = "深色模式",
                selected = themeMode == ThemeMode.DARK,
                onClick = { appPreferences.setThemeMode(ThemeMode.DARK) },
                radioButtonLocation = RadioButtonLocation.End,
                startAction = {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MiuixIcons.ThemeIcon,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                        )
                    }
                }
            )

            SmallTitle(text = "其他")

            ArrowPreference(
                title = "关于知屿",
                summary = "版本信息、开源许可",
                startAction = {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Help,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                        )
                    }
                },
                onClick = onNavigateToAbout
            )
        }
    }
}
