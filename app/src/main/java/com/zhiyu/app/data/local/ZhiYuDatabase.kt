package com.zhiyu.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhiyu.app.data.local.converter.Converters
import com.zhiyu.app.data.local.dao.ArticleDao
import com.zhiyu.app.data.local.dao.CategoryDao
import com.zhiyu.app.data.local.dao.QuickNoteDao
import com.zhiyu.app.data.local.dao.TagDao
import com.zhiyu.app.data.local.entity.ArticleEntity
import com.zhiyu.app.data.local.entity.ArticleFts
import com.zhiyu.app.data.local.entity.ArticleTagCrossRef
import com.zhiyu.app.data.local.entity.CategoryEntity
import com.zhiyu.app.data.local.entity.QuickNoteEntity
import com.zhiyu.app.data.local.entity.TagEntity

@Database(
    entities = [
        ArticleEntity::class,
        CategoryEntity::class,
        TagEntity::class,
        ArticleTagCrossRef::class,
        QuickNoteEntity::class,
        ArticleFts::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ZhiYuDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    abstract fun categoryDao(): CategoryDao

    abstract fun tagDao(): TagDao

    abstract fun quickNoteDao(): QuickNoteDao
}
