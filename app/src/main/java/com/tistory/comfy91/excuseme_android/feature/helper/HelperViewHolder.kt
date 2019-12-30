package com.tistory.comfy91.excuseme_android.feature.helper

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.DetailCardActivity
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.DataHelperCard
import com.tistory.comfy91.excuseme_android.data.DataHelperSortCard
import com.tistory.comfy91.excuseme_android.newStartActivity
import com.tistory.comfy91.excuseme_android.setOnSingleClickListener

class HelperViewHolder(itemView: View, private val onClicked: (String) -> Unit): RecyclerView.ViewHolder(itemView){
    private val imgCard: ImageView = itemView.findViewById(R.id.imgHelperCard)
    private val tvCard: TextView = itemView.findViewById(R.id.tvHelperCard)

    lateinit var dataVisibilityChange: ()-> Unit

    fun bind(data: DataHelperCard, listenerFlag: Int){
        Glide.with(itemView).load(data.imageUrl).into(imgCard)
        tvCard.text = data.title
        itemView.setOnClickListener {
            onClicked(data.desc)
        }

       /* when(listenerFlag){
            HELPER_SORT_ACTIVITY -> itemView.setOnSingleClickListener{clicked()}
            SELECT_SORT_FRAGMENT -> {
                //ctvCheck.setOnSingleClickListener{clicked()}
                itemView.setOnSingleClickListener{itemView.context.newStartActivity(
                    DetailCardActivity::class.java)}
            }
        }*/
    }

    private fun clicked(){
        dataVisibilityChange() // 데이터 변경
        //onClicked(data.desc)
    }

    companion object{
        const val HELPER_SORT_ACTIVITY = 1
        const val SELECT_SORT_FRAGMENT = 2
    }
}