package com.zhiyu.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.zhiyu.app.model.ThemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Root theme composable for the ZhiYu app.
 *
 * Nesting order (critical for correct rendering):
 * ```
 * MaterialTheme (colorScheme, typography, shapes)   ← OUTER: provides Material3 context
 *   └── MiuixTheme (colorScheme, textStyles)        ← INNER: provides MIUIX context
 *        └── content()
 * ```
 *
 * [MaterialTheme] wraps [MiuixTheme] because MIUIX uses [CompositionLocalProvider]
 * directly and does NOT wrap [MaterialTheme]. Material3 components (Scaffold,
 * NavigationBar, etc.) from downstream phases require [MaterialTheme] context.
 * Nesting [MaterialTheme] outside [MiuixTheme] ensures both component sets see
 * the correct theme values.
 *
 * @param themeMode Theme mode: SYSTEM (follow system), LIGHT, or DARK.
 *                  Defaults to SYSTEM to follow device-wide dark mode setting.
 * @param content The composable content tree to be themed.
 */
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

    val miuiColors = if (isDark) miuiDarkColorScheme() else miuiLightColorScheme()
    val materialColors = if (isDark) zhiyuDarkColorScheme() else zhiyuLightColorScheme()

    val cjkTextStyle = cjkTextStyles
    val cjkType = cjkTypography

    MaterialTheme(
        colorScheme = materialColors,
        typography = cjkType,
        shapes = miuiShapes
    ) {
        MiuixTheme(
            colorScheme = miuiColors,
            textStyles = cjkTextStyle
        ) {
            content()
        }
    }
}
