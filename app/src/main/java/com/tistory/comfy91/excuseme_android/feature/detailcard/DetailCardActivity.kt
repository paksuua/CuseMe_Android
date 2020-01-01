package com.tistory.comfy91.excuseme_android.feature.detailcard

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.*
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.DummyCardDataRepository
import com.tistory.comfy91.excuseme_android.data.server.BodyDeleteCard
import com.tistory.comfy91.excuseme_android.feature.modcard.ModCardActivity
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_detail_card.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DetailCardActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null
    private var playFlag = false
    private lateinit var recordFileName: String
    private var card: CardBean? = null
    private lateinit var dialogBuilder: AlertDialog.Builder
    private val cardDataRepository = DummyCardDataRepository()
    private var token = SingletoneToken.getInstance().token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_card)

        getCard()
        //tvDetailCardDesc.text=descList[0]. 서버에서 desc data 받아옴
        // 음성 데이터 양식 서버에서 받아옴
        /* if(음성 녹음 데이터가 유효하면){
                음성 녹음 데이터 재생
            }else{
                tts로 description 띄우기
            }*/


        /*if(intent.hasExtra("imageUrl")) {

            //이미지 url, title, desc, visibility, 음성파일
            */
        /*if(이미지 url 유효하면){
                Glide.with(this).load(intent.getStringExtra("imageUrl")).into(imgDetailCardImg)
            }else{
                imgDetailCardImg.setImageDrawable(대체이미지)
            }*/

        initUi()

        ctvDetaliRecordPlay.setOnClickListener {
            if(playFlag){
                play()
            }else{
                stopPlaying()
            }
        }
    }

    private fun getCard(){
        card = intent.getSerializableExtra("CARD_DATA") as CardBean
    }

    private fun initUi(){
        dialogBuilder = AlertDialog.Builder(this@DetailCardActivity)

        card?.let{
            Glide.with(this).load(it.imageUrl).into(imgDetailCardImg)
            tvDetailCardTitle.text = it.title
            tvDetailCardDesc.text = it.desc
        }

        btnDetailBack.setOnSingleClickListener { finish() }
        btnDetailDelete.setOnSingleClickListener{
            dialogBuilder
                .setMessage("카드를 완젼히\n삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ -> deleteCard()}
                .setNegativeButton("취소") { _, _ -> finish() }
                .setCancelable(false)
                .show()
        }
        btnDetailEdit.setOnClickListener {
            val intent = Intent(this@DetailCardActivity, ModCardActivity::class.java)
            intent.putExtra("CARD_DATA", card)
            startActivityForResult(intent, MODIFY_CARD)
        }

        // Record to the external cache directory for visibility
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        recordFileName = "${externalCacheDir?.absolutePath}/audiorecord$timeStamp.3gp"

        // 실행(count) 버튼 리스너 설정
        //tvAddcardRecordPlay.setOnClickListener{ play() }
    }


    private fun deleteCard(){
        if(token == null){
            token = "token"
        }
        requestDeleteCard(token!!)
    }

    private fun requestDeleteCard(token: String){
        cardDataRepository
            .deleteCard(token, BodyDeleteCard(card!!.cardIdx) )
            .enqueue(object: Callback<ResCards> {
                override fun onFailure(call: Call<ResCards>, t: Throwable) {
                    "request delete card is fail message: ${t.message}".logDebug(this@DetailCardActivity)
                }

                override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                    if(response.isSuccessful){
                        response
                            .body()!!
                            .let{res ->
                                "request delete card is success".logDebug(this@DetailCardActivity)
                                if(res.success){this@DetailCardActivity.finish()}
                                else{}
                            }
                    }
                    else{
                        "request delete card is not success".logDebug(this@DetailCardActivity)
                    }
                }

            })
    }


    private fun getBitmapFromAssets(fileName: String): Bitmap? {
        return try {
            BitmapFactory.decodeStream(assets.open(fileName))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
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
                ctvAddcardRecordPlay.isChecked = false
            } catch (e: IOException) {
                "prepare() failed".logDebug(this@DetailCardActivity)
                //Log.e(TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            MODIFY_CARD ->{
                if(resultCode == Activity.RESULT_OK){
                    "카드가 수정되었습니다.".toast(this)
                }
                else{
                    "modify card result is not result ok, resultCode: ${resultCode}".logDebug(this@DetailCardActivity)
                }
            }
            DELETE_CARD ->{
                if(resultCode == Activity.RESULT_OK){

                }
                else{
                    "delete card result is not result ok, resultCode: ${resultCode}".logDebug(this@DetailCardActivity)
                }
            }
            else ->{
                "requestCode : ${requestCode} resultCode : ${resultCode}".logDebug(this@DetailCardActivity)
            }
        }
    }
    companion object{
        private const val MODIFY_CARD = 3333
        private const val DELETE_CARD = 2222
    }
}
