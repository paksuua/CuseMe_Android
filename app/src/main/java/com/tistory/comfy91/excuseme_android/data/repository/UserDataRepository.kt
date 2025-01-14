package com.tistory.comfy91.excuseme_android.data.repository


import retrofit2.Call
import com.tistory.comfy91.excuseme_android.data.answer.ResUser
import com.tistory.comfy91.excuseme_android.data.answer.ResponseLogin
import com.tistory.comfy91.excuseme_android.data.request.BodyChangePhoneNum
import com.tistory.comfy91.excuseme_android.data.request.BodyChangePw
import com.tistory.comfy91.excuseme_android.data.request.BodyHelperSignIn
import com.tistory.comfy91.excuseme_android.data.request.BodyStartApp

interface UserDataRepository {
    fun startApp(bodyStartApp: BodyStartApp): Call<ResUser>

    fun helperSignIn(bodyHelperSingIn: BodyHelperSignIn): Call<ResponseLogin>

    fun changePw(token: String,bodyChangePw: BodyChangePw): Call<ResUser>

    fun changePhoneNum(token: String,bodyChangePhoneNum: BodyChangePhoneNum): Call<ResUser>
}