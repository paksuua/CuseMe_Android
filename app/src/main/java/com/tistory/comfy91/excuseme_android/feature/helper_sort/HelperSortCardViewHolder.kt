package com.tistory.comfy91.excuseme_android.feature.helper_sort

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.tistory.comfy91.excuseme_android.feature.detailcard.DetailCardActivity
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.answer.ResCards
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.data.server.BodyChangeVisibility
import com.tistory.comfy91.excuseme_android.logDebug
import com.tistory.comfy91.excuseme_android.setOnSingleClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HelperSortCardViewHolder(itemView: View, private val onClicked: () -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val imgCard: ImageView = itemView.findViewById(R.id.imgCard)
    private val tvCard: TextView = itemView.findViewById(R.id.tvCard)
    private val btnHelperCheck: CheckedTextView = itemView.findViewById(R.id.btnCheck)
    lateinit var dataVisibilityChange: () -> Unit



    fun bind(data: CardBean, position: Int, listenerFlag: Int) {
        Glide.with(itemView).load(data.imageUrl).into(imgCard)
        tvCard.text = data.title

        btnHelperCheck.isChecked = data.visibility
        btnHelperCheck.setOnClickListener{
        }

        when(listenerFlag){
            HELPER_SORT_ACTIVITY -> itemView.setOnSingleClickListener{clicked()}
            SELECT_SORT_FRAGMENT -> {
                btnHelperCheck.setOnSingleClickListener{clicked()}
                itemView.setOnSingleClickListener{
                    val intent = Intent(itemView.context, DetailCardActivity::class.java)
                    intent.putExtra("CARD_DATA", data)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    private fun clicked(){
        //todo("btn 글씨 바꿔야함")
        dataVisibilityChange()
        onClicked()
    }

    companion object{
        const val HELPER_SORT_ACTIVITY = 1
        const val SELECT_SORT_FRAGMENT = 2
    }
}