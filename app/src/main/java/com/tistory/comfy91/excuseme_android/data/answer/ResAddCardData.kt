package com.tistory.comfy91.excuseme_android.data.answer

import com.google.gson.annotations.SerializedName

data class ResAddCardData(
    @SerializedName("cardIdx")
    val cardIdx: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val desc: String,

    @SerializedName("image")
    val imageUrl: String,

    @SerializedName("record")
    val audioUrl: String,

    @SerializedName("serialNum")
    val serialNum: String
)