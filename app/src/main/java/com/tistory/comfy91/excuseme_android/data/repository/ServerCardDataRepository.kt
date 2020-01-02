package com.tistory.comfy91.excuseme_android.data.repository

import com.tistory.comfy91.excuseme_android.api.ServerService
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCardDetail
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.ResDownCard
import com.tistory.comfy91.excuseme_android.data.server.BodyDeleteCard
import com.tistory.comfy91.excuseme_android.data.server.BodyGetAllCards
import com.tistory.comfy91.excuseme_android.data.server.BodyGetDisabledCard
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class ServerCardDataRepository:
    CardDataRepository{


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
        visibility: Boolean,
        image: MultipartBody.Part,
        record: MultipartBody.Part
    ): Call<ResCards> {
        return ServerService.service.addCard(token, title, desc, visibility, image, record)
    }

    override fun editCardDetail(
        token: String,
        cardIdx: String,
        title: RequestBody,
        desc : RequestBody,
        visibility: Boolean,
        image: MultipartBody.Part,
        record: MultipartBody.Part
    ): Call<ResCards> {
        return ServerService.service.editCardDetail(token, cardIdx, title, desc, visibility, image, record)
    }

    override fun deleteCard(token: String, bodyDeleteCard: BodyDeleteCard): Call<ResCards> {
        return ServerService.service.deleteCard(token, bodyDeleteCard)
    }

    override fun downCard(token: String, serialNum: String): Call<ResDownCard> {
        return ServerService.service.downCard(token, serialNum)
    }

    override fun incCardCount(token: String, cardIdx: String): Call<ResCards> {
        return ServerService.service.incCardCount(token, cardIdx)
    }

}