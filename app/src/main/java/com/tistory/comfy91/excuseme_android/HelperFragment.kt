package com.tistory.comfy91.excuseme_android


import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.tistory.comfy91.excuseme_android.data.DataHelperCard
import kotlinx.android.synthetic.main.fragment_helper.*

class HelperFragment : Fragment() {
    private lateinit var helperAdapter: RvHelperAdapter
    private lateinit var dummyData: ArrayList<DataHelperCard>
    private val changeTv: (String) -> Unit = {
        tvHelper.text = it
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_helper, container, false)


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        InitUI()
    }

    private fun InitUI() {
//        val rvSelectSort = view.findViewById<RecyclerView>(R.id.rvSelectSort)
        //region dummyData
        dummyData = arrayListOf(
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "first card",
                true,
                "큐즈밀리"
            ),
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "second card",
                true,
                "큐즈밀리"
            ),
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "third card",
                true,
                "큐즈밀리"
            ),
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "fourth card",
                true,
                "큐즈밀리"
            ),
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "fifth card",
                true,
                "큐즈밀리"
            ),
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "sixth card",
                true,
                "큐즈밀리"
            )
        )
        //endregion

        activity?.let{
            helperAdapter = RvHelperAdapter(it.baseContext, changeTv)
            rvHelperCard.adapter= helperAdapter
            rvHelperCard.layoutManager = GridLayoutManager(it.baseContext, 2)
        }
        helperAdapter.data = dummyData
        helperAdapter.notifyDataSetChanged()
    }

    companion object{
        fun newInstance(dummyData: ArrayList<DataHelperCard>)=
            HelperFragment().apply {
                arguments=Bundle().apply {
                    putParcelableArrayList("dummyData", dummyData as java.util.ArrayList<out Parcelable>)
                }

        }
    }
}