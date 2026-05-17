package com.zhiyu.app.ui.theme

import android.graphics.Typeface
import android.graphics.fonts.FontFamily as PlatformFontFamily
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.zhiyu.app.R
import top.yukonga.miuix.kmp.theme.TextStyles
import top.yukonga.miuix.kmp.theme.defaultTextStyles

/**
 * Loads the subsetted Noto Sans SC font and builds a glyph-level fallback chain
 * using [Typeface.CustomFallbackBuilder] (API 29+).
 *
 * The fallback chain is:
 *   1. Bundled Noto Sans SC (subsetted, ~1-3MB)
 *   2. System "sans-serif" (ultimate fallback)
 *
 * This ensures Chinese characters never render as tofu (empty boxes).
 * If the font resource is null, returns [FontFamily.Default] as a safe fallback.
 *
 * CRITICAL: [Typeface.CustomFallbackBuilder] requires API 29+.
 * minSdk=36 guarantees availability.
 */
@Composable
fun rememberCJKFallbackFontFamily(): FontFamily {
    val context = LocalContext.current
    return remember {
        val notoSansSC = ResourcesCompat.getFont(context, R.font.noto_sans_sc_subset)
            ?: return@remember FontFamily.Default

        val platformFontFamily = PlatformFontFamily.Builder(
            android.graphics.Font.Builder(notoSansSC).build()
        ).build()

        val platformTypeface = Typeface.CustomFallbackBuilder(platformFontFamily)
            .setSystemFallback("sans-serif")
            .build()

        FontFamily(typeface = platformTypeface)
    }
}

// =============================================================================
// MIUIX TextStyles (14 styles) with CJK font family
// =============================================================================

/**
 * MIUIX TextStyles with CJK font fallback applied to all 14 styles.
 *
 * This is consumed by [MiuixTheme] via the [TextStyles] parameter.
 * Each style preserves the MIUIX default size and weight while overriding
 * the font family to the CJK-capable [FontFamily] from [rememberCJKFallbackFontFamily].
 */
val cjkTextStyles: TextStyles
    @Composable get() {
        val cjkFamily = rememberCJKFallbackFontFamily()
        return defaultTextStyles().copy(
            title1 = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = cjkFamily
            ),
            title2 = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = cjkFamily
            ),
            title3 = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = cjkFamily
            ),
            title4 = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = cjkFamily
            ),
            main = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = cjkFamily
            ),
            paragraph = TextStyle(
                fontSize = 17.sp,
                lineHeight = 1.5.em,
                fontFamily = cjkFamily
            ),
            body1 = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = cjkFamily
            ),
            headline1 = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = cjkFamily
            ),
            headline2 = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = cjkFamily
            ),
            subtitle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = cjkFamily
            ),
            body2 = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = cjkFamily
            ),
            button = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = cjkFamily
            ),
            footnote1 = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = cjkFamily
            ),
            footnote2 = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = cjkFamily
            )
        )
    }

// =============================================================================
// Material3 Typography (13 roles) with CJK font family
// =============================================================================

/**
 * Material3 [Typography] with CJK font fallback applied to all 13 text roles.
 *
 * This is consumed by [MaterialTheme] and is the typography source for all
 * Material3 components (Scaffold, NavigationBar, etc.).
 *
 * Mapping from MIUIX styles to Material3 roles per UI-SPEC.md:
 * - display/large maps to MIUIX title1
 * - headline/* maps to MIUIX title2-4
 * - title/* maps to MIUIX main/body1/subtitle
 * - body/* maps to MIUIX paragraph/body1/body2
 * - label/* maps to MIUIX button/footnote1/footnote2
 */
val cjkTypography: Typography
    @Composable get() {
        val cjkFamily = rememberCJKFallbackFontFamily()
        return Typography(
            displayLarge = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.2.em,
                fontFamily = cjkFamily
            ),
            headlineLarge = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.2.em,
                fontFamily = cjkFamily
            ),
            headlineMedium = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.2.em,
                fontFamily = cjkFamily
            ),
            headlineSmall = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.2.em,
                fontFamily = cjkFamily
            ),
            titleLarge = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.4.em,
                fontFamily = cjkFamily
            ),
            titleMedium = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 1.4.em,
                fontFamily = cjkFamily
            ),
            titleSmall = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 1.4.em,
                fontFamily = cjkFamily
            ),
            bodyLarge = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.5.em,
                fontFamily = cjkFamily
            ),
            bodyMedium = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.5.em,
                fontFamily = cjkFamily
            ),
            bodySmall = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.5.em,
                fontFamily = cjkFamily
            ),
            labelLarge = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 1.4.em,
                fontFamily = cjkFamily
            ),
            labelMedium = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.4.em,
                fontFamily = cjkFamily
            ),
            labelSmall = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.4.em,
                fontFamily = cjkFamily
            )
        )
    }
