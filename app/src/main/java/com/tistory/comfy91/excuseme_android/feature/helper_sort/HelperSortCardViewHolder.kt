package com.tistory.comfy91.excuseme_android.feature.helper_sort

import android.view.View
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.DataHelperSortCard

class HelperSortCardViewHolder(itemView: View, private val onClicked: () -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val imgCard: ImageView = itemView.findViewById(R.id.imgCard)
    private val tvCard: TextView = itemView.findViewById(R.id.tvCard)
    private val ctvCheck: CheckedTextView = itemView.findViewById(R.id.ctvCheck)
    lateinit var dataVisibilityChange: () -> Unit

    private val clickListener = View.OnClickListener {
        clicked()
    }

    fun bind(data: DataHelperSortCard, position: Int, listenerFlag: Int) {
        Glide.with(itemView).load(data.imageUrl).into(imgCard)
        tvCard.text = data.title
        ctvCheck.isChecked = data.visibility

        // 체크 박스 토글
        when(listenerFlag){
            HELPER_SORT_ACTIVITY -> itemView.setOnClickListener(clickListener)
            SELECT_SORT_FRAGMENT -> ctvCheck.setOnClickListener(clickListener)
        }

    }

    private fun clicked(){
        ctvCheck.toggle()
        if (ctvCheck.isChecked) {
            // 삭제대상으로 선택되었을 때
            Toast.makeText(itemView.context, "삭제", Toast.LENGTH_SHORT).show()
        } else {
            // 삭제대상으로 해제되었을 때
            Toast.makeText(itemView.context, "해제", Toast.LENGTH_SHORT).show()
        }
        dataVisibilityChange()
        onClicked()
    }

    companion object{
        const val HELPER_SORT_ACTIVITY = 1
        const val SELECT_SORT_FRAGMENT = 2
    }
}