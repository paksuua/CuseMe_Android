package com.tistory.comfy91.excuseme_android

import android.content.Context
import android.content.Intent
import android.util.Log


/**
 * UUID 생성 코드
 * var uniqueID = UUID.randomUUID().toString()
 */

fun String.logDebug(any: Any) {
    Log.d(any::class.java.simpleName, this)
}

fun <T>Context.newStartActivity(toClass: Class<T>){
    val intent = Intent(this, toClass)
    startActivity(intent)
}