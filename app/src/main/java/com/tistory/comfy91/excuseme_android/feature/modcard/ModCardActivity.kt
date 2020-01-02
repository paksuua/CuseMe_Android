package com.tistory.comfy91.excuseme_android.feature.modcard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.*
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.feature.addcard.AudioTimer
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_mod_card.btnModcardMod
import kotlinx.android.synthetic.main.activity_mod_card.btnModcardBack
import kotlinx.android.synthetic.main.activity_mod_card.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ModCardActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private lateinit var card: CardBean
    private lateinit var dialogBuilder: AlertDialog.Builder
    private var token = SingletoneToken.getInstance().token
    private val cardDataRepsitory = ServerCardDataRepository()
    private var isCardAudioFilled = true
    private var isExistRecordFile = false
    private  var newRecordFileName: String? = null
    private var cardImageUrl: String? = null

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
    }

    private fun initUi() {
        if(!card.imageUrl.isNullOrEmpty()){
            newcard_photo_mod.isVisible = false
            Glide.with(this).load(card.imageUrl).into(imgModCardImg)
        }
        edtModcardTitle.setText(card.title)
        edtModcardDesc.setText(card.desc)

        // 녹음 관련 ui 설정
        setRecordUi()

        dialogBuilder = AlertDialog.Builder(this)

        btnModcardBack.setOnClickListener {
            showCancelDialog()
        }


        // 최종 수정 버튼
        btnModcardMod.setOnClickListener {
            when(isAllCardInfoFilled()){
                true ->{editCard()}
                false ->{
                    "카드 정보가 불충분합니다".toast(this@ModCardActivity)
                }
            }
        }

        imgModCardImg.setOnClickListener {
            getImageFromAlbum()
        }
    }

    // 모든 데이터 채워졌는지 확인
    private fun isAllCardInfoFilled(): Boolean {
        var result = true
        if (edtModcardTitle.text.isNullOrEmpty()) {
            result = false
        }
        else if (edtModcardDesc.text.isNullOrEmpty()) {
            result = false
        }
        else if(!cardImageUrl.isNullOrEmpty()){
            result = false
        }
        return result
    }

    private fun play(){
        if(!card.audioUrl.isNullOrEmpty()){
            onPlay(playFlag)
            tvModCardRecordNotice.text = when (playFlag) {
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
                setDataSource(newRecordFileName)
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
        setRecordFileName()
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(newRecordFileName)
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

        // TTS 버튼
        ctvModcardAutoRecord.setOnClickListener {
            ctvModcardAutoRecord.isVisible = !ctvModcardAutoRecord.isVisible
        }

        // 녹음 버튼 리스너 설정
        btnModcardTogRecord.setOnClickListener {
            it.requestFocus()
            btnModcardTogRecord.isVisible = false
            record()
        }

        // 실행(count) 버튼 리스너 설정
        ctvModcardRecordPlay.setOnClickListener{ play() }
    }

    private fun setRecordFileName(){
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        newRecordFileName = "${externalCacheDir?.absolutePath}/audiorecord$timeStamp.m4a"
    }

    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            IMAGE_PICK_CODE
        )
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
        card.title = edtModcardTitle.text.toString()
        card.desc =  edtModcardDesc.text.toString()
        newRecordFileName?.let{

        }
        cardImageUrl


        intent.putExtra("MOD_CARD", card)
        this@ModCardActivity.setResult(Activity.RESULT_OK, intent)

    }

    private fun reqEditCard(token: String){
        val title = edtModcardTitle.text.toString()
        val desc =  edtModcardDesc.text.toString()

        val titleRb = RequestBody.create(MediaType.parse("text/plain"), title)
        val descRb = RequestBody.create(MediaType.parse("text/plain"), desc)

        val options = BitmapFactory.Options()

        var audio_rb: MultipartBody.Part? = null
        if(!newRecordFileName.isNullOrEmpty()){
            val audioFile = File(newRecordFileName)
            val audioUri = Uri.fromFile(File(newRecordFileName))
            val audioBody = RequestBody.create(MediaType.parse(contentResolver.getType(audioUri)), audioFile)
            audio_rb = MultipartBody.Part.createFormData("audio", audioFile.name,audioBody)
        }




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
                        cardImageUrl = data?.data?.path
                        imgModCardImg.setImageURI(data?.data)
                    }
                    else -> {

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
