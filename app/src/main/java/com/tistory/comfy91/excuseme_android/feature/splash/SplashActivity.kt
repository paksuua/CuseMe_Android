package com.tistory.comfy91.excuseme_android.feature.splash

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.feature.disabled.DisabledActivity
import com.tistory.comfy91.excuseme_android.newStartActivity

class SplashActivity : AppCompatActivity() {
    val lottieAnimation = findViewById<LottieAnimationView>(R.id.lottieAnimation)
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lottieAnimation.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onAnimationEnd(p0: Animator?) {
                TODO("다음 화면으로 넘어가기 not implemented")
                this@SplashActivity.newStartActivity(DisabledActivity::class.java)
            }

            override fun onAnimationCancel(p0: Animator?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onAnimationStart(p0: Animator?) {
                TODO("uuid 가지고 앱실행 요청 not implemented")
            }
        })
    }
    override fun onBackPressed() {
    }
}
