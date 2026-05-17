package com.zhiyu.app.ui.screens.discover

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector

data class ToolEntry(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val description: String
)

object ToolsViewModel {

    val tools: List<ToolEntry> = listOf(
        ToolEntry(
            id = "calendar",
            name = "日历",
            icon = Icons.Default.DateRange,
            description = "简单月历查看"
        ),
        ToolEntry(
            id = "calculator",
            name = "计算器",
            icon = Icons.Default.Calculate,
            description = "基础四则运算"
        ),
        ToolEntry(
            id = "weather",
            name = "天气",
            icon = Icons.Default.Cloud,
            description = "天气查询（占位）"
        )
    )
}
