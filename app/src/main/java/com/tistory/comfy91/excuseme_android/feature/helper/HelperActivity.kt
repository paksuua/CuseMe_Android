package com.tistory.comfy91.excuseme_android.feature.helper

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.feature.addcard.AddCardActivity
import com.tistory.comfy91.excuseme_android.feature.detailcard.DetailCardActivity
import com.tistory.comfy91.excuseme_android.feature.download_card.DownloadCardActivity
import kotlinx.android.synthetic.main.activity_helper.*


class HelperActivity : AppCompatActivity() {

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private lateinit var rotate_forward: Animation
    lateinit var rotate_backward: Animation
    private val transction = supportFragmentManager.beginTransaction()
    var disabledCardList: ArrayList<CardBean> = arrayListOf()
    var allCardList: ArrayList<CardBean> = arrayListOf()
    private lateinit var helperFragment: NewHelperFragment
    private lateinit var selectSortFragment: SelectSortFragment
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
                enableDisableView(backHelperBlur,false )
                enableDisableView(frameHelper,false )

                backHelperBlur.isVisible=true
                btnHelperDownCard.startAnimation(fab_open)
                btnHelperNewCard.startAnimation(fab_open)
                btnHelperAddCard.isSelected=false////
                btnHelperAddCard.startAnimation(rotate_forward)
                btnHelperAddCard.isSelected=true////
                btnHelperDownCard.isVisible = true
                btnHelperNewCard.isVisible = true
                btnHelperDownCard.isClickable = true
                btnHelperNewCard.isClickable = true
                isOpen = true
            } else {
                enableDisableView(backHelperBlur,true )
                enableDisableView(frameHelper,true )

                backHelperBlur.isVisible=false
                btnHelperDownCard.startAnimation(fab_close)
                btnHelperNewCard.startAnimation(fab_close)
                btnHelperAddCard.isSelected=true
                btnHelperAddCard.startAnimation(rotate_backward)
                btnHelperAddCard.isSelected=false
                btnHelperDownCard.isVisible = false
                btnHelperNewCard.isVisible = false
                isOpen = false
            }
        }

        if(isOpen){
            btnHelperDownCard.setOnClickListener{
                val intent = Intent(this, DownloadCardActivity::class.java)
                startActivity(intent)
            }

            btnHelperNewCard.setOnClickListener{
                val intent = Intent(this, AddCardActivity::class.java)
                startActivity(intent)
            }
        }

        btnHelperDownCard.setOnClickListener {
            val intent = Intent(this, DownloadCardActivity::class.java)
            startActivity(intent)
        }

        btnHelperNewCard.setOnClickListener{
            val intent = Intent(this, AddCardActivity::class.java)
            startActivity(intent)
        }


        btnHelperGoDisabled.setOnClickListener {
            changeNaviBarIcon(true)
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameHelper, helperFragment)
                .commit()
        }

        btnHelperAllCard.setOnClickListener {
            changeNaviBarIcon(false)
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameHelper,selectSortFragment)
                .commit()
        }

        helperFragment = NewHelperFragment.newInstance(disabledCardList)
        selectSortFragment = SelectSortFragment.newInstance(allCardList)
        transction.add(
            R.id.frameHelper,
            helperFragment
        )
        transction.commit()
    }

    private fun changeNaviBarIcon(flag: Boolean){
        cktHelperGoDisabled.isSelected = flag
        cktHelperAllCard.isSelected = !flag
        when(flag){
            true -> {
                cktHelperGoDisabled.isChecked=true
                tvHelperGoDisabled.setTextColor(resources.getColor(R.color.mainpink))
                cktHelperAllCard.isChecked=false
                tvHelperAllCard.setTextColor(resources.getColor(R.color.greyish_two))
            }
            false -> {
                cktHelperGoDisabled.isChecked=false
                tvHelperGoDisabled.setTextColor(resources.getColor(R.color.greyish_two))
                cktHelperAllCard.isChecked=true
                tvHelperAllCard.setTextColor(resources.getColor(R.color.mainpink))
            }
        }
    }


    private fun enableDisableView(view: View, boolean: Boolean){
        view.isEnabled = boolean
        if (view is ViewGroup) {
            val group = view
            for (idx in 0 until group.childCount) {
                enableDisableView(group.getChildAt(idx), boolean)
            }
        }
    }
}
