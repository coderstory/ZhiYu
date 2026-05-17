package com.zhiyu.app.di

import androidx.room.Room
import com.zhiyu.app.data.local.ZhiYuDatabase
import com.zhiyu.app.data.local.dao.ArticleDao
import com.zhiyu.app.data.local.dao.CategoryDao
import com.zhiyu.app.data.local.dao.QuickNoteDao
import com.zhiyu.app.data.local.dao.TagDao
import com.zhiyu.app.data.preferences.AppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            ZhiYuDatabase::class.java,
            "zhiyu_database"
        ).build()
    }

    single<ArticleDao> { get<ZhiYuDatabase>().articleDao() }
    single<CategoryDao> { get<ZhiYuDatabase>().categoryDao() }
    single<TagDao> { get<ZhiYuDatabase>().tagDao() }
    single<QuickNoteDao> { get<ZhiYuDatabase>().quickNoteDao() }

    single { AppPreferences(get()) }
}
