package com.tistory.comfy91.excuseme_android.feature.helper_sort

import android.os.Bundle
import android.view.animation.AnimationUtils

import android.view.animation.LayoutAnimationController
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper

import com.google.gson.Gson
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.answer.ResCards
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.data.request.BodyChangeAllCards
import com.tistory.comfy91.excuseme_android.data.request.ChangeAllCards
import com.tistory.comfy91.excuseme_android.logDebug
import kotlinx.android.synthetic.main.activity_helper_sort.*
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
    private val rvLayoutManager = HelperSortGridLayoutManager(this@HelperSortActivity, 2)
    private lateinit var rvAnimation: LayoutAnimationController
    private var cardList: ArrayList<CardBean> = arrayListOf()
    private val cardDataRepository = ServerCardDataRepository()
    private var token = SingletoneToken.getInstance().token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper_sort)

        uiInit()

    } // end onCreate()

    override fun onResume() {
        super.onResume()
        getCards()
    }

    private fun uiInit() {
        // 리사이클러뷰 어댑터 생성 및 설정
        "GridLayoutManager Animation Support ${rvLayoutManager.supportsPredictiveItemAnimations()}".logDebug(
            this@HelperSortActivity
        )
        rvHelperSortCard.layoutManager = rvLayoutManager
        rvHelperSortCard.adapter = rvHelperSortCardAdapter
        rvHelperSortCardAdapter.data = cardList

        rvAnimation = AnimationUtils.loadLayoutAnimation(applicationContext, R.anim.drag_and_drop_fall_down)
        rvHelperSortCard.layoutAnimation = rvAnimation
        // ItemTouchHelper 설정 - 사용자의
        // 터치에 따라 호출되는 콜백메소드를 담고 있음
        val callback = DragManageAdapter(
            rvHelperSortCardAdapter,
            ItemTouchHelper.LEFT
                .or(ItemTouchHelper.RIGHT)
                .or(ItemTouchHelper.START)
                .or(ItemTouchHelper.END)
                .or(ItemTouchHelper.UP)
                .or(ItemTouchHelper.DOWN),
            0
        )

        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(rvHelperSortCard)

        // 전체 선택 버튼 설정
        btnHelperSortSelectAll.setOnClickListener {
            setAllCardisChecked()
            rvHelperSortCardAdapter.notifyDataSetChanged()
        }

        // 선택한 카드 삭제 버튼 설정
        btnHelperSortDeleteCard.setOnClickListener { deleteSelectedCard() }

        btnHelperSortBack.setOnClickListener { showDialog() }
    }

    private fun showDialog() {
        AlertDialog.Builder(this@HelperSortActivity)
            .apply {
                this.setMessage("변경 내용을\n저장하시겠습니까?")
                this.setPositiveButton(
                    "저장"
                ) { _, _ ->
                    editAllCards()
                }

                this.setNegativeButton(
                    "저장 안함"
                ) { _, _ -> this@HelperSortActivity.finish() }
            }
            .show()
    }

    private fun getCards() {
        intent.getSerializableExtra("CARD_DATA")
            ?.let {
                cardList.sortByDescending { vid -> vid.visibility }
                cardList.addAll(it as ArrayList<CardBean>)
//                rvHelperSortCardAdapter.data.clear()
//                rvHelperSortCardAdapter.data.addAll(cardList)
                rvHelperSortCardAdapter.notifyDataSetChanged()
                rvHelperSortCard.scheduleLayoutAnimation()
            }
    }

    private fun setAllCardisChecked() {
        // until : 끝값은 사용하지 않는다.
        for (x in 0 until cardList.size) {
            cardList[x].visibility = false
        }
        onBtnAllClicked()
    }

    private fun checkAnyCardChecked(): Boolean {
        var result = false

        for (x in 0 until cardList.size) {
            if (!cardList[x].visibility) {
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
        while (it.hasNext()) {
            if (!it.next().visibility) {
                it.remove()
            }
        }
        rvHelperSortCardAdapter.data = deletedList
        rvHelperSortCardAdapter.notifyDataSetChanged()
    }


    private fun editAllCards() {
        if (token == null) {
            token =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWR4IjozOSwidXVpZCI6ImYzZDViM2E1LTkwYjYtNDVlMy1hOThhLTEyODE5OWNmZTg1MCIsImlhdCI6MTU3NzkwMTA1MywiZXhwIjoxNTc3OTg3NDUzLCJpc3MiOiJnYW5naGVlIn0.QytUhsXf4bJirRR_zF3wdACiNu9ytwUE4mrPSNLCFLk"
        }

        val changeAllCards = changeIntoChangeAllCards(cardList)

        for (i in 0 until cardList.size) {
            "CardIdx: ${cardList[i].cardIdx}, card.toString():${cardList[i].toString()} ".logDebug(
                this@HelperSortActivity
            )
        }

        "Token: $token".logDebug(this@HelperSortActivity)

        Gson().toJson(
            BodyChangeAllCards(
                changeAllCards
            )
        ).logDebug(this@HelperSortActivity)

        cardDataRepository.changeAllCards(
            token!!,
            BodyChangeAllCards(
                changeAllCards
            )
        ).enqueue(object : Callback<ResCards> {
            override fun onFailure(call: Call<ResCards>, t: Throwable) {
                "Fail to Edit All Cards, message : ${t.message}".logDebug(this@HelperSortActivity)
            }

            override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                "code : ${response.code()}, message : ${response.message()}".logDebug(this@HelperSortActivity)
                if (response.isSuccessful) {
                    response.body()?.let {
                        "status : ${it.status}, success : ${it.success}, message : ${it.message}".logDebug(
                            this@HelperSortActivity
                        )
                        this@HelperSortActivity.finish()
                    }
                } else {
                    "resonse is Not Success = Body is Empty".logDebug(this@HelperSortActivity)
                }
            }
        })
    }

    private fun changeIntoChangeAllCards(cardList: List<CardBean>): List<ChangeAllCards> {
        var changeAllCardsList: ArrayList<ChangeAllCards> = arrayListOf()
        for (i in 0 until cardList.size) {
            changeAllCardsList.add(
                ChangeAllCards(
                    cardList[i].cardIdx,
                    cardList[i].visibility,
                    i
                )
            )
        }
        return changeAllCardsList
    }
} // end class
