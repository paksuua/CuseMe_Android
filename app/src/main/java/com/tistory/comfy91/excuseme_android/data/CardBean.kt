package com.tistory.comfy91.excuseme_android.data

import com.google.gson.annotations.SerializedName

data class CardBean(
    @SerializedName("cardIdx")
    val cardIdx: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val desc: String,

    @SerializedName("image")
    val imageUrl: String,

    @SerializedName("record")
    val audioUrl: String,

    @SerializedName("count")
    val count: Int,

    @SerializedName("visible")
    val visibility: Int,

    @SerializedName("serialNum")
    val serialNum: String,

    @SerializedName("sequence")
    val sequence: Int,

    @SerializedName("userIdx")
    val userIdx: String?
)