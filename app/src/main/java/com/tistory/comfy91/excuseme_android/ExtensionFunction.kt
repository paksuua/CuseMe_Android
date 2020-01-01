package com.tistory.comfy91.excuseme_android

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat


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

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun String.toast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}
fun Context.isPermissionNotGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
}

fun Context.startSettingActivity() {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    })
}




