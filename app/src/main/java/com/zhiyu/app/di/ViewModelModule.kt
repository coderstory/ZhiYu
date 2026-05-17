package com.zhiyu.app.di

import com.zhiyu.app.data.repository.ArticleRepository
import com.zhiyu.app.ui.screens.knowledge.ArticleDetailViewModel
import com.zhiyu.app.ui.screens.knowledge.EditorViewModel
import com.zhiyu.app.ui.screens.knowledge.KnowledgeViewModel
import com.zhiyu.app.ui.screens.profile.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ProfileViewModel(get()) }
    viewModel { KnowledgeViewModel(get<ArticleRepository>()) }
    viewModel { ArticleDetailViewModel(get<ArticleRepository>()) }
    viewModel { EditorViewModel(get<ArticleRepository>()) }
}
