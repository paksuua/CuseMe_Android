package com.tistory.comfy91.excuseme_android.feature.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.feature.cg_password.ChangePasswordActivity
import com.tistory.comfy91.excuseme_android.feature.cg_phonenum.ChangePhoneNumActivity
import com.tistory.comfy91.excuseme_android.newStartActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        initUi()
    }

    private fun initUi(){
        btnSettingBack.setOnClickListener { finish() }
        btnSettingCgpw.setOnClickListener{this.newStartActivity(ChangePasswordActivity::class.java)}
//        btnSettingCgPhoneNum.setOnClickListener { this.newStartActivity(ChangePhoneNumActivity::class.java) }
    }
}

