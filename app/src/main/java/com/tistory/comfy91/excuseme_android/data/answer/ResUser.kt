package com.tistory.comfy91.excuseme_android.data.answer

import com.tistory.comfy91.excuseme_android.data.UserBean

data class ResUser(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: List<UserBean>?
)