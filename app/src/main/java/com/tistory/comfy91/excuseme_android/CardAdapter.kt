package com.tistory.comfy91.excuseme_android

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class CardAdapter(private val arrData: ArrayList<ItemCard>): RecyclerView.Adapter<CardViewHolder>(){

        var data = arrData

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_card, parent, false)
            return CardViewHolder(view);
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            holder.bind(data.get(position))
        }

    }

