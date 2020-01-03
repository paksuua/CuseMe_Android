package com.tistory.comfy91.excuseme_android.feature.helper


import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.tistory.comfy91.excuseme_android.feature.detailcard.DetailCardActivity
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.feature.disabled.DisabledActivity
import com.tistory.comfy91.excuseme_android.feature.helper_sort.HelperSortActivity
import com.tistory.comfy91.excuseme_android.logDebug
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_helper_sort.*
import kotlinx.android.synthetic.main.fragment_helper.*
import kotlinx.android.synthetic.main.fragment_select_sort.*
import retrofit2.Call
import java.io.IOException
import retrofit2.Callback
import retrofit2.Response

class HelperFragment() : Fragment() {
    lateinit var helperAdapter: RvHelperAdapter
    private var disabledCardList: ArrayList<CardBean> = arrayListOf()
    private var selected_card_num = 0
    private val changeBottomBar: (Boolean, String, Int) -> Unit = { isSelected: Boolean, s: String, cardNum: Int ->
        if(isSelected){
            tvHelper.text = s
            selected_card_num = cardNum
            (activity as HelperActivity).BottomBarChange(true)
        }
        else{
            tvHelper.text = ""
            (activity as HelperActivity).BottomBarChange(false)

        }

    }

    private var token: String? = null
    private val cardDataRepository = ServerCardDataRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_helper, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUi()
    }

    override fun onResume() {
        super.onResume()
        getAllCardData()
    }

    private fun initUi() {
        // DisabledAvtivity로 이동
        btnHelperUnlock?.setOnClickListener{
            activity?.let{
                val intent = Intent (it, DisabledActivity::class.java)
                it.startActivity(intent)
            }
        }

        // HelperSortActivity로 이동
        btnHelperGoSort.setOnClickListener{
            activity?.let{
                val intent = Intent (it.baseContext, HelperSortActivity::class.java)
                intent.putExtra("CARD_DATA", disabledCardList)
                it.startActivity(intent)
            }
        }

        // 카드삭제
        btnHelperDeleteCard.setOnClickListener {
            // 삭제를 물어보는 다이얼로그 띄움
            alertDialog(it)
        }

        // 카드 숨기기
        btnHelperInvisCard.setOnClickListener {
            //TODO: HelperSortActivity로 이동
            activity?.let{
                val intent = Intent (it, HelperSortActivity::class.java)
                it.startActivity(intent)
            }
        }

        // 수정
        btnHelperModCard.setOnClickListener {
            //TODO: DetailCardActivity로 이동
            activity?.let{
                val intent = Intent (it, DetailCardActivity::class.java)
                it.startActivity(intent)
            }
        }

        // 취소
        btnHelperCancleCard.setOnClickListener {
            // TODO: 선택한 카드 취소
            cstHelperSecond.isVisible = false
        }

        // HelperAdapter 초기화
        activity?.let{
            helperAdapter =
                RvHelperAdapter(
                    it.baseContext,
                    changeBottomBar
                )
            rvHelperCard.adapter= helperAdapter
            rvHelperCard.layoutManager = GridLayoutManager(it.baseContext, 2)
        }
        helperAdapter.data = disabledCardList
        helperAdapter.notifyDataSetChanged()
        /// 소연이가 추가한 컬럼
        helperAdapter.data.clear()
        helperAdapter.data.addAll(getCarDummy())
        helperAdapter.notifyDataSetChanged()
        ///여기까지임
    }


    private fun getAllCardData(){
        SingletoneToken.getInstance()
            .token?.let{
            cardDataRepository
                .getAllCards(it)
                .enqueue(object: Callback<ResCards>{
                    override fun onFailure(call: Call<ResCards>, t: Throwable) {
                        "Fail to Get All Card Data message:${t.message}".logDebug(this@HelperFragment)
                    }

                    override fun onResponse(
                        call: Call<ResCards>,
                        response: Response<ResCards>
                    ) {
                        if(response.isSuccessful){
                            response.body()!!.let{res->
                                "success: ${res.success} status: ${res.status}, data: ${res.data}, message: ${res.message}".logDebug(this@HelperFragment)

                                when(res.success){
                                    true->{
                                        disabledCardList.clear()
                                        disabledCardList.addAll(res.data!!)
//                                            helperAdapter.data.clear()
//                                            helperAdapter.data.addAll(disabeld)
                                        helperAdapter.notifyDataSetChanged()
                                    }
                                    false ->{
                                        "Get All Card Data Response is not Success".logDebug(this@HelperFragment)
                                    }
                                }
                            }
                        }
                        else{
                            response.body()?.let{ it ->
                                "success: ${it.success} status: ${it.status}, data: ${it.data}, message: ${it.message}".logDebug(this@HelperFragment)
                            }
                            "resonser is not success".logDebug(this@HelperFragment)
                        }
                    }

                })
        }
    }


    private fun deleteHelperCard() {
        val it: MutableIterator<CardBean> = disabledCardList.iterator()
        while(it.hasNext()){
            if(it.next().visibility){
                it.remove()
            }
        }
        helperAdapter.notifyDataSetChanged()
    }

    fun alertDialog(view: View) {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle("카드를 완전히\n삭제하시겠습니까?")
        //alertDialog.setMessage("Message")

        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "취소"
        ) { dialog, which -> dialog.dismiss() }

        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "삭제"
        ) { dialog, which -> deleteHelperCard() }
        alertDialog.show()

        val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative.layoutParams = layoutParams
    }

    private fun getCarDummy(): ArrayList<CardBean> {

        var dummyList = arrayListOf(
            CardBean(
                0,
                "first card",
                "desc11",
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                0,
                false,
                "serialNum",
                0,
                ""
            ),
            CardBean(
                0,
                "second card",
                "desc222",
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                0,
                false,
                "serialNum",
                0,
                ""

            ),
            CardBean(
                0,
                "third card",
                "desc33",
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                0,
                false,
                "serialNum",
                0,
                ""
            ),
            CardBean(
                0,
                "fourth card",
                "desc44",
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                0,
                false,
                "serialNum",
                0,
                ""
            ),
            CardBean(
                0,
                "fifth card",
                "desc55",
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                0,
                false,
                "serialNum",
                0,
                ""
            ),
            CardBean(
                0,
                "sixth card",
                "desc66",
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                0,
                false,
                "serialNum",
                0,
                ""
            )
        )

        return dummyList
    }

    companion object{
        fun newInstance(dummyData: ArrayList<CardBean>)=
            HelperFragment().apply {
                arguments=Bundle().apply {
                    putParcelableArrayList(
                        "DISABLED_CARD",
                        dummyData as java.util.ArrayList<out Parcelable>
                    )
                }
            }
    }
}