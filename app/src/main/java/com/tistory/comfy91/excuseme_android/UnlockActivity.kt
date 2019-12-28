package com.tistory.comfy91.excuseme_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_download_card.*
import kotlinx.android.synthetic.main.activity_unlock.*

class UnlockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlock)
        
        initUi()
    }

    private fun initUi(){
        btnUnlockBack.setOnSingleClickListener {
            finish()
        }

        btnUnlockGoHelper.setOnSingleClickListener{
            //todo
            // 다음으로 갈 액티비티 지정
            //this.newStartActivity()
        }
    }
}
