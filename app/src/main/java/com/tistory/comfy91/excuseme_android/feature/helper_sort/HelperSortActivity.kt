package com.tistory.comfy91.excuseme_android.feature.helper_sort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.tistory.comfy91.excuseme_android.data.CardBean
import kotlinx.android.synthetic.main.activity_helper_sort.*
import android.content.Intent



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

    private var cardList: ArrayList<CardBean> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.tistory.comfy91.excuseme_android.R.layout.activity_helper_sort)
        getCards()
        uiInit()

    } // end onCreate()

    private fun uiInit(){
        // 리사이클러뷰 어댑터 생성 및 설정
        rvHelperSortCard.adapter = rvHelperSortCardAdapter
        rvHelperSortCard.layoutManager = rvLayoutManager
        rvHelperSortCardAdapter.data = cardList
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
        btnHelperSortDeleteCard.setOnClickListener{deleteSelectedCard()}

        btnHelperSortBack.setOnClickListener { showDialog()}

    }

    private fun showDialog(){
        AlertDialog.Builder(this@HelperSortActivity)
            .apply{
                this.setMessage("변경 내용을\n저장하시겠습니까?")
                this.setPositiveButton("저장"
                ) { _, _ ->
                    //todo("변경된 카드 서버에 전송")
                    this@HelperSortActivity.finish()
                }

                this.setNegativeButton("저장 안함"
                ) { _, _ -> this@HelperSortActivity.finish() }
            }
            .show()


    }

    private fun getCards(){
        intent.getSerializableExtra("CARD_DATA")
            ?.let{
                cardList.addAll(it as ArrayList<CardBean>)
                rvHelperSortCardAdapter.searchedList.clear()
                rvHelperSortCardAdapter.searchedList.addAll(cardList)
                rvHelperSortCardAdapter.notifyDataSetChanged()
            }
    }
    private fun setAllCardisChecked(){
        // until : 끝값은 사용하지 않는다.
        for(x in 0 until cardList.size ){
            cardList[x].visibility = true
        }
        onBtnAllClicked()
    }

    private fun checkAnyCardChecked(): Boolean{
        var result = false

        for(x in 0 until cardList.size){
            if(cardList[x].visibility){
                result = true
                break
            }
        }
        return result
    }

    private fun deleteSelectedCard() {
        val it: MutableIterator<CardBean> = cardList.iterator()
        while(it.hasNext()){
            if(it.next().visibility){
                it.remove()
            }
        }
        rvHelperSortCardAdapter.notifyDataSetChanged()
    }


} // end class
