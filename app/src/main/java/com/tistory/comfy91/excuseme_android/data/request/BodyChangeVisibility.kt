package com.tistory.comfy91.excuseme_android.data.server

import com.google.gson.annotations.SerializedName

data class BodyChangeVisibility (
    @SerializedName("isVisible")
    val isVisible: Boolean
)