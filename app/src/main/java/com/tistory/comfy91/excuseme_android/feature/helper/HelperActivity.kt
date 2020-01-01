package com.tistory.comfy91.excuseme_android.feature.helper

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.R.drawable.btn_home_close
import com.tistory.comfy91.excuseme_android.data.DataHelperCard
import com.tistory.comfy91.excuseme_android.feature.addcard.AddCardActivity
import com.tistory.comfy91.excuseme_android.feature.download_card.DownloadCardActivity
import kotlinx.android.synthetic.main.activity_helper.*

class HelperActivity : AppCompatActivity() {

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private lateinit var rotate_forward: Animation
    lateinit var rotate_backward: Animation
    private val transction = supportFragmentManager.beginTransaction()

    private val TAG = javaClass.name
    private lateinit var dialogBuilder: AlertDialog.Builder
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private lateinit var recordFileName: String
    private var recordFlag = true
    private var playFlag = true


    // dummy card data
    public lateinit var fromServerData: ArrayList<DataHelperCard>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper)

        var isOpen = false
        //btnHelperAddCard.isVisible=true


        //val navView: BottomNavigationView = findViewById(R.id.nvHelperFirst)
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


        // 더미데이터 초기화
        InitDummydata()

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
                btnHelperAddCard.setBackgroundResource(btn_home_close)
                isOpen = true
            } else {
                backHelperBlur.isVisible=false
                btnHelperDownCard.startAnimation(fab_close)
                btnHelperNewCard.startAnimation(fab_close)
                btnHelperAddCard.startAnimation(rotate_forward)
                btnHelperDownCard.isVisible = false
                btnHelperNewCard.isVisible = false
                btnHelperAddCard.setBackgroundResource(R.drawable.btn_managerhome_plus)
                isOpen = false
            }
        }

        // 카드 다운로드뷰로 이동
        if(isOpen){
            btnHelperDownCard.setOnClickListener{
                //val context = it.context
                val intent = Intent(this, DownloadCardActivity::class.java)
                startActivity(intent)
            }

            // 카드생성 뷰로 이동
            btnHelperNewCard.setOnClickListener{
                //val context = it.context
                val intent = Intent(this, AddCardActivity::class.java)
                startActivity(intent)
            }
        }

        btnHelperDownCard.setOnClickListener {
            val intent = Intent(this, DownloadCardActivity::class.java)
            startActivity(intent)
        }
        btnHelperNewCard.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            startActivity(intent)
            /*val intent = Intent(this, NewCardActivity::class.java)
            startActivity(intent)*/
        }

        // 홈미리보기 프래그먼트로 전환
        btnHelperGoDisabled.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameHelper, HelperFragment())
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

        InitUI()
    }

    private fun InitUI(){
        // 리사이클러뷰 어댑터 생성 및 설정
        /*rvHelperAdapter =
            RvHelperAdapter(
                this,
                onBtnAllClicked
            )
        rvHelperCard.adapter = rvHelperAdapter
        rvHelperCard.layoutManager = rvLayoutManager
        rvHelperAdapter.data = fromServerData
        rvHelperAdapter.notifyDataSetChanged()*/

        transction.add(
            R.id.frameHelper,
            HelperFragment.newInstance(
                fromServerData
            )
        )
        transction.commit()
    }

    private fun InitDummydata(){
        fromServerData = arrayListOf(
            DataHelperCard(
            "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
            "first card",
            true,
            "큐즈밀리11"
        ),
        DataHelperCard(
            "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
            "second card",
            true,
            "큐즈밀리22"
        ),
        DataHelperCard(
            "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
            "third card",
            true,
            "큐즈밀리33"
        ),
        DataHelperCard(
            "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
            "fourth card",
            true,
            "큐즈밀리44"
        ),
        DataHelperCard(
            "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
            "fifth card",
            true,
            "큐즈밀리55"
        ),
        DataHelperCard(
            "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
            "sixth card",
            true,
            "큐즈밀리66"
        )
        )
        //endregion
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
