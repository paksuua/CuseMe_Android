package com.tistory.comfy91.excuseme_android.data.answer


import com.google.gson.annotations.SerializedName
import com.tistory.comfy91.excuseme_android.data.Token

data class ResponseLogin(
    @SerializedName("data")
    val data: Token,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)