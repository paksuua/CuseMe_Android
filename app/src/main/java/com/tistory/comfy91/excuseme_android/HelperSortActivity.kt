package com.tistory.comfy91.excuseme_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_helper_sort.*
import kotlinx.android.synthetic.main.activity_main.*

class HelperSortActivity : AppCompatActivity() {
    private val rvHelperSortCardAdapter = CardAdapter(this)

    private val rvLayoutManager = GridLayoutManager(this@HelperSortActivity, 2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper_sort)

        uiInit()
    } // end onCreate()

    private fun uiInit(){
        // 리사이클러뷰 어댑터 생성 및 설정
        rvHelperSortCard.adapter = rvHelperSortCardAdapter
        rvHelperSortCard.layoutManager = rvLayoutManager

//        // ItemTouchHelper 설정
////        val callback = DragManageAdapter(rvLayoutManager,
//                                        this,
//                                        ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
//                                        ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT))
//        val touchHelper = ItemTouchHelper(callback)
//        touchHelper.attachToRecyclerView(rvHelperSortCard)

        rvHelperSortCardAdapter.data = listOf(
            DataCard(
                "https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwjNxNH_-s3mAhWoyosBHQPCDVYQjRx6BAgBEAQ&url=https%3A%2F%2Fkr.pixtastock.com%2Ftags%2F%25EC%2598%2588%25EC%258B%259C%3Fsearch_type%3D2&psig=AOvVaw2GlyncjzgRA1lCskw4YSnC&ust=1577265753079881",
                "first card"
            ),
            DataCard(
                "https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwjNxNH_-s3mAhWoyosBHQPCDVYQjRx6BAgBEAQ&url=https%3A%2F%2Fkr.pixtastock.com%2Ftags%2F%25EC%2598%2588%25EC%258B%259C%3Fsearch_type%3D2&psig=AOvVaw2GlyncjzgRA1lCskw4YSnC&ust=1577265753079881",
                "second card"
            ),
            DataCard(
                "https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwjNxNH_-s3mAhWoyosBHQPCDVYQjRx6BAgBEAQ&url=https%3A%2F%2Fkr.pixtastock.com%2Ftags%2F%25EC%2598%2588%25EC%258B%259C%3Fsearch_type%3D2&psig=AOvVaw2GlyncjzgRA1lCskw4YSnC&ust=1577265753079881",
                "third card"
            )
        )

        btnHelperSortSelectAll.setOnClickListener {

        }


    }
} // end class
