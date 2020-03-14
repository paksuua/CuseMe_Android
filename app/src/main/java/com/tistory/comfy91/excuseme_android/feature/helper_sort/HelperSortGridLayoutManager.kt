package com.tistory.comfy91.excuseme_android.feature.helper_sort

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

public class HelperSortGridLayoutManager(context: Context, spanCount: Int): GridLayoutManager(context, spanCount) {
    override fun supportsPredictiveItemAnimations(): Boolean {
        return true
    }

//    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
//        super.onLayoutChildren(recycler, state)
//    }

}