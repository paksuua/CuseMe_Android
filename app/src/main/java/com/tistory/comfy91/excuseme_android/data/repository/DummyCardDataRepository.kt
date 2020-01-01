package com.tistory.comfy91.excuseme_android.data.repository

import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCardDetail
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.server.BodyDeleteCard
import com.tistory.comfy91.excuseme_android.data.server.BodyGetDisabledCard
import okhttp3.MultipartBody
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

    override fun addCard(
        token: String,
        title: String,
        desc : String,
        visibility: Boolean,
        sequence: Int,
        image: MultipartBody.Part,
        record: MultipartBody.Part
    ): Call<ResCards> {
        return Calls.response(
            ResCards(
                200,
                true,
                "Success Add Card",
                listOf()
            )
        )


    }

    override fun editCardDetail(
        token: String,
        cardIdx: String,
        cardBean: CardBean
    ): Call<ResCards> {
        return Calls.response(
                ResCards(
                    200,
                    true,
                    "dummy 카드 다운 성공",
                    listOf()
                )
        )

    }

    override fun deleteCard(token: String, bodyDeleteCard: BodyDeleteCard): Call<ResCards> {
        return Calls.response(
            ResCards(
                200,
                true,
                "dummyy 카드 삭제 성공",
                listOf()
            )
        )

    }

    override fun downCard(token: String, serialNum: String): Call<ResCards> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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