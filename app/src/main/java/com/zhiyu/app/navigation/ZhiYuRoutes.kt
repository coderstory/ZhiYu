package com.zhiyu.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ZhiYuRoutes {

    @Serializable
    data object Info : ZhiYuRoutes()

    @Serializable
    data object Knowledge : ZhiYuRoutes()

    @Serializable
    data object Discover : ZhiYuRoutes()

    @Serializable
    data object Profile : ZhiYuRoutes()

    @Serializable
    data object Settings : ZhiYuRoutes()

    @Serializable
    data object About : ZhiYuRoutes()

    // ── Knowledge sub-routes ──────────────────────────────────────────

    @Serializable
    data class ArticleDetail(val articleId: Long) : ZhiYuRoutes()

    @Serializable
    data class ArticleEditor(val articleId: Long = 0L) : ZhiYuRoutes()
}
