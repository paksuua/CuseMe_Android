package com.tistory.comfy91.excuseme_android.data.answer

import com.tistory.comfy91.excuseme_android.data.CardBean

data class ResDownCard(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: CardBean?
)

