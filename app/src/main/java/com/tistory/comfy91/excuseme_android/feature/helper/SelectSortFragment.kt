package com.tistory.comfy91.excuseme_android.feature.helperimport android.content.Intentimport android.os.Bundleimport android.os.Parcelableimport android.util.Logimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport android.widget.SearchViewimport androidx.core.view.isVisibleimport androidx.fragment.app.Fragmentimport androidx.recyclerview.widget.GridLayoutManagerimport androidx.recyclerview.widget.RecyclerViewimport com.tistory.comfy91.excuseme_android.Rimport com.tistory.comfy91.excuseme_android.data.CardBeanimport com.tistory.comfy91.excuseme_android.data.ResCardsimport com.tistory.comfy91.excuseme_android.data.SingletoneTokenimport com.tistory.comfy91.excuseme_android.data.repository.DummyCardDataRepositoryimport com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepositoryimport com.tistory.comfy91.excuseme_android.feature.helper_sort.HelperSortActivityimport com.tistory.comfy91.excuseme_android.feature.helper_sort.HelperSortCardViewHolderimport com.tistory.comfy91.excuseme_android.feature.helper_sort.RvHelperSortAdapterimport com.tistory.comfy91.excuseme_android.feature.setting.SettingActivityimport com.tistory.comfy91.excuseme_android.logDebugimport com.tistory.comfy91.excuseme_android.newStartActivityimport kotlinx.android.synthetic.main.fragment_select_sort.*import kotlinx.android.synthetic.main.fragment_select_sort.view.*import retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseclass SelectSortFragment : Fragment() {    private lateinit var selectSortAdapter: RvHelperSortAdapter    private val token = SingletoneToken.getInstance().token    private val cardDataRepository = ServerCardDataRepository()    private var cardList: ArrayList<CardBean> = arrayListOf()    override fun onCreateView(        inflater: LayoutInflater, container: ViewGroup?,        savedInstanceState: Bundle?    ): View? {        val view = inflater.inflate(R.layout.fragment_select_sort, container, false)        view.svSelectSortSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{            override fun onQueryTextSubmit(query: String?): Boolean {                Log.d("yong","listener 111 ")                selectSortAdapter!!.filter.filter(query)                return false            }            override fun onQueryTextChange(query: String?): Boolean {                Log.d("yong","listener 222 ")                selectSortAdapter!!.filter.filter(query)                return false            }        })        return view    }    override fun onActivityCreated(savedInstanceState: Bundle?) {        super.onActivityCreated(savedInstanceState)        initUI()    }    override fun onResume() {        super.onResume()        getAllCardS()        dataSort(SORT_BY_VISIBILITY)    }    private fun initUI() {        initRecyclerView()        initSortButton()        btnSelectSortSetting.setOnClickListener {            val intent = Intent(this@SelectSortFragment.context, HelperSortActivity::class.java)            intent.putExtra("CARD_DATA", cardList)            this@SelectSortFragment.context?.newStartActivity(HelperSortActivity::class.java)        }    }    private fun getAllCardS(){        token?.let{            requestAllCards(it)        }    }    private fun requestAllCards(token: String){        "Request All Cards Data".logDebug(this@SelectSortFragment)        cardDataRepository            .getAllCards(token)            .enqueue(object: Callback<ResCards> {                override fun onFailure(call: Call<ResCards>, t: Throwable) {                    "Get All Cards is Fail message : ${t.message}".logDebug(this@SelectSortFragment)                }                override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {                    if(response.isSuccessful){                        response.body()!!.let{body ->                            "status : ${body.status} data : ${body.data} message : ${body.message}".logDebug(this@SelectSortFragment)                            cardList.clear()                            cardList.addAll(body.data!!)                            selectSortAdapter.notifyDataSetChanged()                        }                    }                    else{                        "Resopnser is not Successful".logDebug(this@SelectSortFragment)                    }                }            })    }    private fun initRecyclerView() {        if (activity?.baseContext != null) {            selectSortAdapter = RvHelperSortAdapter(                activity!!.baseContext,                {},                HelperSortCardViewHolder.SELECT_SORT_FRAGMENT            )                .apply {                    if (this@SelectSortFragment.cardList != null) {                        tvSelectSortAlert.isVisible = false                        rvSelectSort.isVisible = true                        data = this@SelectSortFragment.cardList                        searchedList = data                    }                }            rvSelectSort.adapter = selectSortAdapter            rvSelectSort.layoutManager = GridLayoutManager(activity!!.baseContext, 2)            selectSortAdapter.notifyDataSetChanged()        } else {            "Activity.baseContext is NULL".logDebug(this@SelectSortFragment)        }    }    private fun initSortButton() {        ctvSelectSortSeeSort.setOnClickListener {            dataSort(SORT_BY_VISIBILITY)        }        ctvSelectSortCountSort.setOnClickListener {            dataSort(SORT_BY_COUNT)        }        ctvSelectSortNameSort.setOnClickListener {            dataSort(SORT_BY_TITLE)        }    }    private fun dataSort(sortStandard: Int) {        when (sortStandard) {            SORT_BY_VISIBILITY -> this.cardList.sortByDescending { it.visibility }            SORT_BY_COUNT -> this.cardList.sortByDescending { it.count }            SORT_BY_TITLE -> this.cardList.sortBy { it.title }            else -> "Wrong Standard Flag".logDebug(activity!!.baseContext)        }        selectSortAdapter.notifyDataSetChanged()    }    companion object {        const val SORT_BY_VISIBILITY = 1        const val SORT_BY_COUNT = 2        const val SORT_BY_TITLE = 3        fun newInstance(cardList: ArrayList<CardBean>)=            HelperFragment().apply {                arguments=Bundle().apply {                    putParcelableArrayList(                        "ALL_CARD_DATA",                        cardList as java.util.ArrayList<out Parcelable>                    )                }            }    }}