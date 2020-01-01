package com.tistory.comfy91.excuseme_android.feature.helper_sort

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.DataHelperSortCard

class RvHelperSortAdapter(private val context: Context, private val onBtnAllClicked: ()-> Unit, private val bind: Int): RecyclerView.Adapter<HelperSortCardViewHolder>(),
    Filterable {
    var data = arrayListOf<CardBean>()
    var isChanged = false
    var searchedList: ArrayList<CardBean> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperSortCardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.helper_sort_item_card, parent, false)

        return HelperSortCardViewHolder(
            view,
            onBtnAllClicked
        )
    }

    override fun getItemCount(): Int {
        Log.d("ziwon","searchedList!!.size : " + searchedList!!.size)
        Log.d("ziwon","data!!.size : " + data.size)
        return searchedList!!.size
    }

    override fun onBindViewHolder(holder: HelperSortCardViewHolder, position: Int) {
        holder.bind(searchedList!![position], position, bind)
        holder.dataVisibilityChange = {
            searchedList!![position].visibility = !(searchedList!![position].visibility)
            this.notifyDataSetChanged()
        }
    }

    // 리사이클러뷰의 아이템의 위치를 사용자가 지정한 곳으로 바꿈
    fun swapItems(fromPosition: Int, toPosition: Int){
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                searchedList!![i] = searchedList!!.set(i+1, searchedList!![i])
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                searchedList!![i] = searchedList!!.set(i-1, searchedList!![i])
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        isChanged = true
    }

    // init
    init {
        Log.d("ziwon","init")
        this.searchedList = data
    }

    //for filter
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    searchedList = data
                } else {
                    val filteredList = ArrayList<CardBean>()
                    //이부분에서 원하는 데이터를 검색할 수 있음
                    for (row in data) {

                        Log.d("search1","search data :  " + row.title.toLowerCase())
                        Log.d("search2","input data " + charString.toLowerCase())
                        if (row.title.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    searchedList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = searchedList
                return filterResults
            }
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                Log.d("search3","publishResults")
                searchedList = filterResults.values as ArrayList<CardBean>
                notifyDataSetChanged()
            }
        }
    }
}