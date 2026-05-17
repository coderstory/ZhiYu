package com.zhiyu.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import top.yukonga.miuix.kmp.theme.Colors
import top.yukonga.miuix.kmp.theme.lightColorScheme as miuixLightColorScheme
import top.yukonga.miuix.kmp.theme.darkColorScheme as miuixDarkColorScheme

// =============================================================================
// MIUI Primary Accent Constants
// =============================================================================

val MIUI_Warm_Orange = Color(0xFFFF6B35)
val MIUI_Warm_Orange_Dark = Color(0xFFFF8A50)

// =============================================================================
// Light Theme Colors
// =============================================================================

val PrimaryContainerLight = Color(0xFFFFF0E6)
val OnPrimaryContainerLight = Color(0xFF4A1800)
val BackgroundLight = Color(0xFFFFF5F0)
val OnBackgroundLight = Color(0xFF1A1A1A)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF1A1A1A)
val SurfaceVariantLight = Color(0xFFFFF5F0)
val OnSurfaceSecondaryLight = Color(0xFF8C8C8C)
val ErrorLight = Color(0xFFE53935)
val OnErrorLight = Color(0xFFFFFFFF)
val OutlineLight = Color(0xFFD9D9D9)
val DividerLineLight = Color(0xFFE0E0E0)

// =============================================================================
// Dark Theme Colors
// =============================================================================

val PrimaryContainerDark = Color(0xFF4A2810)
val OnPrimaryContainerDark = Color(0xFFFFDCC4)
val BackgroundDark = Color(0xFF1A1A1A)
val OnBackgroundDark = Color(0xFFF2F2F2)
val SurfaceDark = Color(0xFF000000)
val OnSurfaceDark = Color(0xFFF2F2F2)
val SurfaceVariantDark = Color(0xFF242424)
val OnSurfaceSecondaryDark = Color(0xFFA6A6A6)
val ErrorDark = Color(0xFFEF5350)
val OnErrorDark = Color(0xFFFFFFFF)
val OutlineDark = Color(0xFF404040)
val DividerLineDark = Color(0xFF393939)

// =============================================================================
// MIUIX Color Schemes
// =============================================================================

fun miuiLightColors(): Colors = miuixLightColorScheme().copy(
    primary = MIUI_Warm_Orange,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceSecondary = OnSurfaceSecondaryLight,
    error = ErrorLight,
    onError = OnErrorLight,
    outline = OutlineLight,
    dividerLine = DividerLineLight
)

fun miuiDarkColors(): Colors = miuixDarkColorScheme().copy(
    primary = Color(0xFFFF8A50),
    onPrimary = Color.White,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceSecondary = OnSurfaceSecondaryDark,
    error = ErrorDark,
    onError = OnErrorDark,
    outline = OutlineDark,
    dividerLine = DividerLineDark
)

// =============================================================================
// Material3 Color Schemes (for MaterialTheme layer)
// =============================================================================

fun zhiyuLightColorScheme(): ColorScheme = lightColorScheme(
    primary = MIUI_Warm_Orange,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = Color(0xFFFF8A50),
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF666666),
    error = ErrorLight,
    onError = OnErrorLight,
    outline = OutlineLight,
    outlineVariant = Color(0xFFE8E8E8)
)

fun zhiyuDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = Color(0xFFFF8A50),
    onPrimary = Color.White,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = Color(0xFFFFA570),
    onSecondary = Color.Black,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFA6A6A6),
    error = ErrorDark,
    onError = OnErrorDark,
    outline = OutlineDark,
    outlineVariant = Color(0xFF333333)
)
