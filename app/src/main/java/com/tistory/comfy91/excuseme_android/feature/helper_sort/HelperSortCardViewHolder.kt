package com.tistory.comfy91.excuseme_android.feature.helper_sort

import android.view.View
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.DetailCardActivity
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.DataHelperSortCard
import com.tistory.comfy91.excuseme_android.newStartActivity
import com.tistory.comfy91.excuseme_android.setOnSingleClickListener

class HelperSortCardViewHolder(itemView: View, private val onClicked: () -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val imgCard: ImageView = itemView.findViewById(R.id.imgCard)
    private val tvCard: TextView = itemView.findViewById(R.id.tvCard)
    private val ctvCheck: CheckedTextView = itemView.findViewById(R.id.ctvCheck)
    lateinit var dataVisibilityChange: () -> Unit



    fun bind(data: DataHelperSortCard, position: Int, listenerFlag: Int) {
        Glide.with(itemView).load(data.imageUrl).into(imgCard)
        tvCard.text = data.title
        ctvCheck.isChecked = data.visibility


        when(listenerFlag){
            HELPER_SORT_ACTIVITY -> itemView.setOnSingleClickListener{clicked()}
            SELECT_SORT_FRAGMENT -> {
                ctvCheck.setOnSingleClickListener{clicked()}
                itemView.setOnSingleClickListener{itemView.context.newStartActivity(DetailCardActivity::class.java)}
            }
        }

    }

    private fun clicked(){
        ctvCheck.toggle()
        dataVisibilityChange() // 데이터 변경
        onClicked()
    }

    companion object{
        const val HELPER_SORT_ACTIVITY = 1
        const val SELECT_SORT_FRAGMENT = 2
    }
}