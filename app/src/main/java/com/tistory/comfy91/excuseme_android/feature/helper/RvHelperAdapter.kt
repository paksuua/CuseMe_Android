package com.tistory.comfy91.excuseme_android.feature.helper


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.DataHelperCard

class RvHelperAdapter(private val context: Context, private val onBtnAllClicked: (String)-> Unit): RecyclerView.Adapter<HelperViewHolder>(){
    var data = arrayListOf<DataHelperCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sy_item_card, parent, false)

        return HelperViewHolder(
            view,
            onBtnAllClicked
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HelperViewHolder, position: Int) {
        holder.bind(data[position], position)
        holder.dataVisibilityChange = {
            data[position].visibility = !(data[position].visibility)
        }
    }
}