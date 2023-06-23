package com.rahul.kotlin.architecture.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        plantTimber()
    }

    private fun plantTimber() {
        if (Config.environment == Config.Environment.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
