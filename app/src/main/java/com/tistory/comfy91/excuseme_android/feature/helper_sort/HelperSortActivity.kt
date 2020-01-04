package com.tistory.comfy91.excuseme_android.feature.helper_sort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCards
import kotlinx.android.synthetic.main.activity_helper_sort.*
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.data.server.BodyChangeAllCards
import com.tistory.comfy91.excuseme_android.logDebug
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HelperSortActivity : AppCompatActivity() {
    val onBtnAllClicked: () -> Unit = {
        btnHelperSortDeleteCard.isVisible = checkAnyCardChecked()
    }
    private val rvHelperSortCardAdapter =
        RealHelperSortAdapter(
            this,
            onBtnAllClicked,
            HelperSortCardViewHolder.HELPER_SORT_ACTIVITY
        )
    private val rvLayoutManager = GridLayoutManager(this@HelperSortActivity, 2)

    private var cardList: ArrayList<CardBean> = arrayListOf()
    private val cardDataRepository = ServerCardDataRepository()
    private var token = SingletoneToken.getInstance().token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.tistory.comfy91.excuseme_android.R.layout.activity_helper_sort)

        uiInit()

    } // end onCreate()

    override fun onResume() {
        super.onResume()
        getCards()
    }

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
                    editAllCards()
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
                cardList.sortByDescending {vid -> vid.visibility }
                cardList.addAll(it as ArrayList<CardBean>)
//                rvHelperSortCardAdapter.data.clear()
//                rvHelperSortCardAdapter.data.addAll(cardList)
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
        val deletedList = arrayListOf<CardBean>()
        deletedList.addAll(cardList)

        val it: MutableIterator<CardBean> = deletedList.iterator()
        while(it.hasNext()){
            if(it.next().visibility){
                it.remove()
            }
        }

        rvHelperSortCardAdapter.data = deletedList
        rvHelperSortCardAdapter.notifyDataSetChanged()
    }


    private fun editAllCards(){
        if(token == null){
            token =  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWR4IjozOSwidXVpZCI6ImYzZDViM2E1LTkwYjYtNDVlMy1hOThhLTEyODE5OWNmZTg1MCIsImlhdCI6MTU3NzkwMTA1MywiZXhwIjoxNTc3OTg3NDUzLCJpc3MiOiJnYW5naGVlIn0.QytUhsXf4bJirRR_zF3wdACiNu9ytwUE4mrPSNLCFLk"
        }

        var changeAllCards  = arrayListOf<CardBean>()
        changeAllCards.addAll(cardList)
        for(i in 0 until changeAllCards.size){
            changeAllCards[i].sequence = i
        }

        var forSendCard = arrayListOf<CardBean>()
        changeAllCards.sortedBy { it.sequence }
            .forEach { forSendCard.add(it) }



        for(i in 0 until changeAllCards.size){
            "changeAllCards.data index : $i: card : ${forSendCard[i]}".logDebug(this@HelperSortActivity)
        }



        "Request Edit All Cards Data : ${changeAllCards}".logDebug(this@HelperSortActivity)
        cardDataRepository.changeAllCards(
            token!!,
            BodyChangeAllCards(
                  forSendCard as List<CardBean>
            )
        ).enqueue(object: Callback<ResCards> {
            override fun onFailure(call: Call<ResCards>, t: Throwable) {
                "Fail to Edit All Cards, message : ${t.message}".logDebug(this@HelperSortActivity)
            }

            override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                "code : ${response.code()}, message : ${response.message()}".logDebug(this@HelperSortActivity)
                if(response.isSuccessful){
                    response.body()?.let{
                        "status : ${it.status}, success : ${it.success}, message : ${it.message}".logDebug(this@HelperSortActivity)
                        this@HelperSortActivity.finish()
                    }

                }
                else{
                  "resonse is Not Success = Body is Empty".logDebug(this@HelperSortActivity)
                }
            }

        })
    }



} // end class
