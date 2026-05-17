package com.zhiyu.app.ui.theme

import android.graphics.Typeface
import android.graphics.fonts.Font
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
import com.zhiyu.app.R
import top.yukonga.miuix.kmp.theme.TextStyles
import top.yukonga.miuix.kmp.theme.defaultTextStyles

@Composable
fun rememberCJKFallbackFontFamily(): FontFamily {
    val context = LocalContext.current
    return remember {
        try {
            val notoSansSC = Font.Builder(context.resources, R.font.noto_sans_sc_subset).build()
            val platformFontFamily = PlatformFontFamily.Builder(notoSansSC).build()
            val platformTypeface = Typeface.CustomFallbackBuilder(platformFontFamily)
                .setSystemFallback("sans-serif")
                .build()
            FontFamily(typeface = platformTypeface)
        } catch (e: Exception) {
            FontFamily.Default
        }
    }
}

val cjkTextStyles: TextStyles
    @Composable get() {
        val cjkFamily = rememberCJKFallbackFontFamily()
        return defaultTextStyles().copy(
            title1 = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Normal, fontFamily = cjkFamily),
            title2 = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Normal, fontFamily = cjkFamily),
            title3 = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal, fontFamily = cjkFamily),
            title4 = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, fontFamily = cjkFamily),
            main = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal, fontFamily = cjkFamily),
            paragraph = TextStyle(fontSize = 17.sp, lineHeight = 1.5.em, fontFamily = cjkFamily),
            body1 = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, fontFamily = cjkFamily),
            headline1 = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal, fontFamily = cjkFamily),
            headline2 = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, fontFamily = cjkFamily),
            subtitle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = cjkFamily),
            body2 = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = cjkFamily),
            button = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium, fontFamily = cjkFamily),
            footnote1 = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, fontFamily = cjkFamily),
            footnote2 = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal, fontFamily = cjkFamily)
        )
    }

val cjkTypography: Typography
    @Composable get() {
        val cjkFamily = rememberCJKFallbackFontFamily()
        return Typography(
            displayLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Normal, lineHeight = 1.2.em, fontFamily = cjkFamily),
            headlineLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Normal, lineHeight = 1.2.em, fontFamily = cjkFamily),
            headlineMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal, lineHeight = 1.2.em, fontFamily = cjkFamily),
            headlineSmall = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, lineHeight = 1.2.em, fontFamily = cjkFamily),
            titleLarge = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal, lineHeight = 1.4.em, fontFamily = cjkFamily),
            titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, lineHeight = 1.4.em, fontFamily = cjkFamily),
            titleSmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, lineHeight = 1.4.em, fontFamily = cjkFamily),
            bodyLarge = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal, lineHeight = 1.5.em, fontFamily = cjkFamily),
            bodyMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 1.5.em, fontFamily = cjkFamily),
            bodySmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 1.5.em, fontFamily = cjkFamily),
            labelLarge = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium, lineHeight = 1.4.em, fontFamily = cjkFamily),
            labelMedium = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, lineHeight = 1.4.em, fontFamily = cjkFamily),
            labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal, lineHeight = 1.4.em, fontFamily = cjkFamily)
        )
    }
