package com.zhiyu.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * MIUI-style rounded corner shape tokens.
 *
 * These shapes are applied to Material3 components via MaterialTheme.shapes.
 * MIUIX components use their own shape system via LocalIndication and
 * LocalOverscrollFactory -- they do not read MaterialTheme.shapes directly.
 *
 * @see extraSmall 4.dp — chips, badges, tight inline elements
 * @see small 8.dp — small cards, input fields, dialog corners, compact surfaces
 * @see medium 16.dp — standard cards, list items, menu surfaces, primary cards
 * @see large 20.dp — elevated cards, bottom sheet top corners, prominent surfaces
 * @see extraLarge 24.dp — modal bottom sheets, full-screen cards, maximum rounded surfaces
 */
val miuiShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
