package com.sassnippet.manager

import android.app.Application
import com.sassnippet.manager.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class SasSnippetApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@SasSnippetApp)
        }
    }
}