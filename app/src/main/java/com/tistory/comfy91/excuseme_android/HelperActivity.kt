package com.tistory.comfy91.excuseme_android

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.tistory.comfy91.excuseme_android.data.DataHelperCard
import kotlinx.android.synthetic.main.activity_helper.*

class HelperActivity : AppCompatActivity() {

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private lateinit var rotate_forward: Animation
    lateinit var rotate_backward: Animation
    private val transction = supportFragmentManager.beginTransaction()

    // dummy card data
    private lateinit var fromServerData: ArrayList<DataHelperCard>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper)


        //val navView: BottomNavigationView = findViewById(R.id.nvHelperFirst)
        fab_open = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
        rotate_forward = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_forward)
        rotate_backward = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_backward)
        var isOpen = true



        btnHelperAddCard.setOnClickListener {
            if (isOpen) {
                btnHelperDownCard.startAnimation(fab_open)
                btnHelperNewCard.startAnimation(fab_open)
                btnHelperAddCard.startAnimation(rotate_forward)
                btnHelperDownCard.isVisible = true
                btnHelperNewCard.isVisible = true
                isOpen = false
            } else {
                btnHelperDownCard.startAnimation(fab_close)
                btnHelperNewCard.startAnimation(fab_close)
                btnHelperAddCard.startAnimation(rotate_forward)
                btnHelperDownCard.isVisible = true
                btnHelperNewCard.isVisible = true
                btnHelperDownCard.isClickable = true
                btnHelperNewCard.isClickable = true
                isOpen = true
            }
        }
        btnHelperDownCard.setOnClickListener {
            Toast.makeText(applicationContext, "Button DownCard Clicked", Toast.LENGTH_LONG)
                .show()
        }
        btnHelperNewCard.setOnClickListener {
            Toast.makeText(applicationContext, "Button NewCard Clicked", Toast.LENGTH_LONG)
                .show()
        }


        uiInit()
    }

    private fun uiInit(){
        btnHelperGoDisabled.setOnClickListener {
            transction.add(R.id.frameHelper, HelperFragment.newInstance(fromServerData))
        }
    }
}
