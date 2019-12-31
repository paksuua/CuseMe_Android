package com.tistory.comfy91.excuseme_android.data

import com.google.gson.annotations.SerializedName

data class UserBean(
    @SerializedName("uuid")
    val uuid: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("salt")
    val salt: String,

    @SerializedName("phoneNum")
    val phoneNum: String
)