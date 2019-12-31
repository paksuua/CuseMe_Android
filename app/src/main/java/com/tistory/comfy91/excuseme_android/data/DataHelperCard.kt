package com.tistory.comfy91.excuseme_android.data

import java.io.Serializable

data class DataHelperCard(
    val imageUrl: String,
    val title: String,
    var visibility: Boolean,
    val desc: String
): Serializable