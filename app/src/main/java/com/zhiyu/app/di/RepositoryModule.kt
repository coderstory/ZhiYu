package com.zhiyu.app.di

import com.zhiyu.app.data.local.dao.ArticleDao
import com.zhiyu.app.data.local.dao.CategoryDao
import com.zhiyu.app.data.local.dao.QuickNoteDao
import com.zhiyu.app.data.local.dao.TagDao
import com.zhiyu.app.data.repository.ArticleRepository
import org.koin.dsl.module

val repositoryModule = module {
    single {
        ArticleRepository(
            articleDao = get<ArticleDao>(),
            tagDao = get<TagDao>(),
            categoryDao = get<CategoryDao>(),
            quickNoteDao = get<QuickNoteDao>()
        )
    }
}
