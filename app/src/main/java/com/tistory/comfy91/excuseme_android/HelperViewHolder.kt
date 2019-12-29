package com.tistory.comfy91.excuseme_android

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.data.DataHelperCard

class HelperViewHolder(itemView: View, private val onClicked: (String) -> Unit): RecyclerView.ViewHolder(itemView){
    private val imgCard: ImageView = itemView.findViewById(R.id.imgCard)
    private val tvCard: TextView = itemView.findViewById(R.id.tvCard)

    lateinit var dataVisibilityChange: ()-> Unit

    fun bind(data: DataHelperCard){
        Glide.with(itemView).load(data.imageUrl).into(imgCard)
        tvCard.text = data.title
        itemView.setOnClickListener {
            onClicked(data.desc)
        }
    }


}