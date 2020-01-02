package com.tistory.comfy91.excuseme_android.feature.download_card

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.ResDownCard
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.DummyCardDataRepository
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.feature.addcard.AddCardActivity
import com.tistory.comfy91.excuseme_android.feature.detailcard.DetailCardActivity
import com.tistory.comfy91.excuseme_android.feature.helper.HelperActivity
import com.tistory.comfy91.excuseme_android.logDebug
import com.tistory.comfy91.excuseme_android.newStartActivity
import kotlinx.android.synthetic.main.activity_download_card.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.activity_unlock.*

class DownloadCardActivity : AppCompatActivity() {

    private val cardDataRepository= ServerCardDataRepository()
    private var token = SingletoneToken.getInstance().token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_card)

        uiInit()

    }

    private fun uiInit(){
        edtDownkInputCardNum.doOnTextChanged { text1, start, count, after ->
            if (!text1.isNullOrBlank()) {
                btnDownNewCard.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_pink)

            } else {
                btnDownNewCard.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_gray)
            }
        }

        btnDownBack.setOnClickListener {finish()}

        btnDownNewCard.setOnClickListener {
            if(token == null){
                token = "token"
            }
            edtDownkInputCardNum.text.toString()
                ?.let{
                    cardDataRepository
                        .downCard(token!!,it)
                        .enqueue( object: Callback<ResDownCard> {
                            override fun onFailure(call: Call<ResDownCard>, t: Throwable) {
                                "Fail to Down Card, message : ${t.message}".logDebug(this@DownloadCardActivity)
                            }

                            override fun onResponse(
                                call: Call<ResDownCard>,
                                response: Response<ResDownCard>
                            ) {
                                if(response.isSuccessful){
                                    response.body()
                                        ?.let {res ->
                                            "Down Card is Success, status : ${res.status}, success: ${res.success}, message : ${res.message}, data: ${res.data}".logDebug(this@DownloadCardActivity)
                                            if(res.success){
                                                val card = res.data!!
                                                val intent = Intent(this@DownloadCardActivity, HelperActivity::class.java)
                                                intent.putExtra("DOWN_CARD", card)
                                                startActivity(intent)
                                                this@DownloadCardActivity.finish()
                                            }
                                        }
                                }
                                else{
                                    "Down Card is not success,  message : ${response.message()}, code : ${response.code()} "
                                }
                            }
                        })

                }

        }


    }
}
