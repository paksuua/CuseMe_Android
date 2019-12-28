package com.tistory.comfy91.excuseme_android

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.view.View


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

fun View.setOnSingleClickListener(debounceTime: Long = 600L, action: ()->Unit){
    this.setOnClickListener (object: View.OnClickListener{
        private var lastClickTime: Long = 0

        override fun onClick(p0: View?) {
            if((SystemClock.elapsedRealtime() - lastClickTime) < debounceTime){
                Log.d("Single Click", "연속 클릭 발생")
                return
            }
            else {
                action()
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }
    })
}
