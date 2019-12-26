package com.tistory.comfy91.excuseme_android

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_disabled.*


class DisabledActivity : AppCompatActivity() {

    lateinit var gridManager1: GridLayoutManager
    lateinit var gridManager2: GridLayoutManager
    lateinit var mCurrentLayoutManager: RecyclerView.LayoutManager
    lateinit var rvDisabledaAapter: CardAdapter
    lateinit var cardData: ArrayList<ItemCard>
    var position = 0
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disabled)

        //그냥 터치 할 때
        //btnDisabledUnlock.setOnClickListener{
        //val intent = Intent(this, TestActivity::class.java)
        //tartActivity(intent)
        //count++
        //tv2.setText(""+count)
        //}

        Toast.makeText(this, "safas", Toast.LENGTH_LONG).show()
        toast("safas")
        "safas".toast(this)

        btnDisabledUnlock.setOnClickListener(object : View.OnClickListener {

            private var mLastClickTime: Long = 0

            override fun onClick(v: View) {


                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000)
                    Log.v("Excuse", "연속 터치")
                return

                mLastClickTime = SystemClock.elapsedRealtime()

                Log.v("Excuse", "터치")
                count++
                tv2.setText("" + count)

            }
        })


        //data
        inputData()

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

            //ViewGroup 표면에서 터치 이벤트가 감지될 때 항상 호출
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

                //findChildViewUnder = 지정 point 위의 view를 찾아줌
                val child = rv.findChildViewUnder(e.x, e.y)

                //recyclerView의 빈 곳을 터치할 때
                if (child == null) {

                    //true를 반환하면 이전에 터치 이벤트를 처리하던 하위 뷰가 ACTION_CANCEL을 수신하고
                    // 이 시점 이후로는 이벤트가 상위 뷰의 onTouchEvent() 메서드로 전송되어 평소와 같은 처리
                    return true
                } else {
                    //getChildAdapterPosition = adapter에서 지정된 view에 해당하는 위치 반환.
                    position = rv.getChildAdapterPosition(child!!)
                    mScaleGestureDetector.onTouchEvent(e)

                    Log.v("ExcuseP", position.toString())

                    return false

                }
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }

        })

    }


    private fun inputData() {
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
            ),
            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card4"
            ),

            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card5"
            ),

            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card6"
            ),

            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card7"
            ),

            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card8"
            ),

            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card9"
            ),

            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card10"
            ),

            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card11"
            ),

            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card12"
            ),

            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card13"
            ),

            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card14"
            ),

            ItemCard(
                "https://i.ytimg.com/vi/5-mWvUR7_P0/maxresdefault.jpg",
                "card15d"
            )


        )

    }


}



