package com.zhiyu.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import top.yukonga.miuix.kmp.theme.Colors
import top.yukonga.miuix.kmp.theme.lightColorScheme as miuixLightColorScheme
import top.yukonga.miuix.kmp.theme.darkColorScheme as miuixDarkColorScheme

// =============================================================================
// Tech Blue Primary Accent
// =============================================================================

val TechBlue = Color(0xFF1677FF)
val TechBlueDark = Color(0xFF4096FF)
val TechBlueContainer = Color(0xFFE6F4FF)
val OnTechBlueContainer = Color(0xFF003EB3)

// =============================================================================
// Light Theme Colors
// =============================================================================

val BackgroundLight = Color(0xFFF5F7FA)
val OnBackgroundLight = Color(0xFF1A1A1A)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF1A1A1A)
val SurfaceVariantLight = Color(0xFFF5F7FA)
val OnSurfaceSecondaryLight = Color(0xFF8C8C8C)
val ErrorLight = Color(0xFFE53935)
val OnErrorLight = Color(0xFFFFFFFF)
val OutlineLight = Color(0xFFD9D9D9)
val DividerLineLight = Color(0xFFE8E8E8)

// =============================================================================
// Dark Theme Colors
// =============================================================================

val BackgroundDark = Color(0xFF141414)
val OnBackgroundDark = Color(0xFFF2F2F2)
val SurfaceDark = Color(0xFF1E1E1E)
val OnSurfaceDark = Color(0xFFF2F2F2)
val SurfaceVariantDark = Color(0xFF2A2A2A)
val OnSurfaceSecondaryDark = Color(0xFFA6A6A6)
val ErrorDark = Color(0xFFEF5350)
val OnErrorDark = Color(0xFFFFFFFF)
val OutlineDark = Color(0xFF404040)
val DividerLineDark = Color(0xFF333333)

// =============================================================================
// MIUIX Color Schemes
// =============================================================================

fun miuiLightColors(): Colors = miuixLightColorScheme().copy(
    primary = TechBlue,
    onPrimary = Color.White,
    primaryContainer = TechBlueContainer,
    onPrimaryContainer = OnTechBlueContainer,
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
    primary = TechBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF002966),
    onPrimaryContainer = Color(0xFFD6E9FF),
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
// Material3 Color Schemes
// =============================================================================

fun zhiyuLightColorScheme(): ColorScheme = lightColorScheme(
    primary = TechBlue,
    onPrimary = Color.White,
    primaryContainer = TechBlueContainer,
    onPrimaryContainer = OnTechBlueContainer,
    secondary = Color(0xFF4096FF),
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
    primary = TechBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF002966),
    onPrimaryContainer = Color(0xFFD6E9FF),
    secondary = Color(0xFF4096FF),
    onSecondary = Color.White,
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
