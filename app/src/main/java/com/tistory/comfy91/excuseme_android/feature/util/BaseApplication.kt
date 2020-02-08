package com.tistory.comfy91.excuseme_android.feature.util

import android.app.Application

class BaseApplication : Application() {

    companion object {
        //application class 객체를 static 변수로 선언
        lateinit var app : BaseApplication
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}