package com.tistory.comfy91.excuseme_android.feature.download_card

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.feature.addcard.AddCardActivity
import com.tistory.comfy91.excuseme_android.newStartActivity
import kotlinx.android.synthetic.main.activity_download_card.*

class DownloadCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_card)


        uiInit()
    }

    private fun uiInit(){
        btnDownBack.setOnClickListener {
            finish()
        }


    }
}
