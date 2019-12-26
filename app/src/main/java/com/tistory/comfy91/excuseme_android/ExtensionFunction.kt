package com.tistory.comfy91.excuseme_android

import android.util.Log

fun String.logDebug(any: Any) {
    Log.d(any::class.java.simpleName, this)
}