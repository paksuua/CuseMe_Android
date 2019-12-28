package com.tistory.comfy91.excuseme_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail_card.*

class DetailCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_card)

        initUi()
    }

    private fun initUi(){
        btnDetailBack.setOnSingleClickListener { finish() }
        btnDetailDelete.setOnSingleClickListener{
            //todo
        }



    }
}
