package com.tistory.comfy91.excuseme_android.feature.disabledimport android.os.Handlerimport android.os.SystemClockimport android.util.Logimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport android.widget.*import androidx.constraintlayout.widget.ConstraintLayoutimport androidx.recyclerview.widget.RecyclerViewimport com.bumptech.glide.Glideimport com.tistory.comfy91.excuseme_android.Rimport com.tistory.comfy91.excuseme_android.data.CardBeanimport com.tistory.comfy91.excuseme_android.setOnSingleClickListenerclass CardAdapter() : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {    var data = ArrayList<CardBean>()    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CardViewHolder(parent)    override fun getItemCount(): Int {        return data.size    }    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {//        holder.bind(data.get(position))        holder.bind(data.get(position))    }    inner class CardViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(        LayoutInflater.from(parent.context).inflate(com.tistory.comfy91.excuseme_android.R.layout.disabled_item_card, parent, false)    ) {        //private val card_view: MaterialCardView = itemView.findViewById(R.id.card_view)        private val panel: ConstraintLayout = itemView.findViewById(com.tistory.comfy91.excuseme_android.R.id.panel)        private val imgCard: ImageView = itemView.findViewById(com.tistory.comfy91.excuseme_android.R.id.imgCard)        private val tvCardTitle: TextView = itemView.findViewById(com.tistory.comfy91.excuseme_android.R.id.tvCardTitle)        //private val tvDisabledShowCardText: TextView=itemView.findViewById(R.id.tvDisabledShowCardText)        fun bind(data: CardBean) {            Glide.with(itemView).load(data.imageUrl).into(imgCard)            tvCardTitle.text = data.title            //tvDisabledShowCardText.text=data.desc            itemView.setOnCardSingleClickListener {                panel.isSelected = !panel.isSelected                Handler().postDelayed(Runnable {                    panel.isSelected = false                },CARD_DELAY_CLICK_TIME)            }        }        fun View.setOnCardSingleClickListener(debounceTime: Long = CARD_DELAY_CLICK_TIME, action: ()->Unit){            this.setOnClickListener (object: View.OnClickListener{                override fun onClick(p0: View?) {                    if((System.currentTimeMillis() - CARD_LAST_CLICK_TIME) < debounceTime){                        Log.d("Single Click", "연속 클릭 발생")                    }else {                        Log.d("Single Click", "action()")                        action()                        CARD_LAST_CLICK_TIME = System.currentTimeMillis()                    }                }            })        }    }    interface ItemClickListener {        fun onItemClicked(itemCard: ItemCard)    }    companion object {        var CARD_LAST_CLICK_TIME : Long = 0        var CARD_DELAY_CLICK_TIME : Long = 5000L    }}