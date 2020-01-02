package com.tistory.comfy91.excuseme_android.data.repository

import com.tistory.comfy91.excuseme_android.data.*
import com.tistory.comfy91.excuseme_android.data.server.BodyGetDisabledCard
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

interface CardDataRepository {
    fun getAllCards(token: String): Call<ResCards>

    fun getCardDetail(token: String, cardIdx: String): Call<ResCardDetail>

    fun getDisabledCards(uuid: BodyGetDisabledCard): Call<ResCards>

    fun addCard(
        token: String,
        title: RequestBody,
        desc : RequestBody,
        visibility: Boolean,
        image: MultipartBody.Part,
        record: MultipartBody.Part
    ): Call<ResCards>


    fun editCardDetail(
        token: String,
        cardIdx: String,
        title: RequestBody,
        desc : RequestBody,
        visibility: Boolean,
        image: MultipartBody.Part,
        record: MultipartBody.Part
    ): Call<ResCards>

    fun deleteCard(token: String, cardIdx: String): Call<ResCards>

    fun downCard(token: String, serialNum: String): Call<ResDownCard>

    fun incCardCount(token: String, cardIdx: String): Call<ResCards>

    // endregion 카드
}