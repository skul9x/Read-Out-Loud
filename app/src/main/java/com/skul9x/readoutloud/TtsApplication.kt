package com.skul9x.readoutloud

import android.app.Application
import android.content.Context

class TtsApplication : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}

