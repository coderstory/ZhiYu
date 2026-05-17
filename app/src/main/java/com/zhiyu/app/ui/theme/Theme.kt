package com.zhiyu.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.zhiyu.app.model.ThemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ZhiYuTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val miuiColors = if (isDark) miuiDarkColors() else miuiLightColors()
    val materialColors = if (isDark) zhiyuDarkColorScheme() else zhiyuLightColorScheme()

    val cjkTextStyle = cjkTextStyles
    val cjkType = cjkTypography

    MaterialTheme(
        colorScheme = materialColors,
        typography = cjkType,
        shapes = miuiShapes
    ) {
        MiuixTheme(
            colors = miuiColors,
            textStyles = cjkTextStyle
        ) {
            content()
        }
    }
}
