package com.tistory.comfy91.excuseme_android.feature.helper

import android.content.Intent
import android.view.View
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

class SelectSortCardViewHolder(itemView: View, private val onClicked: () -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val imgCard: ImageView = itemView.findViewById(R.id.imgCardSelect)
    private val tvCard: TextView = itemView.findViewById(R.id.tvCardSelect)
    private val btnHelperCheck: CheckedTextView = itemView.findViewById(R.id.btnCheckSelect)
    private var cardList: ArrayList<CardBean> = arrayListOf()
    private var token = SingletoneToken.getInstance().token
    private var isTrueBtnHelperCheck = false
    private val cardDataRepository= ServerCardDataRepository()
    lateinit var dataVisibilityChange: () -> Unit



    fun bind(data: CardBean, position: Int, listenerFlag: Int) {
        Glide.with(itemView).load(data.imageUrl).into(imgCard)
        tvCard.text = data.title

        btnHelperCheck.isChecked = data.visibility

        when(listenerFlag){
            HELPER_SORT_ACTIVITY -> itemView.setOnSingleClickListener{clicked()}
            SELECT_SORT_FRAGMENT -> {
                btnHelperCheck.setOnClickListener{
                    "셀렉트 시작".logDebug(this@SelectSortCardViewHolder)
                    changeCardVisibility(data.visibility, data.cardIdx.toString())
                    //(it as CheckedTextView).toggle()
                    "셀렉트 끝".logDebug(this@SelectSortCardViewHolder)
                }
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

    private fun changeCardVisibility(visibility: Boolean, cardIdx:String){
        if(token==null){
            token=""
        }
        "Token: $token".logDebug(this@SelectSortCardViewHolder)

        cardDataRepository.changeVisibilty(token!!, BodyChangeVisibility(!visibility), cardIdx).enqueue(object :
            Callback<ResCards>{
            override fun onFailure(call: Call<ResCards>, t: Throwable) {
                "Fail to Edit Card Visibility, message : ${t.message}".logDebug(this@SelectSortCardViewHolder)
            }

            override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                if(response.isSuccessful){
                    val res=response.body()
                    btnHelperCheck.isChecked=!btnHelperCheck.isChecked
                    "status2: ${res!!.status} success: ${res!!.success} message: ${res!!.message}".logDebug(this@SelectSortCardViewHolder)
                }
            }
        })
    }

    companion object{
        const val HELPER_SORT_ACTIVITY = 1
        const val SELECT_SORT_FRAGMENT = 2
    }
}