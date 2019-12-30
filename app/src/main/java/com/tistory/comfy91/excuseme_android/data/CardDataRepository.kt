package com.tistory.comfy91.excuseme_android.data

import retrofit2.Call

interface CardDataRepository{
    fun getAllCards(token: String): Call<ResCards>
}