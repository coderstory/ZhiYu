package com.zhiyu.app.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 8-point spacing grid for the ZhiYu app.
 *
 * All spacing values follow the 8-point grid system for visual consistency.
 * Consumed by all downstream phases (2-5) for layout, padding, and gaps.
 *
 * Usage:
 * ```
 * Modifier.padding(Spacing.md)
 * Modifier.size(Spacing.xl)
 * ```
 */
object Spacing {
    /** 4.dp — Icon gaps, inline padding, tight spacing */
    val xs: Dp = 4.dp

    /** 8.dp — Compact element spacing, chip margins */
    val sm: Dp = 8.dp

    /** 16.dp — Default element spacing, card content padding */
    val md: Dp = 16.dp

    /** 24.dp — Section padding, list item margins */
    val lg: Dp = 24.dp

    /** 32.dp — Layout gaps, screen edge padding */
    val xl: Dp = 32.dp

    /** 48.dp — Major section breaks, modal bottom sheet margins */
    val xxl: Dp = 48.dp

    /** 64.dp — Page-level spacing, top-of-screen padding */
    val xxxl: Dp = 64.dp
}
