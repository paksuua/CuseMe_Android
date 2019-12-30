package com.tistory.comfy91.excuseme_android.feature.helper_sort

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.DataHelperSortCard
import kotlinx.android.synthetic.main.activity_helper_sort.*

class HelperSortActivity : AppCompatActivity() {
    val onBtnAllClicked: () -> Unit = {
        btnHelperSortDeleteCard.isVisible = checkAnyCardChecked()
    }
    private val rvHelperSortCardAdapter =
        RvHelperSortAdapter(
            this,
            onBtnAllClicked,
            HelperSortCardViewHolder.HELPER_SORT_ACTIVITY
        )
    private val rvLayoutManager = GridLayoutManager(this@HelperSortActivity, 2)

    // dummy
    private lateinit var dummyData: ArrayList<DataHelperSortCard>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper_sort)

        // dummy data 생성
        makeDummyData()

        uiInit()
    } // end onCreate()

    private fun uiInit(){
        // 리사이클러뷰 어댑터 생성 및 설정
        rvHelperSortCard.adapter = rvHelperSortCardAdapter
        rvHelperSortCard.layoutManager = rvLayoutManager
        rvHelperSortCardAdapter.data = dummyData
        rvHelperSortCardAdapter.notifyDataSetChanged()

        // ItemTouchHelper 설정 - 사용자의
        // 터치에 따라 호출되는 콜백메소드를 담고 있음
        val callback = DragManageAdapter(
            rvHelperSortCardAdapter,
            this,
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
            ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)
        )
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(rvHelperSortCard)


        // 전체 선택 버튼 설정
        btnHelperSortSelectAll.setOnClickListener {
            setAllCardisChecked()
            rvHelperSortCardAdapter.notifyDataSetChanged()
        }

        // 선택한 카드 삭제 버튼 설정
        btnHelperSortDeleteCard.setOnClickListener{
            deleteSelectedCard()
        }

        btnHelperSortBack.setOnClickListener {
            /*if(rvHelperSortCardAdapter.isChanged){
                alertDialog(it)
            }*/
        }
    }

    private fun makeDummyData(){
        dummyData = arrayListOf(
            DataHelperSortCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "first card",
                false,
                0
            ),
            DataHelperSortCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "second card",
                false,
                0

            ),
            DataHelperSortCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "third card",
                false,
                0
            ),
            DataHelperSortCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "fourth card",
                false,
                0
            )
        )
    }
    private fun setAllCardisChecked(){
        // until : 끝값은 사용하지 않는다.
        for(x in 0 until dummyData.size ){
            dummyData[x].visibility = true
        }
        onBtnAllClicked()
    }

    private fun checkAnyCardChecked(): Boolean{
        var result = false

        for(x in 0 until dummyData.size){
            if(dummyData[x].visibility){
                result = true
                break
            }
        }
        return result
    }

    private fun deleteSelectedCard() {
        val it: MutableIterator<DataHelperSortCard> = dummyData.iterator()
        while(it.hasNext()){
            if(it.next().visibility){
                it.remove()
            }
        }
        rvHelperSortCardAdapter.notifyDataSetChanged()
    }
} // end class
