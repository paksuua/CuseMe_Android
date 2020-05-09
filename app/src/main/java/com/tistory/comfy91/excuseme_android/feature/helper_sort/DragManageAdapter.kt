package com.tistory.comfy91.excuseme_android.feature.helper_sortimport android.util.Logimport androidx.recyclerview.widget.ItemTouchHelperimport androidx.recyclerview.widget.RecyclerViewimport com.tistory.comfy91.excuseme_android.logDebug/** * Using in HelperSortActivity * For Drag & Drop */class DragManageAdapter(adapter: RealHelperSortAdapter, dragDirs: Int, swipeDirs: Int) :    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {    var cardAdapter = adapter    private var fromPosition = -1    private var toPosition = -1    var cardData = cardAdapter.data    override fun getDragDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {        val direction = super.getDragDirs(recyclerView, viewHolder)        fromPosition = viewHolder.adapterPosition        Log.d("adapter", viewHolder.adapterPosition.toString())        "getSwipeDirs(): $direction".logDebug(this)        return direction    }    override fun getSwipeDirs(        recyclerView: RecyclerView,        viewHolder: RecyclerView.ViewHolder    ): Int {        val direction = super.getSwipeDirs(recyclerView, viewHolder)        fromPosition = viewHolder.adapterPosition        Log.d("adapter", viewHolder.adapterPosition.toString())        "getSwipeDirs(): ${super.getSwipeDirs(recyclerView, viewHolder)}"        return direction    }    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {        // nope    }    override fun onMove(        recyclerView: RecyclerView,        viewHolder: RecyclerView.ViewHolder,        target: RecyclerView.ViewHolder    ): Boolean {        fromPosition = viewHolder.adapterPosition        toPosition = target.adapterPosition        cardAdapter.notifyItemMoved(fromPosition, toPosition)//        cardAdapter.swapItems(fromPosition, toPosition)        return true    } // end onMove()    override fun getMovementFlags(        recyclerView: RecyclerView,        viewHolder: RecyclerView.ViewHolder    ): Int {        return makeFlag(            ItemTouchHelper.ACTION_STATE_DRAG,            ItemTouchHelper.RIGHT                .or(ItemTouchHelper.LEFT)                .or(ItemTouchHelper.UP)                .or(ItemTouchHelper.DOWN)        )    }    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {        super.clearView(recyclerView, viewHolder)        Log.d("toPosition", toPosition.toString())        Log.d("fromPosition", fromPosition.toString())        "beforeSwap: $cardData".logDebug(this)        if (fromPosition < toPosition) {            for (i in fromPosition until toPosition) {                cardData[i] = cardData.set(i + 1, cardData[i])            }        } else {            for (i in fromPosition..toPosition + 1) {                cardData[i] = cardData.set(i - 1, cardData!![i])            }        }        "afterSwap: $cardData".logDebug(this)    }} // end class