package com.tistory.comfy91.excuseme_android.feature.helper

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.DataHelperCard

class HelperViewHolder(itemView: View, private val onClicked: (Boolean, String, Int) -> Unit): RecyclerView.ViewHolder(itemView){
    private val imgCard: ImageView = itemView.findViewById(R.id.imgHelperCard)
    private val tvCard: TextView = itemView.findViewById(R.id.tvHelperCard)

    lateinit var dataVisibilityChange: ()-> Unit

    fun bind(data: CardBean, position: Int){
        Glide.with(itemView).load(data.imageUrl).into(imgCard)
        tvCard.text = data.title
        itemView.setOnClickListener {
            it.isSelected = !it.isSelected
            onClicked(it.isSelected, data.desc, position)
        }
    }

    private fun clicked(){
        dataVisibilityChange() // 데이터 변경

    }

    companion object{
        const val HELPER_SORT_ACTIVITY = 1
        const val SELECT_SORT_FRAGMENT = 2
    }
}