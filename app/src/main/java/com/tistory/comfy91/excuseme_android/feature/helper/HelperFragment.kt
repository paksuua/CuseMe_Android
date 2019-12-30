package com.tistory.comfy91.excuseme_android.feature.helper


import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.tistory.comfy91.excuseme_android.DetailCardActivity
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.DataHelperCard
import com.tistory.comfy91.excuseme_android.feature.addcard.AddCardActivity
import com.tistory.comfy91.excuseme_android.feature.addcard.AudioTimer
import com.tistory.comfy91.excuseme_android.feature.disabled.DisabledActivity
import com.tistory.comfy91.excuseme_android.feature.helper_sort.HelperSortActivity
import com.tistory.comfy91.excuseme_android.feature.helper_sort.HelperSortCardViewHolder
import com.tistory.comfy91.excuseme_android.feature.helper_sort.RvHelperSortAdapter
import com.tistory.comfy91.excuseme_android.logDebug
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_helper.*
import kotlinx.android.synthetic.main.activity_helper_sort.*
import kotlinx.android.synthetic.main.fragment_helper.*
import kotlinx.android.synthetic.main.fragment_select_sort.*
import java.io.IOException

class HelperFragment() : Fragment() {
    private lateinit var helperAdapter: RvHelperAdapter
    private lateinit var dummyData: ArrayList<DataHelperCard>
    private var fragbottom_flag=true
    private val changeTv: (String) -> Unit = {
        tvHelper.text = it
    }
    val onBtnAllClicked: () -> Unit = {
        btnHelperSortDeleteCard.isVisible = checkAnyCardChecked()
    }
    private var player: MediaPlayer? = null
    private var playFlag = true
    private lateinit var recordFileName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_helper, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        InitUI()

        // DisabledAvtivity로 이동
        btnHelperUnlock.setOnClickListener{
            activity?.let{
                val intent = Intent (it, DisabledActivity::class.java)
                it.startActivity(intent)
            }
        }

        // HelperSortActivity로 이동
        btnHelperGoSort.setOnClickListener{
            activity?.let{
                val intent = Intent (it, HelperSortActivity::class.java)
                it.startActivity(intent)
            }
        }

        // 카드삭제
        btnHelperDeleteCard.setOnClickListener {
            // 삭제를 물어보는 다이얼로그 띄움
            alertDialog(it)
        }

        // 카드 숨기기
        btnHelperInvisCard.setOnClickListener {
            //TODO: HelperSortActivity로 이동
            activity?.let{
                val intent = Intent (it, HelperSortActivity::class.java)
                it.startActivity(intent)
            }
        }

        // 수정
        btnHelperModCard.setOnClickListener {
            //TODO: DetailCardActivity로 이동
            activity?.let{
                val intent = Intent (it, DetailCardActivity::class.java)
                it.startActivity(intent)
            }
        }

        // 취소
        btnHelperCancleCard.setOnClickListener {
            // TODO: 선택한 카드 취소
            setAllCardNotChecked()
        }
    }

    private fun InitUI() {
//        val rvSelectSort = view.findViewById<RecyclerView>(R.id.rvSelectSort)
        //region dummyData

         // 서버에서 HelperActivity로 받은 데이터를 HelperFragment에서 이용
        dummyData =  (activity as HelperActivity).fromServerData
        (activity as HelperActivity).fromServerData=dummyData
           /* arrayListOf(
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "first card",
                true,
                "큐즈밀리 Fragment1"
            ),
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "second card",
                true,
                "큐즈밀리 Fragment2"
            ),
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "third card",
                true,
                "큐즈밀리 Fragment3"
            ),
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "fourth card",
                true,
                "큐즈밀리 Fragment4"
            ),
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "fifth card",
                true,
                "큐즈밀리 Fragment5"
            ),
            DataHelperCard(
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "sixth card",
                true,
                "큐즈밀리 Fragment6"
            )
        )*/
        //endregion

        activity?.let{
            helperAdapter =
                RvHelperAdapter(
                    it.baseContext,
                    changeTv
                )
            rvHelperCard.adapter= helperAdapter
            rvHelperCard.layoutManager = GridLayoutManager(it.baseContext, 2)
        }
        helperAdapter.data = dummyData
        helperAdapter.notifyDataSetChanged()

        /// 여기서 Activity로 bottom_flag값을 넘겨야 하는데 그게 잘 안된단 마리지
        if(checkAnyCardChecked()){
            fragbottom_flag=true
            return
        }else{
            fragbottom_flag=false
            return
        }
        (activity as HelperActivity).bottom_flag=fragbottom_flag
    }

    private fun checkAnyCardChecked(): Boolean{
        var result = false

        for(x in 0 until dummyData.size){
            if(dummyData[x].visibility){
                result = true
                break
            }
        }
        return result
    }



    private fun deleteHelperCard() {
        val it: MutableIterator<DataHelperCard> = dummyData.iterator()
        while(it.hasNext()){
            if(it.next().visibility){
                it.remove()
            }
        }
        helperAdapter.notifyDataSetChanged()
    }

    fun alertDialog(view: View) {

        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle("카드를 완전히\n삭제하시겠습니까?")
        //alertDialog.setMessage("Message")

        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "취소"
        ) { dialog, which -> dialog.dismiss() }

        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "삭제"
        ) { dialog, which -> deleteHelperCard() }
        alertDialog.show()

        val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative.layoutParams = layoutParams
    }

    private fun setAllCardNotChecked(){
        // until : 끝값은 사용하지 않는다.
        for(x in 0 until dummyData.size ){
            dummyData[x].visibility = false
        }
        onBtnAllClicked()
}
    private fun play(){
        onPlay(playFlag)
        tvAddCardRecordNotice.text = when (playFlag) {
            true -> "Stop playing"
            false -> "Start playing"
        }
        playFlag = !playFlag
    }

    private fun onPlay(start: Boolean) = if (start) startPlaying() else stopPlaying()

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(recordFileName)
                prepare()
                start()
                tvAddcardRecordPlay.isChecked = false
            } catch (e: IOException) {
                "prepare() failed".logDebug(this@HelperFragment)
                Log.e(TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    companion object{
        fun newInstance(dummyData: ArrayList<DataHelperCard>)=
            HelperFragment().apply {
                arguments=Bundle().apply {
                    putParcelableArrayList(
                        "dummyData",
                        dummyData as java.util.ArrayList<out Parcelable>
                    )
                }
        }
    }
}