package com.zhiyu.app

import android.app.Application
import com.zhiyu.app.di.appModule
import com.zhiyu.app.di.repositoryModule
import com.zhiyu.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ZhiYuApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ZhiYuApplication)
            modules(appModule, repositoryModule, viewModelModule)
        }
    }
}
