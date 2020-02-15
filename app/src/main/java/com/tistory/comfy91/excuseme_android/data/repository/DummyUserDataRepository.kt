package com.tistory.comfy91.excuseme_android.data.repository

import retrofit2.Call
import com.tistory.comfy91.excuseme_android.data.*
import com.tistory.comfy91.excuseme_android.data.answer.ResUser
import com.tistory.comfy91.excuseme_android.data.answer.ResponseLogin
import com.tistory.comfy91.excuseme_android.data.request.BodyChangePhoneNum
import com.tistory.comfy91.excuseme_android.data.request.BodyHelperSignIn
import com.tistory.comfy91.excuseme_android.data.request.BodyStartApp
import retrofit2.mock.Calls
import com.tistory.comfy91.excuseme_android.data.request.BodyChangePw as BodyChangePw1

class DummyUserDataRepository :
    UserDataRepository {
    override fun startApp(bodyStartApp: BodyStartApp): Call<ResUser> {
        return Calls.response(
            ResUser(
                200,
                true,
                "Success",
                listOf(
                    UserBean(
                        "uuid",
                        "string",
                        "salt",
                        "phoneNum",
                        Token("dddd")
                    )
                )
            )
        )
    }

    override fun helperSignIn(bodyHelperSingIn: BodyHelperSignIn): Call<ResponseLogin> {
        return Calls.response(
            ResponseLogin(
                Token("data"),
                "message",
                200,
                true


            )
        )
    }

    override fun changePw(token: String, bodyChangePw: BodyChangePw1): Call<ResUser> {
        return Calls.response(
            ResUser(
                200,
                true,
                "Success",
                listOf()
            )
        )
    }

    override fun changePhoneNum(
        token: String,
        bodyChangePhoneNum: BodyChangePhoneNum
    ): Call<ResUser> {
        return Calls.response(
            ResUser(
                200,
                true,
                "Success",
                listOf()
            )
        )
    }
}