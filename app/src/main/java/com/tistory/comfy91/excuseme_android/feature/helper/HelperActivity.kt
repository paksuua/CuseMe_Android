package com.tistory.comfy91.excuseme_android.feature.helper

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.service.autofill.Validators.not
import retrofit2.Call
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.DummyCardDataRepository
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.feature.addcard.AddCardActivity
import com.tistory.comfy91.excuseme_android.feature.detailcard.DetailCardActivity
import com.tistory.comfy91.excuseme_android.feature.download_card.DownloadCardActivity
import com.tistory.comfy91.excuseme_android.logDebug
import com.tistory.comfy91.excuseme_android.newStartActivity
import kotlinx.android.synthetic.main.activity_helper.*
import retrofit2.Callback
import retrofit2.Response

class HelperActivity : AppCompatActivity() {

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private lateinit var rotate_forward: Animation
    lateinit var rotate_backward: Animation
    private val transction = supportFragmentManager.beginTransaction()

    private lateinit var dialogBuilder: AlertDialog.Builder

    var fromServerData: ArrayList<CardBean> = arrayListOf()

    private val cardDataRepository = ServerCardDataRepository()
    private lateinit var helperFragment: HelperFragment
    private var isOpen = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper)

        fab_open = AnimationUtils.loadAnimation(applicationContext,
            R.anim.fab_open
        )
        fab_close = AnimationUtils.loadAnimation(applicationContext,
            R.anim.fab_close
        )
        rotate_forward = AnimationUtils.loadAnimation(applicationContext,
            R.anim.rotate_forward
        )
        rotate_backward = AnimationUtils.loadAnimation(applicationContext,
            R.anim.rotate_backward
        )
        initUI()
    }



    override fun onResume() {
        super.onResume()
        initData()
    }


    private fun requestCardsData(token: String){
        cardDataRepository
            .getAllCards(token)
            .enqueue(object: Callback<ResCards> {
                override fun onFailure(call: Call<ResCards>, t: Throwable) {
                    "Fail to Get All Card Data message:${t.message}".logDebug(this@HelperActivity)
                }

                override fun onResponse(
                    call: Call<ResCards>,
                    response: Response<ResCards>
                ) {
                    if(response.isSuccessful){
                        response.body()!!.let{res->
                            "success: ${res.success} status: ${res.status}, data: ${res.data}, message: ${res.message}".logDebug(this@HelperActivity)

                            when(res.success){
                                true->{
                                    fromServerData.clear()
                                    fromServerData.addAll(res.data!!)
                                    helperFragment.helperAdapter.notifyDataSetChanged()
                                }
                                false ->{"Get All Card Data  Response is Not Sucess".logDebug(this@HelperActivity)}
                            }
                        }
                    }
                    else{
                        response.body()?.let{ it ->
                            "success: ${it.success} status: ${it.status}, data: ${it.data}, message: ${it.message}".logDebug(this@HelperActivity)
                        }
                        "resonser is not success".logDebug(this@HelperActivity)
                    }
                }

            })
    }

    private fun initData(){
//        SingletoneToken.getInstance().token
//            ?.let {  }

        //todo("삭제하고 위로 수정")
        val token = SingletoneToken.getInstance()
        token.token = "token"
        token.token?.let{token -> requestCardsData(token)}


    }
    private fun initUI(){

        btnHelperAddCard.setOnClickListener {
            if (!isOpen) {
                backHelperBlur.isVisible=true
                btnHelperDownCard.startAnimation(fab_open)
                btnHelperNewCard.startAnimation(fab_open)
                btnHelperAddCard.startAnimation(rotate_forward)
                btnHelperDownCard.isVisible = true
                btnHelperNewCard.isVisible = true
                btnHelperDownCard.isClickable = true
                btnHelperNewCard.isClickable = true
                isOpen = true
            } else {
                backHelperBlur.isVisible=false
                btnHelperDownCard.startAnimation(fab_close)
                btnHelperNewCard.startAnimation(fab_close)
                btnHelperAddCard.startAnimation(rotate_forward)
                btnHelperDownCard.isVisible = false
                btnHelperNewCard.isVisible = false
                isOpen = false
            }
        }

        // 카드 다운로드뷰로 이동
        if(isOpen){
            btnHelperDownCard.setOnClickListener{
                val intent = Intent(this, DownloadCardActivity::class.java)
                startActivity(intent)
            }

            // 카드생성 뷰로 이동
            btnHelperNewCard.setOnClickListener{
                val intent = Intent(this, AddCardActivity::class.java)
                startActivity(intent)
            }
        }

        btnHelperDownCard.setOnClickListener {
            val intent = Intent(this, DownloadCardActivity::class.java)
            startActivity(intent)
        }
        btnHelperNewCard.setOnClickListener {
            Toast.makeText(applicationContext, "Button NewCard Clicked", Toast.LENGTH_LONG)
                .show()
            this.newStartActivity(AddCardActivity::class.java)
        }

        // 홈미리보기 프래그먼트로 전환
        btnHelperGoDisabled.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameHelper, helperFragment)
                .commit()
        }

        // 카드관리 프래그먼트로 전환
        btnHelperAllCard.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameHelper,
                    SelectSortFragment()
                )
                .commit()
        }

        helperFragment = HelperFragment.newInstance(fromServerData)
        transction.add(
            R.id.frameHelper,
            helperFragment
        )
        transction.commit()
    }


    fun BottomBarChange(bottom_flag: Boolean){
        if(bottom_flag){
            cstHelperBottom.isVisible=true
            btnHelperAddCard.isVisible=true
//            cstHelperBottom.setOnClickListener {
//                btnHelperNewCard.isVisible=true
//                btnHelperDownCard.isVisible=true
//            }
        }else{
            cstHelperBottom.isVisible=false
            btnHelperAddCard.isVisible=false
            btnHelperNewCard.isVisible=false
            btnHelperDownCard.isVisible=false
        }
    }


}
