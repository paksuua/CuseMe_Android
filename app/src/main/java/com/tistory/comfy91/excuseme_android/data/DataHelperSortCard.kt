package com.tistory.comfy91.excuseme_android.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


data class DataHelperSortCard(
        val imageUrl: String,
        val title: String,
        var visibility: Boolean,
        val count: Int
):Serializable