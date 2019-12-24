package com.tistory.comfy91.excuseme_android

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val imgCard: ImageView = itemView.findViewById(R.id.imgCard)
    private val tvCard: TextView = itemView.findViewById(R.id.tvCard)

    fun bind(data: DataCard){
        Glide.with(itemView).load(data.imageUrl).into(imgCard)
        tvCard.text = data.text
    }
}