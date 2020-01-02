package com.tistory.comfy91.excuseme_android.data


import com.google.gson.annotations.SerializedName
import java.io.File

data class Data(
    @SerializedName("cardIdx")
    val cardIdx: Int,
    @SerializedName("content")
    val content: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("record")
    val record: File,
    @SerializedName("sequence")
    val sequence: Int,
    @SerializedName("serialNum")
    val serialNum: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("visible")
    val visible: Boolean
)