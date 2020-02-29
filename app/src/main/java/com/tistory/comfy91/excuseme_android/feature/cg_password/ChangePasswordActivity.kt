package com.tistory.comfy91.excuseme_android.feature.cg_password

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.answer.ResUser
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.ServerUserDataRepository
import com.tistory.comfy91.excuseme_android.data.request.BodyChangePw
import com.tistory.comfy91.excuseme_android.logDebug
import com.tistory.comfy91.excuseme_android.toast
import kotlinx.android.synthetic.main.activity_change_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChangePasswordActivity : AppCompatActivity() {
    private var userDataRepository = ServerUserDataRepository()
    private var token = SingletoneToken.getInstance().token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        initUi()
    }


    private fun initUi() {
        tvChangepwNotice.visibility = View.INVISIBLE

        et_password_exist.doOnTextChanged { text1, start, count, after ->
            Log.d("ssss", et_password_new.text.toString())
            if (!text1.isNullOrBlank() && !et_password_new.text.toString().isNullOrBlank() && !et_password_new_confirm.text.toString().isNullOrBlank()) {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_pink)
                button.isEnabled = true

            } else {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_gray)
                tvChangepwNotice.visibility = View.VISIBLE
                button.isEnabled = false
            }
        }
        et_password_new.doOnTextChanged { text2, start, count, after ->
            if (!et_password_exist.text.toString().isNullOrBlank() && !text2.isNullOrBlank() && !et_password_new_confirm.text.toString().isNullOrBlank()) {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_pink)
                tvChangepwNotice.visibility = View.INVISIBLE
                button.isEnabled = true

            } else {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_gray)
                tvChangepwNotice.visibility = View.VISIBLE
                button.isEnabled = false

            }
        }
        et_password_new_confirm.doOnTextChanged { text3, start, count, after ->
            if (!et_password_exist.text.toString().isNullOrBlank() && !et_password_new.text.toString().isNullOrBlank() && !text3.isNullOrBlank()) {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_pink)
                tvChangepwNotice.visibility = View.INVISIBLE
                button.isEnabled = true

            } else {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_gray)
                tvChangepwNotice.visibility = View.VISIBLE
                button.isEnabled = false
            }
        }

        button.setOnClickListener {
            if (et_password_new.text == et_password_new_confirm.text) {
                "비밀번호가 일치하지 않습니다.".toast(this@ChangePasswordActivity)
                return@setOnClickListener
            }
            changePw()
        }

        btnChangepwBack.setOnClickListener { finish() }


    }

    private fun changePw() {
        if (token == null) {
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWR4IjozOSwidXVpZCI6ImYzZDViM2E1LTkwYjYtNDVlMy1hOThhLTEyODE5OWNmZTg1MCIsImlhdCI6MTU3NzkwMTA1MywiZXhwIjoxNTc3OTg3NDUzLCJpc3MiOiJnYW5naGVlIn0.QytUhsXf4bJirRR_zF3wdACiNu9ytwUE4mrPSNLCFLk"
        }

        userDataRepository.changePw(
            token!!,
            BodyChangePw(
                et_password_exist.text.toString()
                , et_password_new.text.toString()
            )
        ).enqueue(object : Callback<ResUser> {
            override fun onFailure(call: Call<ResUser>, t: Throwable) {
                "Fail Request Change Password, message : ${t.message}".logDebug(this@ChangePasswordActivity)
            }

            override fun onResponse(call: Call<ResUser>, response: Response<ResUser>) {
                "code: ${response.code()}, message : ${response.message()}".logDebug(this@ChangePasswordActivity)
                if (response.isSuccessful) {
                    response.body()
                        ?.let {
                            "status : ${it.status}, success : ${it.success}, message : ${it.message}".logDebug(this@ChangePasswordActivity)
                            finish()
                        }
                } else {
                    "response is Not Success = Body is Empty".logDebug(this@ChangePasswordActivity)
                    "비밀번호가 일치하지 않습니다.".logDebug(this@ChangePasswordActivity)
                }
            }

        })

    }
}

