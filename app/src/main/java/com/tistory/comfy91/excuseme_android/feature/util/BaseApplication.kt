package com.tistory.comfy91.excuseme_android.feature.util

import android.app.Application

class BaseApplication : Application() {

    companion object {
        lateinit var app: BaseApplication
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}