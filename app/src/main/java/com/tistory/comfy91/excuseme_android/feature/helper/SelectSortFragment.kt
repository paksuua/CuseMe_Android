
package com.tistory.comfy91.excuseme_android.feature.helper


import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.text.Spannable
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.data.server.BodyChangeAllCards
import com.tistory.comfy91.excuseme_android.feature.helper_sort.HelperSortCardViewHolder
import com.tistory.comfy91.excuseme_android.feature.helper_sort.RvHelperSortAdapter
import com.tistory.comfy91.excuseme_android.feature.setting.SettingActivity
import com.tistory.comfy91.excuseme_android.logDebug
import com.tistory.comfy91.excuseme_android.newStartActivity
import kotlinx.android.synthetic.main.fragment_select_sort.*
import kotlinx.android.synthetic.main.fragment_select_sort.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SelectSortFragment : Fragment() {
    private lateinit var selectSortAdapter: SelectSortAdapter
    private val token = SingletoneToken.getInstance().token
    private val token_vis = SingletoneToken.getInstance().token
    private val cardDataRepository = ServerCardDataRepository()

    private var cardList: ArrayList<CardBean> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_select_sort, container, false)
        view.svSelectSortSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("yong","listener 111 ")
                selectSortAdapter!!.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("yong","listener 222 ")
                selectSortAdapter!!.filter.filter(query)
                return false
            }
        })
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUI()
    }

    override fun onResume() {
        super.onResume()
        getAllCardS()
        dataSort(SORT_BY_VISIBILITY)
    }

    private fun initUI() {
        initRecyclerView()
        initSortButton()

        btnSelectSortSetting.setOnClickListener {
            this@SelectSortFragment.context?.newStartActivity(SettingActivity::class.java)
        }

        imgSelectSortConfirm.setOnClickListener {
            (context as HelperActivity).finish()
        }
    }

    private fun getAllCardS(){
        token?.let{
            requestAllCards(it)
        }
    }

    private fun requestAllCards(token: String){
        "Request All Cards Data".logDebug(this@SelectSortFragment)
        cardDataRepository
            .getAllCards(token)
            .enqueue(object: Callback<ResCards> {
                override fun onFailure(call: Call<ResCards>, t: Throwable) {
                    "Get All Cards is Fail message : ${t.message}".logDebug(this@SelectSortFragment)
                    //소연
                    rvSelectSort.isVisible=false
                    tvSelectSortAlert.isVisible=true
                }

                override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                    if(response.isSuccessful){
                        response.body()!!.let{body ->
                            "status : ${body.status} data : ${body.data} message : ${body.message}".logDebug(this@SelectSortFragment)
                            selectSortAdapter.data.clear()
                            selectSortAdapter.data.addAll(body.data!!)
//                            cardList.clear()
//                            cardList.addAll(body.data!!)
//                            val sortedOne = cardList.sortedBy { it.sequence }
//                            for(i in 0 until sortedOne.size ){
//                                    cardList.add(sortedOne[i])
//                            }
                            selectSortAdapter.notifyDataSetChanged()
                            // 소연
                            rvSelectSort.isVisible=true
                            tvSelectSortAlert.isVisible=false
                        }
                    }
                    else{
                        "Resopnser is not Successful".logDebug(this@SelectSortFragment)
                    }
                }

            })
    }

    private fun initRecyclerView() {
        if (activity?.baseContext != null) {
            selectSortAdapter = SelectSortAdapter(
                activity!!.baseContext,
                {},
                HelperSortCardViewHolder.SELECT_SORT_FRAGMENT
            )
                .apply {
                    if (this@SelectSortFragment.cardList != null) {
                        tvSelectSortAlert.isVisible = false
                        rvSelectSort.isVisible = true
                        data = this@SelectSortFragment.cardList
                        searchedList = data
                    }
                }
            rvSelectSort.adapter = selectSortAdapter
            rvSelectSort.layoutManager = GridLayoutManager(activity!!.baseContext, 2)
            selectSortAdapter.notifyDataSetChanged()
        } else {
            "Activity.baseContext is NULL".logDebug(this@SelectSortFragment)
        }
    }

    private fun initSortButton() {
        ctvSelectSortSeeSort.setOnClickListener {
            dataSort(SORT_BY_VISIBILITY)
        }
        ctvSelectSortCountSort.setOnClickListener {
            dataSort(SORT_BY_COUNT)
        }
        ctvSelectSortNameSort.setOnClickListener {
            dataSort(SORT_BY_TITLE)
        }
    }

    private fun dataSort(sortStandard: Int) {

        val span1 = ctvSelectSortSeeSort.text as Spannable
        val span2 = ctvSelectSortCountSort.text as Spannable
        val span3 = ctvSelectSortNameSort.text as Spannable

        when (sortStandard) {
            SORT_BY_VISIBILITY -> {
                span1.setSpan(ForegroundColorSpan(Color.parseColor("#fb6d6a")), 0, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                span2.setSpan(ForegroundColorSpan(Color.parseColor("#b4b4b4")), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                span3.setSpan(ForegroundColorSpan(Color.parseColor("#b4b4b4")), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                span1.setSpan(StyleSpan(Typeface.BOLD), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                this.cardList.sortBy { it.sequence}
                img_select_sort_clicked.isVisible = true
                img_select_sort_clicked2.isVisible = false
                img_select_sort_clicked3.isVisible = false
            }
            SORT_BY_COUNT -> {
                this.cardList.sortByDescending { it.count }
                span2.setSpan(ForegroundColorSpan(Color.parseColor("#fb6d6a")), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                span1.setSpan(ForegroundColorSpan(Color.parseColor("#b4b4b4")), 0, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                span3.setSpan(ForegroundColorSpan(Color.parseColor("#b4b4b4")), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                span2.setSpan(StyleSpan(Typeface.BOLD), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                img_select_sort_clicked2.isVisible = true
                img_select_sort_clicked.isVisible = false
                img_select_sort_clicked3.isVisible = false
            }

            SORT_BY_TITLE -> {
                this.cardList.sortBy { it.title }
                span3.setSpan(ForegroundColorSpan(Color.parseColor("#fb6d6a")), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                span1.setSpan(ForegroundColorSpan(Color.parseColor("#b4b4b4")), 0, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                span2.setSpan(ForegroundColorSpan(Color.parseColor("#b4b4b4")), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                span3.setSpan(StyleSpan(Typeface.BOLD), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                img_select_sort_clicked3.isVisible = true
                img_select_sort_clicked.isVisible = false
                img_select_sort_clicked2.isVisible = false
            }
            else -> "Wrong Standard Flag".logDebug(activity!!.baseContext)
        }
        selectSortAdapter.notifyDataSetChanged()
    }

    // 소연
    private fun editCardVisibility(){
        if(token_vis == null){
            //token_vis =  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWR4IjozOSwidXVpZCI6ImYzZDViM2E1LTkwYjYtNDVlMy1hOThhLTEyODE5OWNmZTg1MCIsImlhdCI6MTU3NzkwMTA1MywiZXhwIjoxNTc3OTg3NDUzLCJpc3MiOiJnYW5naGVlIn0.QytUhsXf4bJirRR_zF3wdACiNu9ytwUE4mrPSNLCFLk"
        }

        for(i in 0 until cardList.size){
            cardList[i].sequence = i
        }

        for(i in 0 until cardList.size){
            "CardIdx: ${cardList[i].cardIdx}, card.toString():${cardList[i].toString()} ".logDebug(this@SelectSortFragment)
        }

        "Token: $token".logDebug(this@SelectSortFragment)

        Gson().toJson(
            BodyChangeAllCards(
            cardList as List<CardBean>
        )
        ).logDebug(this@SelectSortFragment)

        cardDataRepository.changeAllCards(
            token!!,
            BodyChangeAllCards(
                cardList as List<CardBean>
            )
        ).enqueue(object: Callback<ResCards> {
            override fun onFailure(call: Call<ResCards>, t: Throwable) {
                "Fail to Edit All Cards, message : ${t.message}".logDebug(this@SelectSortFragment)
            }

            override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                "code : ${response.code()}, message : ${response.message()}".logDebug(this@SelectSortFragment)
                if(response.isSuccessful){
                   /* response.body()?.let{
                        "status : ${it.status}, success : ${it.success}, message : ${it.message}".logDebug(this@SelectSortFragment)
                        this@SelectSortFragment.finish()
                    }*/

                }
                else{
                    "resonse is Not Success = Body is Empty".logDebug(this@SelectSortFragment)
                }
            }

        })
    }


    companion object {
        const val SORT_BY_VISIBILITY = 1
        const val SORT_BY_COUNT = 2
        const val SORT_BY_TITLE = 3

        fun newInstance(cardList: ArrayList<CardBean>)=
            SelectSortFragment().apply {
                arguments=Bundle().apply {
                    putParcelableArrayList(
                        "ALL_CARD_DATA",
                        cardList as java.util.ArrayList<out Parcelable>
                    )
                }
            }
    }
}

