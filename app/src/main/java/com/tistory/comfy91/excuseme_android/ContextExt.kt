package com.tistory.comfy91.excuseme_android

import android.content.Context
import android.widget.Toast

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun String.toast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}