package com.tistory.comfy91.excuseme_android


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.tistory.comfy91.excuseme_android.data.DataHelperSortCard
import com.tistory.comfy91.excuseme_android.feature.RvHelperSortAdapter
import kotlinx.android.synthetic.main.fragment_select_sort.*


class SelectSortFragment : Fragment() {
    private lateinit var selectSortAdapter: RvHelperSortAdapter
    private lateinit var dummyData: ArrayList<DataHelperSortCard>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_sort, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUI()
    }

    private fun initUI() {
//        val rvSelectSort = view.findViewById<RecyclerView>(R.id.rvSelectSort)
        //region dummyData
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
        //endregion

        initRecyclerView()
        initSortButton()
    }

    private fun initRecyclerView() {
        if (activity?.baseContext != null) {
            selectSortAdapter = RvHelperSortAdapter(activity!!.baseContext) {}
                .apply {
                    if (dummyData != null) {
                        tvSelectSortAlert.isVisible = false
                        rvSelectSort.isVisible = true
                        data = dummyData
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
        when (sortStandard) {
            SORT_BY_VISIBILITY -> dummyData.sortByDescending { it.visibility }
            SORT_BY_COUNT -> dummyData.sortByDescending { it.count }
            SORT_BY_TITLE -> dummyData.sortBy { it.title }
            else -> "Wrong Standard Flag".logDebug(activity!!.baseContext)
        }
        selectSortAdapter.notifyDataSetChanged()
    }

    companion object {
        const val SORT_BY_VISIBILITY = 1
        const val SORT_BY_COUNT = 2
        const val SORT_BY_TITLE = 3
    }


}
