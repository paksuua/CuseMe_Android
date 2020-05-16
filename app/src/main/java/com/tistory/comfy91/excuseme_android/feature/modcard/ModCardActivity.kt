package com.tistory.comfy91.excuseme_android.feature.modcard

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.*
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.DummyCardDataRepository
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.feature.addcard.AudioTimer
import kotlinx.android.synthetic.main.activity_mod_card.*
import kotlinx.android.synthetic.main.addcard_recored_finish_layout.*
import kotlinx.android.synthetic.main.addcard_recored_init_layout.*
import kotlinx.android.synthetic.main.addcard_recored_play_layout.*
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
    private var newRecordFileName: String? = null
    private var cardImageUrl: Uri? = null

    //record
    val SECTION_INIT = 1
    val SECTION_PLAY = 2
    val SECTION_FINISH = 3

    private var recordFileName: String? = null
    private var recorder: MediaRecorder? = null
    private lateinit var audioTimer: AudioTimer
    private var state: Boolean = false
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var circleAnimation: ValueAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod_card)

        // btm record view init
        setRecordView(SECTION_INIT)
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

        mediaPlayer = MediaPlayer()

        recordInit_CenterBtn.setOnClickListener{
            // start record
//            if(!checkPermission()){
//                requestPermission()
//            }else{
            startRecording()
//            }
        }
        recordPlay_LeftBtn.setOnClickListener {
            stopRecording()
        }
        recordFinish_LeftBtn.setOnClickListener {
            play()
        }
        recordFinish_CenterBtn.setOnClickListener {
            startRecording()
        }
        recordFinish_RightBtn.setOnClickListener {
            tvModCardFinish.text = "저장되었습니다."
        }

        //init animation
        circleAnimation = ValueAnimator.ofFloat(0f, 360f)
            .apply {
                this.setDuration(10000)
                    .addUpdateListener { animation ->
                        var value: Float = animation?.getAnimatedValue() as Float
                        recordPlay_CenterCircle.angle = value
                    }
            }
    }

    private fun initUi() {
        if (!card.imageUrl.isNullOrEmpty()) {
            newcard_photo_mod.isVisible = false
            mod_newcard_tv.isVisible = false
            Glide.with(this).load(card.imageUrl).into(imgModCardImg)
        }
        edtModcardTitle.setText(card.title)
        edtModcardDesc.setText(card.desc)

        dialogBuilder = AlertDialog.Builder(this)

        btnModcardBack.setOnClickListener {showCancelDialog()}

        // 최종 수정 버튼
        btnModcardMod.setOnClickListener {
            when (isAllCardInfoFilled()) {
                true -> {editCard()}
                false -> {"카드 정보가 불충분합니다".toast(this@ModCardActivity)}
            }
        }

        imgModCardImg.setOnClickListener {
            if(!checkPermission(IMAGE_PERMISSON)) {
                showBanPermissionAlert()
            }
            else{
                getImageFromAlbum()
            }
        }
    }

    // 모든 데이터 채워졌는지 확인
    private fun isAllCardInfoFilled(): Boolean {
        var result = true
        if (edtModcardTitle.text.isNullOrEmpty()) {
            result = false
        } else if (edtModcardDesc.text.isNullOrEmpty()) {
            result = false
//        } else if (!cardImageUrl.isNullOrEmpty()) {
            result = false
        }
        return result
    }


    //녹음 시작
    fun startRecording() {

        tvModCardFinish.text = ""

        //stop palying
        mediaPlayer!!.stop()

        //start animation
        circleAnimation.start()

        //set section play
        setRecordView(SECTION_PLAY)

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        recordFileName = "${externalCacheDir?.absolutePath}/audiorecord$timeStamp.m4a"
        recorder= MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder?.setOutputFile(recordFileName)

        try {
            recorder?.prepare()
            recorder?.start()
            state = true
            Toast.makeText(this, "녹음 시작", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        audioTimer =
            AudioTimer(this@ModCardActivity) {
                recordPlay_CenterTv.text = "${audioTimer.count}초"
            }
        audioTimer.start()
//        circleAnimation.start()
    }

    private fun stopRecording(){

        //set section play
        setRecordView(SECTION_FINISH)
        circleAnimation.cancel()

        if(state){
            recorder?.stop()
            recorder?.reset()
            recorder?.release()
            state = false
        }else{

        }
    }

    private fun play() {

        if (File(recordFileName!!).exists()) {

            Toast.makeText(this, "녹음 재생 중", Toast.LENGTH_SHORT).show()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(recordFileName)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()

        } else {
            Toast.makeText(this, "녹음된 파일이 없습니다.", Toast.LENGTH_SHORT).show()
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

    private fun showCancelDialog() {
        dialogBuilder
            .setMessage("변경 내용을 되돌리시겠습니까?")
            .setPositiveButton("되돌리기") { _, _ ->
                this@ModCardActivity.setResult(Activity.RESULT_CANCELED)
                this@ModCardActivity.finish()
            }
            .setNegativeButton("취소") { _, _ -> }
            .setCancelable(false)
            .show()
    }


    private fun editCard() {
        card.title = edtModcardTitle.text.toString()
        card.desc = edtModcardDesc.text.toString()
        newRecordFileName?.let {
            card.audioUrl = it
        }

        cardImageUrl?.let{
            card.imageUrl = it.toString()
        }
        intent.putExtra("MOD_CARD", card)
        this@ModCardActivity.setResult(Activity.RESULT_OK, intent)
        finish()

    }

    //todo("카드 수정 반영 Retrofit")
    private fun reqEditCard(token: String) {
        val title = edtModcardTitle.text.toString()
        val desc = edtModcardDesc.text.toString()

        val titleRb = RequestBody.create(MediaType.parse("text/plain"), title)
        val descRb = RequestBody.create(MediaType.parse("text/plain"), desc)

        val options = BitmapFactory.Options()

        var audio_rb: MultipartBody.Part? = null
        if (!newRecordFileName.isNullOrEmpty()) {
            val audioFile = File(newRecordFileName)
            val audioUri = Uri.fromFile(File(newRecordFileName))
            val audioBody =
                RequestBody.create(MediaType.parse(contentResolver.getType(audioUri)), audioFile)
            audio_rb = MultipartBody.Part.createFormData("audio", audioFile.name, audioBody)
        }
    }

    private fun checkPermission(switch: Int): Boolean{
        when (switch) {
            AUDIO_PERMISSON -> {
                if (isPermissionNotGranted(Manifest.permission.RECORD_AUDIO)
                    || isPermissionNotGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.RECORD_AUDIO
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        return false

                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ),
                            PERMISSION_CODE
                        )
                        return false
                    }
                }
                else{return true}
            }
            IMAGE_PERMISSON -> {
                if (isPermissionNotGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        return false
                        showBanPermissionAlert()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.RECORD_AUDIO),
                            PERMISSION_CODE
                        )
                        return false
                    }
                }
                else{
                    return true
                }
            }
            else -> {
                "없는 코드".logDebug(this@ModCardActivity)
                return false
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
                        cardImageUrl = data?.data
                        imgModCardImg.setImageURI(data?.data)
                        newcard_photo_mod.isVisible = false
                    }
                    else -> {

                        newcard_photo_mod.isVisible = true
                    }
                }
            }
        }
    } // end onActivityResult()


    fun setRecordView(section : Int){
        when(section){
            SECTION_INIT ->{
                recordInitLayout.visibility = View.VISIBLE
                recordPlayLayout.visibility =View.GONE
                recordFinishLayout.visibility =View.GONE
            }
            SECTION_PLAY ->{
                recordInitLayout.visibility = View.GONE
                recordPlayLayout.visibility =View.VISIBLE
                recordFinishLayout.visibility =View.GONE
            }
            SECTION_FINISH ->{
                recordInitLayout.visibility = View.GONE
                recordPlayLayout.visibility =View.GONE
                recordFinishLayout.visibility =View.VISIBLE
            }
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1111
        private const val AUDIO_PERMISSON = 2222
        private const val IMAGE_PERMISSON = 3333
    }

}
