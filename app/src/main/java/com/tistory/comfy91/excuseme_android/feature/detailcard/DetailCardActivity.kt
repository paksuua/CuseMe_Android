package com.tistory.comfy91.excuseme_android.feature.detailcard

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.DataHelperSortCard
import com.tistory.comfy91.excuseme_android.logDebug
import com.tistory.comfy91.excuseme_android.setOnSingleClickListener
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_detail_card.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DetailCardActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null
    private var playFlag = false
    private lateinit var recordFileName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_card)
        //itemView.context.newStartActivity(DetailCardActivity::class.java)
        //var li

        var card=intent.getSerializableExtra("CARDDATA") as DataHelperSortCard


        if(!card.imageUrl.isNullOrEmpty()){
                Glide.with(this).load(card.imageUrl).into(imgDetailCardImg)
            }
        tvDetailCardTitle.text = card.title

        if(card.visibility){ // 보일경우 이미지 변경
           /* val assetsBitmap: Bitmap? = getBitmapFromAssets("flower8.jpg")
            ctvHelperCheck.setImageBitmap(assetsBitmap)*/
        }else{ // 안보일 경우
           /* val assetsBitmap: Bitmap? = getBitmapFromAssets("flower8.jpg")
            ctvHelperCheck.setImageBitmap(assetsBitmap)*/
        }


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

        InitUi()

        ctvDetaliRecordPlay.setOnClickListener {
            if(playFlag){
                play()
            }else{
                stopPlaying()
            }
        }
    }

    private fun InitUi(){
        btnDetailBack.setOnSingleClickListener { finish() }
        btnDetailDelete.setOnSingleClickListener{
            //todo
        }

        // Record to the external cache directory for visibility
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        recordFileName = "${externalCacheDir?.absolutePath}/audiorecord$timeStamp.3gp"

        // 실행(count) 버튼 리스너 설정
        //tvAddcardRecordPlay.setOnClickListener{ play() }
    }

    // Custom method to get assets folder image as bitmap
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
                tvAddcardRecordPlay.isChecked = false
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
}
