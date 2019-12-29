package com.tistory.comfy91.excuseme_android.feature.disabled

import android.os.Parcel
import android.os.Parcelable


data class ItemCard(val imageUrl: String, val title: String): Parcelable{
    companion object{
        @JvmField
        val CREATOR:Parcelable.Creator<ItemCard> = object: Parcelable.Creator<ItemCard>{
            override fun createFromParcel(source: Parcel): ItemCard = ItemCard(source)
            override fun newArray(size: Int): Array<ItemCard?> = arrayOfNulls(size)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(imageUrl)
        dest.writeString(title)
    }

    constructor(source: Parcel): this(source.readString()!!, source.readString()!!)



}




