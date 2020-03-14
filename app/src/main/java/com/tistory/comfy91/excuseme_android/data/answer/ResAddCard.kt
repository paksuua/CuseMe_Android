package com.tistory.comfy91.excuseme_android.data.answer

import com.google.gson.annotations.SerializedName

data class ResAddCard(
    @SerializedName("status")
    val status: Int,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ResAddCardData?
)