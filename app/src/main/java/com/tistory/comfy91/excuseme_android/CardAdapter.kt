package com.tistory.comfy91.excuseme_android

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private val context: Context, private val onBtnAllClicked: ()-> Unit): RecyclerView.Adapter<CardViewHolder>(){
    var data = arrayListOf<DataCard>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)

        return CardViewHolder(view, onBtnAllClicked)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(data.get(position), position)
        holder.dataVisibilityChange = {
            data[position].visibility = !(data[position].visibility)
        }
    }

    // 리사이클러뷰의 아이템의 위치를 사용자가 지정한 곳으로 바꿈
    fun swapItems(fromPosition: Int, toPosition: Int){
        if (fromPosition < toPosition) {
            for (i in fromPosition..toPosition - 1) {
                data.set(i, data.set(i+1, data.get(i)))
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                data.set(i, data.set(i-1, data.get(i)))
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }




}