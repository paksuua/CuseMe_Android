package com.tistory.comfy91.excuseme_android.feature.download_card

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.feature.addcard.AddCardActivity
import com.tistory.comfy91.excuseme_android.newStartActivity
import kotlinx.android.synthetic.main.activity_download_card.*
import kotlinx.android.synthetic.main.activity_unlock.*

class DownloadCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_card)


        uiInit()


        edtDownkInputCardNum.doOnTextChanged { text1, start, count, after ->
            if (!text1.isNullOrBlank()) {
                btnDownNewCard.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_pink)

            } else {
                btnDownNewCard.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_gray)
            }
        }

    }

    private fun uiInit(){
        btnDownBack.setOnClickListener {
            finish()
        }


    }
}
