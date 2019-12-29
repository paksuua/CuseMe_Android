package com.tistory.comfy91.excuseme_android.feature.disabled

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tistory.comfy91.excuseme_android.CardAdapter
import com.tistory.comfy91.excuseme_android.ItemCard
import com.tistory.comfy91.excuseme_android.R
import kotlinx.android.synthetic.main.activity_disabled.*
import android.view.WindowManager
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


class DisabledActivity : AppCompatActivity() {

    lateinit var gridManager1: GridLayoutManager
    lateinit var gridManager2: GridLayoutManager
    lateinit var gridManager3: GridLayoutManager

    lateinit var mCurrentLayoutManager: RecyclerView.LayoutManager
    lateinit var rvDisabledaAapter: CardAdapter
    lateinit var cardData: ArrayList<ItemCard>
    var position = 0
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.tistory.comfy91.excuseme_android.R.layout.activity_disabled)

        //그냥 터치 할 때
        //btnDisabledUnlock.setOnClickListener{
        //val intent = Intent(this, TestActivity::class.java)
        //tartActivity(intent)
        //count++
        //tv2.setText(""+count)
        //}


        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)


        btnDisabledUnlock.setOnClickListener(object : View.OnClickListener {

            private var mLastClickTime: Long = 0

            override fun onClick(v: View) {

                if (SystemClock.elapsedRealtime() - mLastClickTime > 3000) {
                    Log.v("Excuse", "터치")
                    mLastClickTime = SystemClock.elapsedRealtime()
                    count++
                    tv2.setText("" + count)
                } else return
                Log.v("Excuse", "연속 터치")

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

                        // 2에서 3됐을 때
                        if (detector.currentSpan - detector.previousSpan < -1) {
                            if (mCurrentLayoutManager == gridManager2) {
                                mCurrentLayoutManager = gridManager3
                                rvDisabledCard.layoutManager = mCurrentLayoutManager
                                rvDisabledCard.scrollToPosition(position)
                                return true

                                //1에서 2됐을 때
                            } else if (detector.currentSpan - detector.previousSpan < -1) {
                                if (mCurrentLayoutManager == gridManager1) {
                                    mCurrentLayoutManager = gridManager2
                                    rvDisabledCard.layoutManager = mCurrentLayoutManager
                                    rvDisabledCard.scrollToPosition(position)
                                    return true
                                }
                            }
                        }
                        //3에서 2됐을 떄
                    } else if (detector.currentSpan - detector.previousSpan > 1) {
                        if (mCurrentLayoutManager == gridManager3) {
                            mCurrentLayoutManager = gridManager2
                            rvDisabledCard.layoutManager = mCurrentLayoutManager
                            rvDisabledCard.scrollToPosition(position)
                            return true

                            //2에서 1됐을 때
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
        gridManager3 = GridLayoutManager(this, 3)


        //adapter 초기화
        rvDisabledaAapter = CardAdapter(
            cardData,
            object : CardAdapter.ItemClickListener {
                override fun onItemClicked(itemCard: ItemCard) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        rvDisabledCard.adapter = rvDisabledaAapter

        mCurrentLayoutManager = gridManager2
        rvDisabledCard.layoutManager = gridManager2
        rvDisabledCard.adapter = rvDisabledaAapter

        svSortSearch!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("zzzz", "listener 1 ")
                rvDisabledaAapter!!.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("zzzz", "listener 2 ")
                rvDisabledaAapter!!.filter.filter(query)
                return false
            }
        })




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
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "집"
            ),
            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "아파요"
            ),
            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "병원"
            ),
            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "학교"
            ),

            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "병원 가고싶어요"
            ),

            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "목 말라요"
            ),

            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "화장실2"
            ),

            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "화장실"
            ),

            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "mdlsf"
            ),

            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "abcdef"
            ),

            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "Frozen"
            ),

            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "아이언맨"
            ),

            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "어밴져스"
            ),

            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "겨울왕국2"
            ),

            ItemCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "겨울왕국"
            )


        )

    }

    //override fun onItemClicked(photocard: ItemCard) {
    //tvDisabledShowCardText.setText(.title)
    //}


}



