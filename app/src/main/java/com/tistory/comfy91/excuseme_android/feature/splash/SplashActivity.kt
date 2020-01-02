package com.tistory.comfy91.excuseme_android.feature.splash

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.airbnb.lottie.LottieAnimationView
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.api.Service
import com.tistory.comfy91.excuseme_android.data.repository.DummyUserDataRepository
import com.tistory.comfy91.excuseme_android.data.ResUser
import com.tistory.comfy91.excuseme_android.data.repository.ServerUserDataRepository
import com.tistory.comfy91.excuseme_android.data.server.BodyStartApp
import com.tistory.comfy91.excuseme_android.feature.disabled.DisabledActivity
import com.tistory.comfy91.excuseme_android.feature.login.Login
import com.tistory.comfy91.excuseme_android.logDebug
import com.tistory.comfy91.excuseme_android.newStartActivity
import kotlinx.android.synthetic.main.activity_splash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    private val userDataRespository = ServerUserDataRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initUi()
    }

    private fun initUi() {
        lottieAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {this@SplashActivity.newStartActivity(DisabledActivity::class.java)}

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {requestStartApp()}
        })
    }

    private fun requestStartApp() {
        Login.getUUID(this@SplashActivity)
            .let {
                "(uuid : ${it})".logDebug(this@SplashActivity)
                requestServer(it)
            }

    }

    private fun requestServer(uuid: String) {
        BodyStartApp(uuid)
            .let {
                userDataRespository.startApp(it)
                    .enqueue(object : Callback<ResUser> {
                        override fun onFailure(call: Call<ResUser>, t: Throwable) {
                            "Fail Start App message : ${t.message}"
                        }

                        override fun onResponse(call: Call<ResUser>, response: Response<ResUser>) {
                            if (response.isSuccessful) {
                                response.body()
                                    ?.let { resUser ->
                                        if (resUser.success) {
                                            this@SplashActivity.newStartActivity(DisabledActivity::class.java)
                                            this@SplashActivity.finish()
                                        } else {
                                            "${resUser.message}".logDebug(SplashActivity::class.java)
                                        }
                                    }
                            } else {
                                "Get Response Start App but Response is Fail".logDebug(SplashActivity::class.java)
                            }

                        }

                    })

            }
    }

    override fun onBackPressed() {
    }
}
