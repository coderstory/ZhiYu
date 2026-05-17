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
}
