package com.tistory.comfy91.excuseme_android.feature.cg_password

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.tistory.comfy91.excuseme_android.R
import kotlinx.android.synthetic.main.activity_change_password.*


class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.tistory.comfy91.excuseme_android.R.layout.activity_change_password)

        tvChangepwNotice.setVisibility(View.INVISIBLE)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        et_password_exist.doOnTextChanged { text1, start, count, after ->
            Log.d("ssss",et_password_new.text.toString())
            if (!text1.isNullOrBlank() && !et_password_new.text.toString().isNullOrBlank() && !et_password_new_confirm.text.toString().isNullOrBlank()) {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_pink)

            } else {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_gray)
                tvChangepwNotice.setVisibility(View.VISIBLE)
            }
        }
        et_password_new.doOnTextChanged { text2, start, count, after ->
            if (!et_password_exist.text.toString().isNullOrBlank() && !text2.isNullOrBlank() && !et_password_new_confirm.text.toString().isNullOrBlank()) {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_pink)
                tvChangepwNotice.setVisibility(View.INVISIBLE)

            } else {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_gray)
                tvChangepwNotice.setVisibility(View.VISIBLE)

            }
        }
        et_password_new_confirm.doOnTextChanged { text3, start, count, after ->
            if (!et_password_exist.text.toString().isNullOrBlank() && !et_password_new.text.toString().isNullOrBlank() && !text3.isNullOrBlank()) {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_pink)
                tvChangepwNotice.setVisibility(View.INVISIBLE)

            } else {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_gray)
                tvChangepwNotice.setVisibility(View.VISIBLE)

            }
        }
    }
}

