package com.tistory.comfy91.excuseme_android


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val view =inflater.inflate(R.layout.fragment_select_sort, container, false)

        initUI()
        dummyData = arrayListOf(
            DataHelperSortCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "first card",
                false
            ),
            DataHelperSortCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "second card",
                false

            ),
            DataHelperSortCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "third card",
                false
            ),
            DataHelperSortCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "fourth card",
                false
            )
        )
        return view
    }

    private fun initUI(){
        if(activity?.baseContext != null) {
            selectSortAdapter = RvHelperSortAdapter(activity!!.baseContext) {}
                .apply {
                    data = dummyData
                }
            rvSelectSort.adapter = selectSortAdapter
        }
        else{
            "Activity.baseContext is NULL".logDebug(this@SelectSortFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUI()
    }
}
