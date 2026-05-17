package com.zhiyu.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(contentEntity = ArticleEntity::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity(tableName = "articles_fts")
data class ArticleFts(
    @ColumnInfo(name = "rowid")
    val rowId: Long,
    val title: String,
    val content: String
)
