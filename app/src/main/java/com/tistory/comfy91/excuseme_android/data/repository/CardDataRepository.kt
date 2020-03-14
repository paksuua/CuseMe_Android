package com.tistory.comfy91.excuseme_android.data.repository

import com.tistory.comfy91.excuseme_android.data.answer.ResAddCard
import com.tistory.comfy91.excuseme_android.data.answer.ResCardDetail
import com.tistory.comfy91.excuseme_android.data.answer.ResCards
import com.tistory.comfy91.excuseme_android.data.answer.ResDownCard
import com.tistory.comfy91.excuseme_android.data.request.BodyChangeAllCards
import com.tistory.comfy91.excuseme_android.data.request.BodyGetDisabledCard
import com.tistory.comfy91.excuseme_android.data.server.BodyChangeVisibility
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
        record: MultipartBody.Part?
    ): Call<ResAddCard>


    fun editCardDetail(
        token: String,
        cardIdx: String,
        title: RequestBody,
        desc : RequestBody,
        visibility: Boolean,
        image: MultipartBody.Part?,
        record: MultipartBody.Part?
    ): Call<ResDownCard>


    fun deleteCard(token: String, cardIdx: String): Call<ResCards>

    fun downCard(token: String, serialNum: String): Call<ResDownCard>

    fun incCardCount(token: String, cardIdx: String): Call<ResCards>

    fun changeAllCards(token: String, bodyChangeAllCards: BodyChangeAllCards): Call<ResCards>

    fun changeVisibilty(token: String, bodyChangeVisibility: BodyChangeVisibility, cardIdx: String): Call<ResCards>

    // endregion 카드
}