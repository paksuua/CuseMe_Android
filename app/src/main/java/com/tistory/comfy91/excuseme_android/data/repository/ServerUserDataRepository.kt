package com.tistory.comfy91.excuseme_android.data.repository

import com.tistory.comfy91.excuseme_android.api.ServerService
import com.tistory.comfy91.excuseme_android.data.answer.ResUser
import com.tistory.comfy91.excuseme_android.data.answer.ResponseLogin
import com.tistory.comfy91.excuseme_android.data.request.BodyChangePhoneNum
import com.tistory.comfy91.excuseme_android.data.request.BodyChangePw
import com.tistory.comfy91.excuseme_android.data.request.BodyHelperSignIn
import com.tistory.comfy91.excuseme_android.data.request.BodyStartApp
import retrofit2.Call

class ServerUserDataRepository:
    UserDataRepository {
    override fun startApp(bodyStartApp: BodyStartApp): Call<ResUser> {
        return ServerService.service.startApp(bodyStartApp)
    }

    override fun helperSignIn(bodyHelperSingIn: BodyHelperSignIn): Call<ResponseLogin> {
        return ServerService.service.helperSignIn(bodyHelperSingIn)
    }

    override fun changePw(token: String, bodyChangePw: BodyChangePw): Call<ResUser> {
        return ServerService.service.changePw(token, bodyChangePw)
    }

    override fun changePhoneNum(
        token: String,
        bodyChangePhoneNum: BodyChangePhoneNum
    ): Call<ResUser> {
        return ServerService.service.changePhoneNum(token, bodyChangePhoneNum)
    }

}