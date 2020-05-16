package com.tistory.comfy91.excuseme_android.data.repository

import com.tistory.comfy91.excuseme_android.api.ServerService
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

class ServerCardDataRepository:
    CardDataRepository{

    /**
     * cardIdx,
     * visbile,
     * sequence
     */
    override fun changeAllCards(
        token: String,
        bodyChangeAllCards: BodyChangeAllCards
    ): Call<ResCards> {
        return ServerService.service.changeAllCards(token, bodyChangeAllCards)
    }

    override fun changeVisibilty(
        token: String,
        bodyChangeVisibility: BodyChangeVisibility,
        cardIdx: String
    ): Call<ResCards> {
        return ServerService.service.changeVisibility(token, bodyChangeVisibility, cardIdx)
    }

    override fun getAllCards(token: String): Call<ResCards> {
        return ServerService.service.getAllCards(token)
    }

    override fun getCardDetail(token: String, cardIdx: String): Call<ResCardDetail> {
        return ServerService.service.getCardDetail(token, cardIdx)
    }

    override fun getDisabledCards(uuid: BodyGetDisabledCard): Call<ResCards> {
        return ServerService.service.getDisabledCards(uuid)
    }

    override fun addCard(
        token: String,
        title: RequestBody,
        desc: RequestBody,
        visibility: RequestBody,
        image: MultipartBody.Part,
        record: MultipartBody.Part?
    ): Call<ResAddCard> {
        return ServerService.service.addCard(token, image, record, title, desc, visibility)
    }

    override fun editCardDetail(
        token: String,
        cardIdx: String,
        title: RequestBody,
        desc : RequestBody,
        visibility: Boolean,
        image: MultipartBody.Part?,
        record: MultipartBody.Part?
    ): Call<ResDownCard> {
        return ServerService.service.editCardDetail(token, cardIdx, title, desc, visibility, image, record)
    }

    override fun deleteCard(token: String, cardIdx: String): Call<ResCards> {
        return ServerService.service.deleteCard(token, cardIdx)
    }

    override fun downCard(token: String, serialNum: String): Call<ResDownCard> {
        return ServerService.service.downCard(token, serialNum)
    }

    override fun incCardCount(token: String, cardIdx: String): Call<ResCards> {
        return ServerService.service.incCardCount(token, cardIdx)
    }

}