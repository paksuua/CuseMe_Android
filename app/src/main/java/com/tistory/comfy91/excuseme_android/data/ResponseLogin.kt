package com.tistory.comfy91.excuseme_android.data


import com.google.gson.annotations.SerializedName

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