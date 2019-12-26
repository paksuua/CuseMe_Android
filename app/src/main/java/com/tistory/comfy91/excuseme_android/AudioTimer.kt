package com.tistory.comfy91.excuseme_android

import android.app.Activity
import java.util.*

class AudioTimer(private val activity: Activity, private val mainThreadFunc : () -> Unit) : TimerTask(){
    var count = 0

    override fun run() {
        count++
        activity.runOnUiThread {
            mainThreadFunc()
        }
    }

    fun start(){
        Timer().apply {
            schedule(this@AudioTimer, 0, 1000)
        }
    }
}