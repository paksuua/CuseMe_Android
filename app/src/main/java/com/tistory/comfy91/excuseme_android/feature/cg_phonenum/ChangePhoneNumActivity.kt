package com.tistory.comfy91.excuseme_android.feature.cg_phonenum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.tistory.comfy91.excuseme_android.R
import kotlinx.android.synthetic.main.activity_change_phone_num.*
import kotlinx.android.synthetic.main.activity_download_card.*

class ChangePhoneNumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_phone_num)


        edtPhoneNumpw.doOnTextChanged { text1, start, count, after ->
            if (!text1.isNullOrBlank()) {
                btnChangePhoneNum.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_pink)

            } else {
                btnChangePhoneNum.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_gray)
            }
        }
    }
}
