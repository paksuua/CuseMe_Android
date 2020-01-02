package com.tistory.comfy91.excuseme_android.data

import okhttp3.ResponseBody

data class ResCardDetail(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: CardBean?
)