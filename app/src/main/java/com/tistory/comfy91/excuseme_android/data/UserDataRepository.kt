package com.tistory.comfy91.excuseme_android.data


import retrofit2.Call
import com.tistory.comfy91.excuseme_android.api.Service

interface UserDataRepository{
    fun startApp(bodyStartApp: Service.BodyStartApp): Call<ResUser>
}