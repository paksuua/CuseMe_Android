package com.tistory.comfy91.excuseme_android.feature.addcard

import android.Manifest
import android.app.Activity
import android.content.ContentProvider
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.DummyCardDataRepository
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.feature.detailcard.DetailCardActivity
import com.tistory.comfy91.excuseme_android.isPermissionNotGranted
import com.tistory.comfy91.excuseme_android.logDebug
import com.tistory.comfy91.excuseme_android.startSettingActivity
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_mod_card.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class AddCardActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private lateinit var dialogBuilder: AlertDialog.Builder

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var recordFlag = true
    private var playFlag = true
    private var isExistRecordFile = false
    private var isCardImageFilled = false

    private lateinit var audioTimer: AudioTimer

    private var recordFileName: String? = null
    private lateinit var selectPicUri : Uri

    private var token = SingletoneToken.getInstance().token
    private val cardDataRepository =DummyCardDataRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        checkPermission()

        initData()
        initUI()
    } // end onCrate()

    private fun initData(){
        // Record to the external cache directory for visibility
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        recordFileName = "${externalCacheDir?.absolutePath}/audiorecord$timeStamp.m4a"
    }

    private fun initUI() {
        dialogBuilder = AlertDialog.Builder(this)

        // 이미지 가져오기 리스너 설정
        imgAddcardCardImg.setOnClickListener {
            getImageFromAlbum()
        }

        // 녹음 버튼 리스너 설정
        btnAddcardTogRecord.setOnClickListener {
            it.requestFocus()

            btnAddcardTogRecord.isVisible = false
            record()
        }

        // 실행(count) 버튼 리스너 설정
        ctvAddcardRecordPlay.setOnClickListener{
            if(!isExistRecordFile){
                record()
            }
            else{
                play()
            }

        }

        ctvAddcardRecordPlay.apply {
            when(isExistRecordFile){
                true ->  setBackgroundResource(R.drawable.ctv_record)
                false ->{
                    setBackgroundResource(R.drawable.btn_newcard_play_unslected)
                    isEnabled = false
                }
            }
        }

        // 확인 버튼
        btnAddcardSaveRecord
            .apply {isChecked = true}
            .setOnClickListener {(it as CheckedTextView).toggle()}


        // TTS
        btnAddCardTts.setOnClickListener(object: View.OnClickListener{
            private val isClicked = false

            override fun onClick(view: View?) {
                val imageView = view as ImageView
                isClicked != isClicked
                setTTSUI(isClicked, imageView)

            }

        })
    }

    private fun setTTSUI(isClicked: Boolean, imageView: ImageView){
        if(isExistRecordFile){
            recordFileName = null
        }

        if(isClicked){
            Glide
                .with(imageView.context)
                .load(R.drawable.btn_newcard_maketts_selected)
                .into(imageView)
        }
        else{
            Glide
                .with(imageView.context)
                .load(R.drawable.btn_newcard_maketts_unselected)
                .into(imageView)
        }
        tvAddcardTTSNotice.isVisible = isClicked
        ctvAddcardRecordPlay.isVisible = !isClicked
        circleCounterView.isVisible = !isClicked
        btnAddcardSaveRecord.isVisible = !isClicked

    }

    private fun setCompleteUi(){
        ctvAddcardRecordPlay.isChecked = false
    }


    private fun play(){
        if(isExistRecordFile){
            onPlay(playFlag)
            ctvAddcardRecordPlay.isChecked = playFlag
            playFlag = !playFlag

        }
        else{
            "녹음 파일 없음".logDebug(this@AddCardActivity)
        }
    }

    private fun onPlay(start: Boolean) = if (start) startPlaying() else stopPlaying()

    private fun startPlaying() {
        player = MediaPlayer().apply {
            setOnCompletionListener { setCompleteUi()  }
            try {
                setDataSource(recordFileName)
                prepare()
                start()
                ctvAddcardRecordPlay.isChecked = false
            } catch (e: IOException) {
                "prepare() failed".logDebug(this@AddCardActivity)
                Log.e(TAG, "prepare() failed")
            }
    }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        ctvAddcardRecordPlay.isChecked = false
    }

    private fun record(){
        onRecord(recordFlag)
        when(recordFlag){
            true ->{
                ctvAddcardRecordPlay.isEnabled = true
                ctvAddcardRecordPlay.setBackgroundResource(R.drawable.ctv_record)
                ctvAddcardRecordPlay.isChecked = true
            }
            false->{
                ctvAddcardRecordPlay.isChecked = false

                if(isExistRecordFile){
                    ctvAddcardRecordPlay.setBackgroundResource(R.drawable.ctv_record)
                    btnAddcardSaveRecord.isChecked = true
                }
                else{
                    ctvAddcardRecordPlay.isEnabled = false
                    ctvAddcardRecordPlay.setBackgroundResource(R.drawable.btn_newcard_play_unslected)
                }
            }
        }
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
                AudioTimer(this@AddCardActivity) {
                    tvAddCardRecordNotice.text = "${audioTimer.count}초"
                }
            audioTimer.start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null

        audioTimer.cancel()

        tvAddCardRecordNotice.text = getString(R.string.record_notice)
        ctvAddcardRecordPlay.apply{
            isVisible = true
            isEnabled = true
            isExistRecordFile = true
        }
    }

    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,
            IMAGE_PICK_CODE
        )
    }

    private fun isAllCardInfoFilled(): Boolean {
        var result = true
        if (edtAddcardTitle.text.isNullOrEmpty()) {
            result = false
        }
        else if (edtAddcardDesc.text.isNullOrEmpty()) {
            result = false
        }
        else if(!isCardImageFilled){
            result = false
        }
        else if(!isExistRecordFile) {
            result = false
        }
        return result
    }


    private fun checkPermission() {
        if (this.isPermissionNotGranted(Manifest.permission.RECORD_AUDIO)
            || this.isPermissionNotGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
            || this.isPermissionNotGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                dialogBuilder
                    .setMessage("권한이 거부 되었습니다. 직접 권한을 허용하세요.")
                    .setPositiveButton("상세") { _, _ -> this.startSettingActivity() }
                    .setNegativeButton("취소") { _, _ -> finish() }
                    .setCancelable(false)
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        (Manifest.permission.READ_EXTERNAL_STORAGE),
                        (Manifest.permission.RECORD_AUDIO)
                    ),
                    PERMISSION_CODE
                )
            } // end if...else
        }
    } // end checkPermission()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_CODE -> {
                grantResults.filter { it < 0 }.forEach {
                    Toast.makeText(applicationContext, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_LONG).show()
                    checkPermission()
                    return
                }
            } // end 1111
        }
    } // end onRequestPermissionResult()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "(requestCode : $requestCode) (resultCode: $resultCode)")

        when (requestCode) {
            IMAGE_PICK_CODE -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {
                        // 정상적으로 이미지를 가져온 경우
                        Log.d(TAG, "Success Get Image from Gallery")
                        selectPicUri = data?.data!!
                        imgAddcardCardImg.setImageURI(selectPicUri)
                        isCardImageFilled = true
                    }
                    else -> {
                        "Fail Get Image From Gallery".logDebug(this@AddCardActivity)
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

    private fun uploadCard(){
        if(token==null){
            token = "token"
        }
        else{
            // get cardData
            val title = edtAddcardTitle.text.toString()
            val desc= edtAddcardDesc.text.toString()
            //todo("visibility 가져와야함")
            val visibility = true

            val title_rb = RequestBody.create(MediaType.parse("text/plain"), title)
            val desc_rb = RequestBody.create(MediaType.parse("text/plain"), desc)

            val options = BitmapFactory.Options()
            val inputStream: InputStream = contentResolver.openInputStream(selectPicUri)!!
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)
            val photoBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray())

            val picture_rb = MultipartBody.Part.createFormData("image", File(selectPicUri.toString()).name,photoBody)

            // region audio file 전송
            val audioFile = File(recordFileName)
            val audioUri = Uri.fromFile(File(recordFileName))
            val audioBody = RequestBody.create(MediaType.parse(contentResolver.getType(audioUri)), audioFile)
            val audio_rb = MultipartBody.Part.createFormData("audio", audioFile.name,audioBody)

            cardDataRepository
                .addCard(
                token!!,
                title_rb,
                desc_rb,
                visibility,
                picture_rb,
                audio_rb
                )
                .enqueue(object: Callback<ResCards> {
                    override fun onFailure(call: Call<ResCards>, t: Throwable) {
                        "Fail to Add Card, message:${t.message}".logDebug(this@AddCardActivity)
                    }

                    override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                        if(response.isSuccessful){
                            response.body()
                                ?.let{
                                    "status : ${it.status}, success: ${it.success}, data: ${it.data}, message : ${it.message}".logDebug(this@AddCardActivity)
                                    if(it.success){
                                        goDetailcardActivity()
                                    }
                                    else{"Add Card Body is not Success".logDebug(this@AddCardActivity)}
                                }
                        }
                        else{
                            "Add Card Response is not Succes".logDebug(this@AddCardActivity)
                        }
                    }

                })
            // endregion
        }
    }

    private fun goDetailcardActivity(){
        val intent = Intent(this, DetailCardActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1111

    }
}
