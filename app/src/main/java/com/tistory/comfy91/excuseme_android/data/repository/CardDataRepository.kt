package com.tistory.comfy91.excuseme_android.data.repository

import com.tistory.comfy91.excuseme_android.api.Service
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCardDetail
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.ResDownCard
import com.tistory.comfy91.excuseme_android.data.server.BodyDeleteCard
import com.tistory.comfy91.excuseme_android.data.server.BodyGetDisabledCard
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Part
import retrofit2.http.Path

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


    fun deleteCard(token: String, bodyDeleteCard: BodyDeleteCard): Call<ResCards>

    fun downCard(token: String, serialNum: String): Call<ResDownCard>

    fun incCardCount(token: String, cardIdx: String): Call<ResCards>

    // endregion 카드
}