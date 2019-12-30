package com.tistory.comfy91.excuseme_android.data

import retrofit2.Call
import com.tistory.comfy91.excuseme_android.api.Service
import retrofit2.mock.Calls

class DummyUserDataRepository: UserDataRepository {
    override fun startApp(bodyStartApp: Service.BodyStartApp): Call<ResUser> {
        return Calls.response(
            ResUser(
                true,
                "앱 시작 성공",
                listOf(UserBean("uuid", "password", "salt", "phoneNum"))
            )
        )
    }
}