package com.tistory.comfy91.excuseme_android.data

data class ResUser(
    val success: Boolean,
    val message: String,
    val data: List<UserBean>?
)