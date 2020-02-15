package com.tistory.comfy91.excuseme_android.data.request


import com.google.gson.annotations.SerializedName
import com.tistory.comfy91.excuseme_android.data.CardBean

data class BodyChangeAllCards(
    @SerializedName("updateArr")
    val updateArr: List<ChangeAllCards>
)