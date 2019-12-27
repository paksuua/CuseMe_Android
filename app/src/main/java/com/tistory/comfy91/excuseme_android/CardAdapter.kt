package com.tistory.comfy91.excuseme_android

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView


class CardAdapter(private val ItemCardList: ArrayList<ItemCard>) :
    RecyclerView.Adapter<CardViewHolder>(), Filterable {


    var data = arrayListOf<ItemCard>()

    //필터 전 리스트 = 전체 리스트
    val unFilteredList = ItemCardList

    //필터 중 리스트
    var filteredList = ItemCardList

    //var data = arrData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view);
    }

    //fun getItemCount(data): Int {
    //return data.size
    //}

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(data.get(position))
    }


    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                filteredList = if (charString.isEmpty()) { //⑶
                    unFilteredList
                } else {
                    var filteringList = ArrayList<ItemCard>()
                    for (item in unFilteredList) {
                        if (item.title == charString) filteringList.add(item)
                    }
                    filteringList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList =
                    results?.values as ArrayList<ItemCard>
                notifyDataSetChanged()
            }
        }
    }


}
