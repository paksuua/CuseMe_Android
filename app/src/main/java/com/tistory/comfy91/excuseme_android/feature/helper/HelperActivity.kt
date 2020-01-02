package com.tistory.comfy91.excuseme_android.feature.helper

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.feature.addcard.AddCardActivity
import com.tistory.comfy91.excuseme_android.feature.detailcard.DetailCardActivity
import com.tistory.comfy91.excuseme_android.feature.download_card.DownloadCardActivity
import com.tistory.comfy91.excuseme_android.newStartActivity
import kotlinx.android.synthetic.main.activity_helper.*

class HelperActivity : AppCompatActivity() {

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private lateinit var rotate_forward: Animation
    lateinit var rotate_backward: Animation
    private val transction = supportFragmentManager.beginTransaction()

    private lateinit var dialogBuilder: AlertDialog.Builder

    var disabledCardList: ArrayList<CardBean> = arrayListOf()
    var allCardList: ArrayList<CardBean> = arrayListOf()
    private lateinit var helperFragment: HelperFragment
    //소연 private lateinit var selectSortFragment: SelectSortFragment
    private lateinit var selectSortFragment: HelperFragment
    private var isOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper)

        (intent.getSerializableExtra("DISABLED_CARD"))?.let{
            disabledCardList = it as ArrayList<CardBean>
        }
        (intent.getSerializableExtra("ALL_CARD_DATA"))?.let{
            allCardList= it as ArrayList<CardBean>
        }
        (intent.getSerializableExtra("DOWN_CARD"))?.let{
            val cardBean = it as CardBean
            val intent = Intent(this, DetailCardActivity::class.java)
            intent.putExtra("CARD_DATA", cardBean)
            startActivity(intent)
        }


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
                btnHelperAddCard.startAnimation(rotate_backward)
                btnHelperDownCard.isVisible = false
                btnHelperNewCard.isVisible = false
                btnHelperAddCard.setBackgroundResource(R.drawable.btn_managerhome_plus)
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
                .replace(R.id.frameHelper,selectSortFragment)
                .commit()
        }

        helperFragment = HelperFragment.newInstance(disabledCardList)
        selectSortFragment = SelectSortFragment.newInstance(allCardList)
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
        }else{
            cstHelperBottom.isVisible=false
            btnHelperAddCard.isVisible=false
            btnHelperNewCard.isVisible=false
            btnHelperDownCard.isVisible=false
        }
    }
}
