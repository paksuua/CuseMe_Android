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

class RealHelperSortAdapter(
    private val context: Context,
    private val onBtnAllClicked: ()-> Unit,
    private val bind: Int)
    : RecyclerView.Adapter<HelperSortCardViewHolder>(){
    var data = arrayListOf<CardBean>()
    var isChanged = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperSortCardViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.helper_sort_item_card, parent, false)

        return HelperSortCardViewHolder(
            view,
            onBtnAllClicked
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HelperSortCardViewHolder, position: Int) {
        holder.bind(data[position], position, bind)
        holder.dataVisibilityChange = {
            data[position].visibility = !(data[position].visibility)
            this.notifyDataSetChanged()
        }
    }


    fun swapItems(fromPosition: Int, toPosition: Int){


        notifyItemMoved(fromPosition, toPosition)
        isChanged = true
    }


}