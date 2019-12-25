package com.tistory.comfy91.excuseme_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_disabled.*


class DisabledActivity : AppCompatActivity() {

    private val rvDisabledaAapter = CardAdapter(this)

    private val rvLayoutManager = GridLayoutManager(this@DisabledActivity, 2)


    override fun  onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disabled)

        //recycleView 초기화

        layoutInit()
    }

    private fun layoutInit(){

        rvDisabledCard.adapter = rvDisabledaAapter
        rvDisabledCard.layoutManager = rvLayoutManager



        rvDisabledaAapter.data = listOf(
            ItemCard(
                "http://cfile1.uf.tistory.com/image/0138F14A517F77713A43A6",
                "card1"
            ),
            ItemCard(
                "http://cfile1.uf.tistory.com/image/0138F14A517F77713A43A6",
                "card2"
            ),
            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card3"
            )
        )

    }

}
