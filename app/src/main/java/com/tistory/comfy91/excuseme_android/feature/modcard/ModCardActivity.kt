package com.tistory.comfy91.excuseme_android.feature.modcard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.DummyCardDataRepository
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.feature.addcard.AudioTimer
import com.tistory.comfy91.excuseme_android.isPermissionNotGranted
import com.tistory.comfy91.excuseme_android.logDebug
import com.tistory.comfy91.excuseme_android.startSettingActivity
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_detail_card.*
import kotlinx.android.synthetic.main.activity_mod_card.btnModcardMod
import kotlinx.android.synthetic.main.activity_mod_card.btnModcardBack
import kotlinx.android.synthetic.main.activity_mod_card.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class ModCardActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private lateinit var card: CardBean
    private lateinit var dialogBuilder: AlertDialog.Builder
    private var token = SingletoneToken.getInstance().token
    private val cardDataRepsitory = ServerCardDataRepository()
    private var isCardImageFilled = true
    private var isCardAudioFilled = true
    private var isExistRecordFile = false
    private lateinit var recordFileName: String

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var recordFlag = true
    private var playFlag = true

    private lateinit var audioTimer: AudioTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod_card)

        initData()
        initUi()
    }

    private fun initData() {
        card = intent.getSerializableExtra("CARD_DATA") as CardBean
        if (card == null) {
            card = CardBean(
                0,
                "third card",
                "desc",
                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                "dummyAudio : https://t18.pimg.jp/055/208/688/1/55208688.jpg",
                0,
                false,
                "serialNum",
                0,
                ""
            )
        }

        // Record to the external cache directory for visibility
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        recordFileName = "${externalCacheDir?.absolutePath}/audiorecord$timeStamp.m4a"
    }

    private fun initUi() {
        Glide.with(this).load(card.imageUrl).into(imgDetailCardImg)
        tvDetailCardTitle.text = card.title
        tvDetailCardDesc.text = card.desc
        //todo("on,off")

        setRecordUi()

        dialogBuilder = AlertDialog.Builder(this)

        btnModcardBack.setOnClickListener {
            showCancelDialog()
        }


        btnModcardMod.setOnClickListener {
            when(isAllCardInfoFilled()){
                true ->{
                    card.apply {
                        this.title = edtModcardTitle.text.toString()
                        this.desc = edtModcardDesc.text.toString()

                    }
                    editCard()}
                false ->{
                    "카드 정보가 불충분합니다".logDebug(this@ModCardActivity)
                }
            }
        }


        imgModcardCardImg.setOnClickListener {
            getImageFromAlbum()
        }

        // 녹음 버튼 리스너 설정
        btnModcardTogRecord.setOnClickListener {
            it.requestFocus()

            btnModcardTogRecord.isVisible = false
            record()
        }

        // 실행(count) 버튼 리스너 설정
        ctvAddcardRecordPlay.setOnClickListener{ play() }



    }

    private fun play(){
        if(isExistRecordFile){
            onPlay(playFlag)
            tvAddCardRecordNotice.text = when (playFlag) {
                true -> "Stop playing"
                false -> "Start playing"
            }
            playFlag = !playFlag
        }
        else{
            "녹음 파일 없음".logDebug(this@ModCardActivity)
        }
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
                "prepare() failed".logDebug(this@ModCardActivity)
                Log.e(TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun record(){
        onRecord(recordFlag)
//        btnAddcardTogRecord.text = when (recordFlag) {
//            true -> "Stop recording"
//            false -> "Start recording"
//        }
        recordFlag = !recordFlag
    }


    private fun onRecord(start: Boolean) = if (start) startRecording() else stopRecording()

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(recordFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed")
            }

            start()
            audioTimer =
                AudioTimer(this@ModCardActivity) {
                    tvAddCardRecordNotice.text = "${audioTimer.count}초"
                }
            audioTimer.start()

            ctvAddcardRecordPlay.isVisible = false
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null

        audioTimer.cancel()
        btnAddcardSaveRecord.isEnabled = true
        tvAddCardRecordNotice.text = getString(R.string.record_notice)
        ctvAddcardRecordPlay.apply{
            isVisible = true
            isEnabled = true
            isExistRecordFile = true
        }
    }







    private fun setRecordUi(){
        if(card.audioUrl.isNullOrEmpty()){
            ctvModcardAutoRecord.isVisible = false
            ctvModcardRecordPlay.isEnabled = false
        }
        else{
            ctvModcardAutoRecord.isVisible = true
            ctvModcardRecordPlay.isEnabled = true
        }

        ctvModcardAutoRecord.setOnClickListener {
            ctvModcardAutoRecord.isVisible = !ctvModcardAutoRecord.isVisible
        }
    }

    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            IMAGE_PICK_CODE
        )
    }


    private fun isAllCardInfoFilled(): Boolean {
        var result = true
        if (edtModcardTitle.text.isNullOrEmpty()) {
            result = false
        }
        else if (edtModcardDesc.text.isNullOrEmpty()) {
            result = false
        }
        else if(!isCardImageFilled){
            result = false
        }
        else if(!isCardAudioFilled) {
            result = false
        }
        return result
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_CODE -> {
                grantResults.filter { it < 0 }.forEach {
                    Toast.makeText(applicationContext, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_LONG)
                        .show()
                    checkPermission(it)
                    return
                }
            } // end 1111
        }
    } // end onRequestPermissionResult()

    private fun showBanPermissionAlert() {
        dialogBuilder
            .setMessage("권한이 거부 되었습니다. 직접 권한을 허용하세요.")
            .setPositiveButton("상세") { _, _ -> startSettingActivity() }
            .setNegativeButton("취소") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun showCancelDialog(){
        dialogBuilder
            .setMessage("변경 내용을 되돌리시겠습니까?")
            .setPositiveButton("되돌리기") { _, _ -> this@ModCardActivity.finish() }
            .setNegativeButton("취소") { _, _ ->}
            .setCancelable(false)
            .show()
    }


    private fun editCard(){
        if(token == null){
            token = "string"
        }
        reqEditCard(token!!)
    }

    private fun reqEditCard(token: String){
        val title = edtModcardTitle.text.toString()
        val desc =  edtModcardDesc.text.toString()

        val titleRb = RequestBody.create(MediaType.parse("text/plain"), title)
        val descRb = RequestBody.create(MediaType.parse("text/plain"), desc)

        val options = BitmapFactory.Options()


        //////

//        cardDataRepsitory
//            .editCardDetail(token, (card.cardIdx).toString(), card)
//            .enqueue(object: Callback<ResCards> {
//                override fun onFailure(call: Call<ResCards>, t: Throwable) {
//                    "Request Edit Card is Fail, message: ${t.message}".logDebug(this@ModCardActivity)
//                }
//
//                override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
//                    if(response.isSuccessful){
//                        response.body()
//                            ?.let{
//                                "status : ${it.status}, success : ${it.success}message:  ${it.message} data: ${it.data}".logDebug(this@ModCardActivity)
//                                if(it.success){
//                                    intent.putExtra("MODIFY_CARD_DATA", card)
//                                    this@ModCardActivity.setResult(Activity.RESULT_OK, intent)
//                                    finish()
//                                }
//                                else{}
//                            }
//                    }
//                    else{
//                        "Request Edit Card is Not Successful".logDebug(this@ModCardActivity)
//                    }
//                }
//
//            })
//

    }

    private fun checkPermission(switch: Int) {
        when (switch) {
            AUDIO_PERMISSON -> {
                if (isPermissionNotGranted(Manifest.permission.RECORD_AUDIO)
                    || isPermissionNotGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.RECORD_AUDIO
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        showBanPermissionAlert()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ),
                            PERMISSION_CODE
                        )
                    }
                }
            }
            IMAGE_PERMISSON -> {
                if (isPermissionNotGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        showBanPermissionAlert()
                    }
                    else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.RECORD_AUDIO),
                            PERMISSION_CODE
                        )
                    }
                }
            }
            else -> {
                "없는 코드".logDebug(this@ModCardActivity)
            }
        }
    } // end checkPermission()


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        "(requestCode : $requestCode) (resultCode: $resultCode)".logDebug(this@ModCardActivity)

        when (requestCode) {
            IMAGE_PICK_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        // 정상적으로 이미지를 가져온 경우
                        "Success Get Image from Gallery".logDebug(this@ModCardActivity)
                        imgModcardCardImg.setImageURI(data?.data)
                        isCardImageFilled = true

                    }
                    else -> {
                        isCardImageFilled = false
                    }
                }
            }
        }
    } // end onActivityResult()

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        stopPlaying()
    }


    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1111
        private const val AUDIO_PERMISSON = 2222
        private const val IMAGE_PERMISSON = 3333

    }



}
