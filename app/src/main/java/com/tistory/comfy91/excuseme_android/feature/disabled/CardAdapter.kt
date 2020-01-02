package com.tistory.comfy91.excuseme_android.feature.disabledimport android.util.Logimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport android.widget.*import androidx.constraintlayout.widget.ConstraintLayoutimport androidx.recyclerview.widget.RecyclerViewimport com.bumptech.glide.Glideimport com.tistory.comfy91.excuseme_android.data.CardBeanimport com.tistory.comfy91.excuseme_android.data.ResCardsimport com.tistory.comfy91.excuseme_android.data.SingletoneTokenimport com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepositoryimport com.tistory.comfy91.excuseme_android.feature.login.Loginimport com.tistory.comfy91.excuseme_android.logDebugimport com.tistory.comfy91.excuseme_android.setOnSingleClickListenerimport retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseclass CardAdapter(//    private val listener: CardViewHolder.ItemClickListener) :    RecyclerView.Adapter<CardAdapter.CardViewHolder>(), Filterable {    var data = arrayListOf<CardBean>()    var searchedList: ArrayList<CardBean>? = null    private val cardDataRepository = ServerCardDataRepository()    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CardViewHolder(parent)    override fun getItemCount(): Int {        return searchedList!!.size    }    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {        holder.bind(searchedList!!.get(position))    }    // init    init {        this.searchedList = data    }    //for filter    override fun getFilter(): Filter {        return object : Filter() {            override fun performFiltering(charSequence: CharSequence): FilterResults {                val charString = charSequence.toString()                if (charString.isEmpty()) {                    searchedList = data                } else {                    val filteredList = ArrayList<CardBean>()                    //원하는 데이터를 검색                    for (row in data) {                        Log.d("search1", "search data :  " + row.title.toLowerCase())                        Log.d("search2", "input data " + charString.toLowerCase())                        if (row.title.toLowerCase().contains(charString.toLowerCase())) {                            Log.d("search3", "addddd ")                            filteredList.add(row)                        }                    }                    searchedList = filteredList                }                val filterResults = FilterResults()                filterResults.values = searchedList                return filterResults            }            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {                Log.d("search4", "publishResults ")                searchedList = filterResults.values as ArrayList<CardBean>                notifyDataSetChanged()            }        }    }    inner class CardViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(        LayoutInflater.from(parent.context).inflate(com.tistory.comfy91.excuseme_android.R.layout.disabled_item_card, parent, false)    ) {        //private val card_view: MaterialCardView = itemView.findViewById(R.id.card_view)        private val panel: ConstraintLayout = itemView.findViewById(com.tistory.comfy91.excuseme_android.R.id.panel)        private val imgCard: ImageView = itemView.findViewById(com.tistory.comfy91.excuseme_android.R.id.imgCard)        private val tvCardTitle: TextView = itemView.findViewById(com.tistory.comfy91.excuseme_android.R.id.tvCardTitle)        fun bind(data: CardBean) {            Glide.with(itemView).load(data.imageUrl).into(imgCard)            tvCardTitle.text = data.title//            itemView.setOnSingleClickListener {//                val intent = Intent(itemView.context, DetailCardActivity::class.java)//                intent.putParcelableArrayListExtra("DATA_CARD", arrayListOf(data))//                itemView.context.startActivity(intent)//            }            itemView.setOnSingleClickListener {                countUp(data, itemView)            }        }    }    private fun countUp(data: CardBean, itemView: View){        data.count++        val uuid = Login.getUUID(itemView.context)        var token = SingletoneToken.getInstance().token        if(token == null){            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWR4IjozOSwidXVpZCI6ImYzZDViM2E1LTkwYjYtNDVlMy1hOThhLTEyODE5OWNmZTg1MCIsImlhdCI6MTU3NzkwMTA1MywiZXhwIjoxNTc3OTg3NDUzLCJpc3MiOiJnYW5naGVlIn0.QytUhsXf4bJirRR_zF3wdACiNu9ytwUE4mrPSNLCFLk"        }        cardDataRepository.incCardCount(token, data.cardIdx.toString())            .enqueue(object: Callback<ResCards>{                override fun onFailure(call: Call<ResCards>, t: Throwable) {                    "Fail Inc Card Data, message: ${t.message}".logDebug(this@CardAdapter)                }                override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {                    "code : ${response.code()}, message : ${response.message()}"                    if(response.isSuccessful){                        response.body()?.let{                            "status : ${it.status}, succces : ${it.success}, message : ${it.message}".logDebug(this@CardAdapter)                            "Succss 카드 클릭 증가".logDebug(this@CardAdapter)                        }                    }                    else{                        "response is Not Successfus".logDebug(this@CardAdapter)                    }                }            })    }    interface ItemClickListener {        fun onItemClicked(itemCard: ItemCard)    }}