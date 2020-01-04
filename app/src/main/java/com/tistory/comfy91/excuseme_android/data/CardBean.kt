package com.tistory.comfy91.excuseme_android.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CardBean(
    @SerializedName("cardIdx")
    val cardIdx: Int,

    @SerializedName("title")
    var title: String,

    @SerializedName("content")
    var desc: String,

    @SerializedName("image")
    var imageUrl: String,

    @SerializedName("record")
    var audioUrl: String?,

    @SerializedName("count")
    val count: Int,

    @SerializedName("visible")
    var visibility: Boolean,

    @SerializedName("serialNum")
    val serialNum: String,

    @SerializedName("sequence")
    var sequence: Int,

    @SerializedName("userIdx")
    val userIdx: String?
):Serializable