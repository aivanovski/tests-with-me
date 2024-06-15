package com.github.aivanovski.testwithme.android

import androidx.multidex.MultiDexApplication
import com.github.aivanovski.testwithme.android.di.AndroidAppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(AndroidAppModule.module)
        }
    }
}