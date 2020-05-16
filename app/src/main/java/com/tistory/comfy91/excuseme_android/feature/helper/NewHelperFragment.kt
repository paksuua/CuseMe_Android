package com.tistory.comfy91.excuseme_android.feature.helper


import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.answer.ResCards
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.data.request.BodyGetDisabledCard
import com.tistory.comfy91.excuseme_android.data.server.BodyChangeVisibility
import com.tistory.comfy91.excuseme_android.feature.TTS
import com.tistory.comfy91.excuseme_android.feature.detailcard.DetailCardActivity
import com.tistory.comfy91.excuseme_android.feature.helper_sort.HelperSortActivity
import com.tistory.comfy91.excuseme_android.feature.login.Login
import com.tistory.comfy91.excuseme_android.logDebug
import kotlinx.android.synthetic.main.activity_helper.*
import kotlinx.android.synthetic.main.fragment_new_helper.*
import kotlinx.android.synthetic.main.helper_item_card.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.collections.ArrayList

class NewHelperFragment : Fragment() {
    private var disabledCardList: ArrayList<CardBean> = arrayListOf()
    private var rvAdapter = NewHelperAdapter()
    private val cardDataRepository = ServerCardDataRepository()
    var clickedCardView: ConstraintLayout? = null
    private var clickedCardData: CardBean? = null
    private var token = SingletoneToken.getInstance().token
    private var fragmentContext: Context? = null

    // audio
    private lateinit var tts: TextToSpeech
    private var player: MediaPlayer? = null
    private var playFlag = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_helper, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
        initUi()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = activity?.baseContext

    }

    override fun onResume() {
        super.onResume()
        getDisabledCards()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
        stopPlaying()
    }

    private fun initData() {
        tts = TTS.getInstance(activity!!.applicationContext)

    }

    private fun initUi() {

        // DisabledAvtivity로 이동
        btnNewHelperUnlock?.setOnClickListener {
            activity?.finish()
        }

        // HelperSortActivity로 이동
        btnNewHelperGoSort.setOnClickListener {
            activity?.let {
                val intent = Intent(it.baseContext, HelperSortActivity::class.java)
                intent.putExtra("CARD_DATA", disabledCardList)
                it.startActivity(intent)
            }
        }

        // 하단바 카드삭제
        btnNewHelperDeleteCard.setOnClickListener {
            // 삭제를 물어보는 다이얼로그 띄움
            showDeleteDialog()
        }

        // 하단바 카드숨기기
        btnNewHelperInvisCard.setOnClickListener {
            // 카드 숨기기 api
            hideCard()

            // 카드 리스트
            getDisabledCards()
        }

        // 하단바 수정
        btnNewHelperModCard.setOnClickListener {
            activity?.let {
                val intent = Intent(it, DetailCardActivity::class.java)
                intent.putExtra("FROM_NEW_HELPER", clickedCardData)
                it.startActivity(intent)
            }
        }

        // 하단바 취소
        btnNewHelperCancleCard.setOnClickListener {
            // TODO: 선택한 카드 취소
            cstNewHelperSecond.isVisible = false
            clickedCardData = null
            clickedCardView?.isSelected = false
            clickedCardView = null
            tvNewHelper.text = ""
            bottomBarIsVisible(false)
        }

        // HelperAdapter 초기화
        rvNewHelperCard.apply {
            adapter = rvAdapter
            layoutManager = GridLayoutManager(this@NewHelperFragment.context, 2)
        }

        rvAdapter.data = disabledCardList
        rvAdapter.notifyDataSetChanged()
    }

    private fun getDisabledCards() {
        if (token == null) {
            token =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWR4IjozOSwidXVpZCI6ImYzZDViM2E1LTkwYjYtNDVlMy1hOThhLTEyODE5OWNmZTg1MCIsImlhdCI6MTU3NzkwMTA1MywiZXhwIjoxNTc3OTg3NDUzLCJpc3MiOiJnYW5naGVlIn0.QytUhsXf4bJirRR_zF3wdACiNu9ytwUE4mrPSNLCFLk"
        }
        cardDataRepository
            .getDisabledCards(
                BodyGetDisabledCard(
                    Login.getUUID(fragmentContext)
                )
            )
            .enqueue(object : Callback<ResCards> {
                override fun onFailure(call: Call<ResCards>, t: Throwable) {
                    "Fail to Get All Card Data message:${t.message}".logDebug(this@NewHelperFragment)
                }

                override fun onResponse(
                    call: Call<ResCards>,
                    response: Response<ResCards>
                ) {
                    if (response.isSuccessful) {
                        response.body()!!.let { res ->
                            "success: ${res.success} status: ${res.status}, data: ${res.data}, message: ${res.message}".logDebug(
                                this@NewHelperFragment
                            )

                            for (i in 0 until res.data?.size!!) {
                                "for recieved.data index : $i: card : ${res.data[i]}".logDebug(this@NewHelperFragment)
                            }
                            when (res.success) {
                                true -> {
                                    backgroundIsVisible(res.data.isNullOrEmpty())
                                    disabledCardList.clear()
                                    val sortedCards = res.data.sortedBy { card -> card.sequence }
                                    disabledCardList.addAll(sortedCards)
                                    rvAdapter.notifyDataSetChanged()

                                }
                                false -> {
                                    "Get All Card Data Response is not Success".logDebug(this@NewHelperFragment)
                                    disabledCardList.clear()
                                    rvAdapter.notifyDataSetChanged()
                                    backgroundIsVisible(true)
                                }
                            }
                        }
                    } else {
                        response.body()?.let { it ->
                            "success: ${it.success} status: ${it.status}, data: ${it.data}, message: ${it.message}".logDebug(
                                this@NewHelperFragment
                            )
                        }
                        "resonser is not success".logDebug(this@NewHelperFragment)
                    }
                }

            })
    }


    private fun backgroundIsVisible(visible: Boolean) {
        imgNewHelperCard.isVisible = visible
        tvNewHelperCard.isVisible = visible
        imgNewEmpty.isVisible = visible
    }

    // 카드 삭제 api 호출
    private fun deleteCard() {
        SingletoneToken
            .getInstance()
            .token?.let { token ->
                cardDataRepository.deleteCard(
                    token,
                    clickedCardData?.cardIdx.toString()
                ).enqueue(object : Callback<ResCards> {
                    override fun onFailure(call: Call<ResCards>, t: Throwable) {
                        "Fail Delete Card, message : ${t.message}".logDebug(this@NewHelperFragment)
                    }

                    override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                        "response code : ${response.code()}, message: ${response.message()}".logDebug(
                            this@NewHelperFragment
                        )
                        if (response.isSuccessful) {
                            response.body()
                                ?.let {
                                    "status : ${it.status}, success : ${it.success}, message : ${it.message}".logDebug(
                                        this@NewHelperFragment
                                    )
                                    onResume()
                                }
                        } else {
                            "resonse is Not Success = Body is Empty".logDebug(this@NewHelperFragment)
                        }
                    }

                })
            }
    }

    // 카드 숨김 api 호출
    private fun hideCard() {
        SingletoneToken
            .getInstance()
            .token?.let { token ->
                cardDataRepository.changeVisibilty(
                    token,
                    BodyChangeVisibility(!(clickedCardData!!.visibility)),
                    clickedCardData?.cardIdx.toString()
                ).enqueue(object : Callback<ResCards> {
                    override fun onFailure(call: Call<ResCards>, t: Throwable) {
                        "Fail Delete Card, message : ${t.message}".logDebug(this@NewHelperFragment)
                    }

                    override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                        "response code : ${response.code()}, message: ${response.message()}".logDebug(
                            this@NewHelperFragment
                        )
                        if (response.isSuccessful) {
                            response.body()
                                ?.let {
                                    "status : ${it.status}, success : ${it.success}, message : ${it.message}".logDebug(
                                        this@NewHelperFragment
                                    )
                                    onResume()
                                }
                        } else {
                            "hide response is Not Success = Body is Empty".logDebug(this@NewHelperFragment)
                        }
                    }

                })
            }
    }

    fun showDeleteDialog() {
        "Clicked Card Data : $clickedCardData".logDebug(this@NewHelperFragment)

        this@NewHelperFragment.context
            ?.let {
                AlertDialog.Builder(it)
                    .apply {
                        this.setMessage("카드를 완전히\n삭제하시겠습니까?")
                        this.setPositiveButton(
                            "삭제"
                        ) { dialogue, _ ->
                            deleteCard()
                            bottomBarIsVisible(false)
                        }

                        this.setNegativeButton(
                            "취소"
                        ) { dialogue, _ ->
                            dialogue.dismiss()
                        }
                    }
                    .show()
            }

    }

    private fun play() {
        onPlay(!playFlag)
        playFlag != playFlag
    }

    private fun onPlay(playFlag: Boolean) {
        if (playFlag) startPlaying() else stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(clickedCardData?.audioUrl)
                prepare()
                start()
            } catch (e: IOException) {
                "prepare() failed".logDebug(this@NewHelperFragment)
            }
        }
    }

    fun stopPlaying() {
        player?.release()
        player = null
    }


    private fun bottomBarIsVisible(visible: Boolean) {
        cstNewHelperSecond.isVisible = visible
        btnNewHelperDeleteCard.isVisible = visible
        btnNewHelperInvisCard.isVisible = visible
        btnNewHelperModCard.isVisible = visible
        btnNewHelperCancleCard.isVisible = visible
        (this@NewHelperFragment.context as HelperActivity).cstHelperBottom.isVisible = !visible
        (this@NewHelperFragment.context as HelperActivity).btnHelperAddCard.isVisible = !visible
    }

    companion object {
        fun newInstance(diabledCardList: ArrayList<CardBean>) =
            NewHelperFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(
                        "DISABLED_CARD",
                        diabledCardList as java.util.ArrayList<out Parcelable>
                    )
                }
            }
    }


    // region RecyclerView Adapter
    inner class NewHelperAdapter : RecyclerView.Adapter<NewViewHolder>() {
        var data = arrayListOf<CardBean>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewViewHolder {
            val view = LayoutInflater.from(this@NewHelperFragment.context)
                .inflate(R.layout.helper_item_card, parent, false)
            return NewViewHolder(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: NewViewHolder, position: Int) {
            holder.bind(data[position])
        }

    }

    // end Adapter
// region ViewHolder
    inner class NewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lyNewHelperCard: ConstraintLayout = itemView.findViewById(R.id.lyHelper)
        private val imgNewHelperCard: ImageView = itemView.findViewById(R.id.imgCard)
        private val tvNewHelperCard: TextView = itemView.findViewById(R.id.tvCard)

        fun bind(cardBean: CardBean) {
            lyNewHelperCard.setBackgroundResource(R.drawable.card_bg_selector)
            Glide.with(itemView)
                .load(cardBean.imageUrl)
                .into(imgNewHelperCard)
            tvNewHelperCard.text = cardBean.title

            itemView.setOnClickListener {
                clickedCardData = cardBean
                clickedCardView?.isSelected = false

                if (clickedCardView == it.lyHelper) {
                    clickedCardView = null
                    tvNewHelper.text = ""
                    bottomBarIsVisible(false)
                    tts.stop()
                    onStop()
                    return@setOnClickListener
                }
                clickedCardView = it.lyHelper
                tvNewHelper.text = cardBean.desc
                it.lyHelper.isSelected = true
                bottomBarIsVisible(true)

                if (clickedCardData?.audioUrl.isNullOrEmpty()) {
                    tts.speak(cardBean.desc, TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    play()
                }
            }
        }
    }
//endregion
}
