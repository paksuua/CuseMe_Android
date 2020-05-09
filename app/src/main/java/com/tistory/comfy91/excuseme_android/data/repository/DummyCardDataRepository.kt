package com.tistory.comfy91.excuseme_android.data.repository

import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.answer.*
import com.tistory.comfy91.excuseme_android.data.request.BodyChangeAllCards
import com.tistory.comfy91.excuseme_android.data.request.BodyGetDisabledCard
import com.tistory.comfy91.excuseme_android.data.server.BodyChangeVisibility
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.mock.Calls

//        @SerializedName("cardIdx")
//        val cardIdx: Int,
//
//        @SerializedName("title")
//        val title: String,
//
//        @SerializedName("content")
//        val desc: String,
//
//        @SerializedName("image")
//        val imageUrl: String,
//
//        @SerializedName("record")
//        val audioUrl: String,
//
//        @SerializedName("count")
//        val count: Int,
//
//        @SerializedName("visible")
//        val visibility: Int,
//
//        @SerializedName("serialNum")
//        val serialNum: String,
//
//        @SerializedName("sequence")
//        val sequence: Int,
//
//        @SerializedName("userIdx")
//        val userIdx: String?

class DummyCardDataRepository :
    CardDataRepository {
    override fun changeAllCards(
        token: String,
        bodyChangeAllCards: BodyChangeAllCards
    ): Call<ResCards> {
        return Calls.response(
            ResCards(
                200,
                true,
                "카드 수정 성공",
                listOf()
            )
        )
    }

    override fun changeVisibilty(
        token: String,
        bodyChangeVisibility: BodyChangeVisibility,
        cardIdx: String
    ): Call<ResCards> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return Calls.response(
            ResCards(
                status = 200,
                success = true,
                message = "dummy card data",
                data = listOf(
                    CardBean(
                        0,
                        "first card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""
                    ),
                    CardBean(
                        0,
                        "second card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""

                    ),
                    CardBean(
                        0,
                        "third card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""
                    ),
                    CardBean(
                        0,
                        "fourth card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""
                    )
                )
            )
        )
    }

    override fun addCard(
        token: String,
        title: RequestBody,
        desc: RequestBody,
        visibility: RequestBody,
        image: MultipartBody.Part,
        record: MultipartBody.Part?
    ): Call<ResAddCard> {
        return Calls.response(
            ResAddCard(
                200,
                true,
                "카드 작성 성공",
                ResAddCardData(
                    "0",
                    "first card",
                    "desc",
                    "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                    "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                    "dummyserailNum"
                )
            )

        )
    }

    override fun getAllCards(token: String): Call<ResCards> {
        return Calls.response(
            ResCards(
                200,
                true,
                "Success Get All Dummy Cards",
                listOf(
                    CardBean(
                        0,
                        "first card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""
                    ),
                    CardBean(
                        0,
                        "second card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""

                    ),
                    CardBean(
                        0,
                        "third card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""
                    ),
                    CardBean(
                        0,
                        "fourth card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""
                    )
                )
            )
        )

    }

    override fun getCardDetail(token: String, cardIdx: String): Call<ResCardDetail> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDisabledCards(uuid: BodyGetDisabledCard): Call<ResCards> {
        return Calls.response(
            ResCards(
                200,
                true,
                "Dummy Data",
                listOf(
                    CardBean(
                        0,
                        "first card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""
                    ),
                    CardBean(
                        0,
                        "first card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""

                    ),
                    CardBean(
                        0,
                        "first card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""
                    ),
                    CardBean(
                        0,
                        "first card",
                        "desc",
                        "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                        0,
                        false,
                        "serialNum",
                        0,
                        ""
                    )
                )


            )

        )
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
        return Calls.response(
            ResDownCard(
                200,
                true,
                "dummy 카드 다운 성공",
                CardBean(
                    0,
                    "first card",
                    "desc",
                    "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                    "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                    0,
                    false,
                    "serialNum",
                    0,
                    ""
                )
            )
        )

    }

    override fun deleteCard(token: String, cardIdx:String): Call<ResCards> {
        return Calls.response(
            ResCards(
                200,
                true,
                "dummyy 카드 삭제 성공",
                listOf()
            )
        )

    }

    override fun downCard(token: String, serialNum: String):  Call<ResDownCard> {
        return Calls.response(
            ResDownCard(
                200,
                true,
                "dummy 카드 다운 성공",
                CardBean(
                    3,
                    "장난감",
                    "장난감을 가지고 놀고싶어요. 자동차 모양 장난감을 좋아한답니다.",
                    "https://s3sopt25.s3.ap-northeast-2.amazonaws.com/1577892774215.png",
                    "https://s3sopt25.s3.ap-northeast-2.amazonaws.com/1577892774217.MP3",
                    0,
                    false,
                    "h2kpa5zvkh",
                    2,
                    ""
                )
            )
        )
    }

    override fun incCardCount(token: String, cardIdx: String): Call<ResCards> {
        return Calls.response(
            ResCards(
                200,
                true,
                "dummy 카드 다운 성공",
                listOf()
            )
        )

    }


}