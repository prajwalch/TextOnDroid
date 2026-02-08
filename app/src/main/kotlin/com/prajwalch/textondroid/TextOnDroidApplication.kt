package com.prajwalch.textondroid

import android.app.Application

import com.prajwalch.textondroid.di.appModule
import com.prajwalch.textondroid.di.viewModelModule
import com.prajwalch.textondroid.ui.crash.CrashActivity
import com.prajwalch.textondroid.util.GlobalExceptionHandler

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

import timber.log.Timber

class TextOnDroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(
                context = this,
                activityToLaunch = CrashActivity::class.java,
            )
        )
        
        startKoin {
            androidContext(this@TextOnDroidApplication)
            androidLogger()
            modules(appModule, viewModelModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}