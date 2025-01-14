package com.tistory.comfy91.excuseme_android.feature.helper

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean

class SelectSortAdapter(private val context: Context, private val onBtnAllClicked: ()-> Unit, private val bind: Int): RecyclerView.Adapter<SelectSortCardViewHolder>(),
    Filterable {
    var data = arrayListOf<CardBean>()
    var isChanged = false
    var searchedList: ArrayList<CardBean> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectSortCardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.select_sort_item_card, parent, false)

        return SelectSortCardViewHolder(
            view,
            onBtnAllClicked
        )
    }

    override fun getItemCount(): Int {
        return searchedList.size
    }

    override fun onBindViewHolder(holder: SelectSortCardViewHolder, position: Int) {
        holder.bind(searchedList[position], position, bind)
    }

    fun swapItems(fromPosition: Int, toPosition: Int){
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                searchedList[i] = searchedList.set(i+1, searchedList[i])
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                searchedList[i] = searchedList.set(i-1, searchedList[i])
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        isChanged = true
    }

    init {
        this.searchedList = data
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    searchedList = data
                } else {
                    val filteredList = ArrayList<CardBean>()

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