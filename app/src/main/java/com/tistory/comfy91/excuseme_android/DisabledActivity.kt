package com.tistory.comfy91.excuseme_android

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_disabled.*


class DisabledActivity : AppCompatActivity() {

    lateinit var gridManager1 : GridLayoutManager
    lateinit var gridManager2 : GridLayoutManager
    lateinit var mCurrentLayoutManager : RecyclerView.LayoutManager
    lateinit var rvDisabledaAapter : CardAdapter
    var position = 0
    private lateinit var cardData: ArrayList<ItemCard>



    override fun  onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disabled)

        //data
        InputData()

        val mScaleGestureDetector = ScaleGestureDetector(
            this,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    Log.v("Excuse", "줌2")

                    if (detector.currentSpan > 200 && detector.timeDelta > 200) {
                        if (detector.currentSpan - detector.previousSpan < -1) {
                            if (mCurrentLayoutManager == gridManager1) {
                                mCurrentLayoutManager = gridManager2
                                rvDisabledCard.layoutManager = mCurrentLayoutManager
                                rvDisabledCard.scrollToPosition(position)
                                return true
                            }
                        } else if (detector.currentSpan - detector.previousSpan > 1) {
                            if (mCurrentLayoutManager == gridManager2) {
                                mCurrentLayoutManager = gridManager1
                                rvDisabledCard.layoutManager = mCurrentLayoutManager
                                rvDisabledCard.scrollToPosition(position)
                                return true
                            }
                        }
                    }
                    return false
                }
            })

        gridManager1 = GridLayoutManager(this, 1)
        gridManager2 = GridLayoutManager(this, 2)

        //adapter 초기화
        rvDisabledaAapter = CardAdapter(cardData)
        rvDisabledCard.adapter = rvDisabledaAapter

        mCurrentLayoutManager = gridManager2
        rvDisabledCard.layoutManager = gridManager2
        rvDisabledCard.adapter = rvDisabledaAapter

        rvDisabledCard.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                position = rv.getChildAdapterPosition(child!!)
                mScaleGestureDetector.onTouchEvent(e)

                Log.v("ExcuseP", position.toString())

                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }

        })

    }
        private fun InputData(){
            cardData = arrayListOf(
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
