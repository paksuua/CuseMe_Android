package com.tistory.comfy91.excuseme_android.feature.splash

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.api.Service
import com.tistory.comfy91.excuseme_android.data.DummyUserDataRepository
import com.tistory.comfy91.excuseme_android.data.ResUser
import com.tistory.comfy91.excuseme_android.feature.disabled.DisabledActivity
import com.tistory.comfy91.excuseme_android.feature.login.Login
import com.tistory.comfy91.excuseme_android.newStartActivity
import javax.security.auth.callback.Callback

class SplashActivity : AppCompatActivity() {
    val lottieAnimation = findViewById<LottieAnimationView>(R.id.lottieAnimation)
    private val userDataRespository = DummyUserDataRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initUi()
    }

    private fun initUi(){
        lottieAnimation.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onAnimationEnd(p0: Animator?) {
                this@SplashActivity.newStartActivity(DisabledActivity::class.java)
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {
                requestStartApp()


            }
        })
    }
    private fun requestStartApp(){
//        val uuid = Login.getUUID(this@SplashActivity)
//        requestServer(uuid)
    }


    private fun requestServer(uuid: String){
        Service.BodyStartApp(uuid)
            .let{
                userDataRespository.startApp(it)


            }
    }
    override fun onBackPressed() {
    }
}
