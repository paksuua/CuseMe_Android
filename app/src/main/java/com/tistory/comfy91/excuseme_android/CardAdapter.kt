package com.tistory.comfy91.excuseme_android

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private val context: Context): RecyclerView.Adapter<CardViewHolder>(){
    var data = listOf<DataCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view);
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(data.get(position))
    }

}