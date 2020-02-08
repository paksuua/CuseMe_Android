package com.tistory.comfy91.excuseme_android.data.repository


import retrofit2.Call
import com.tistory.comfy91.excuseme_android.api.Service
import com.tistory.comfy91.excuseme_android.data.ResUser
import com.tistory.comfy91.excuseme_android.data.ResponseLogin
import com.tistory.comfy91.excuseme_android.data.server.BodyChangePhoneNum
import com.tistory.comfy91.excuseme_android.data.server.BodyChangePw
import com.tistory.comfy91.excuseme_android.data.server.BodyHelperSignIn
import com.tistory.comfy91.excuseme_android.data.server.BodyStartApp

interface UserDataRepository {
    fun startApp(bodyStartApp: BodyStartApp): Call<ResUser>

    fun helperSignIn(bodyHelperSingIn: BodyHelperSignIn): Call<ResponseLogin>

    fun changePw(token: String,bodyChangePw: BodyChangePw): Call<ResUser>

    fun changePhoneNum(token: String,bodyChangePhoneNum: BodyChangePhoneNum): Call<ResUser>
}