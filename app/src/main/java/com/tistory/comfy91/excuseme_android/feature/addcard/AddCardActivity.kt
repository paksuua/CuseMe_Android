package com.tistory.comfy91.excuseme_android.feature.addcard

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.*
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.ResDownCard
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.DummyCardDataRepository
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.feature.detailcard.DetailCardActivity
import kotlinx.android.synthetic.main.activity_add_card.*
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
import java.net.URI
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
    private lateinit var selectPicUri: Uri
    private lateinit var circleAnimation: ValueAnimator


    private var token = SingletoneToken.getInstance().token
    private val cardDataRepository = ServerCardDataRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        initData()
        initUI()
    } // end onCrate()

    private fun initData() {
        // Record to the external cache directory for visibility
        circleAnimation = ValueAnimator.ofFloat(0f, 360f)
            .apply {
                this.setDuration(10000)
                    .addUpdateListener { animation ->
                        var value: Float = animation?.animatedValue as Float
                        circleCounterView.angle = value
                    }
            }
    }

    private fun initUI() {
        dialogBuilder = AlertDialog.Builder(this)


        //이미지 가져오기 리스너 설정
        imgAddcardCardImg.setOnClickListener {
            if (checkPermission(PERMISSION_READ_EXTERNAL_STORAGE)) {
                getImageFromAlbum()
            }
        }

        //녹음 버튼 리스너 설정
        btnAddcardTogRecord.setOnClickListener {
            checkPermission(PERMISSION_RECORD_AUDIO)
            checkPermission(PERMISSION_WRITE_EXTERNAL_STORAGE)
            it.requestFocus()

            record()
        }

        // 실행(count) 버튼 리스너 설정
        ctvAddcardRecordPlay.setOnClickListener {
            if (!isExistRecordFile) {
                record()
            } else {
                play()
            }

        }


        ctvAddcardRecordPlay.apply {
            when (isExistRecordFile) {
                true -> setBackgroundResource(R.drawable.ctv_record)
                false -> {
                    setBackgroundResource(R.drawable.btn_newcard_play_unslected)
                }
            }
        }

        // 확인 버튼
        btnAddcardSaveRecord
            .apply { setSaveBtn(false) }
            .setOnClickListener { (it as CheckedTextView).toggle() }

        btnAddcardSaveRecord.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                setSaveBtn(false)
            }
        })


        // TTS
        btnAddCardTts.setOnClickListener(object : View.OnClickListener {
            private val isClicked = false

            override fun onClick(view: View?) {
                val imageView = view as ImageView
                isClicked != isClicked
                setTTSUI(isClicked, imageView)
            }

        })


        // 최종 카드 추가 버튼
        btnAddCard.setOnClickListener {
            if (isAllCardInfoFilled()) {
                uploadCard()
            } else {
                "카드 만들기에 필요한 정보가 충분하지 않습니다".toast(this@AddCardActivity)
            }

        }

        // 뒤로가기 버튼
        btnAddcardBack.setOnClickListener { finish() }
    }

    private fun setTTSUI(isClicked: Boolean, imageView: ImageView) {
        if (isExistRecordFile) {
            recordFileName = null
        }

        if (isClicked) {
            Glide
                .with(imageView.context)
                .load(R.drawable.btn_newcard_maketts_selected)
                .into(imageView)
        } else {
            Glide
                .with(imageView.context)
                .load(R.drawable.btn_newcard_maketts_unselected)
                .into(imageView)
        }

    }

    private fun setCompleteUi() {
    }


    private fun play() {
        if (isExistRecordFile) {
            onPlay(playFlag)
            ctvAddcardRecordPlay.isChecked = playFlag
            playFlag = !playFlag

        } else {
            "녹음 파일 없음".logDebug(this@AddCardActivity)
        }
    }

    private fun onPlay(start: Boolean) = if (start) startPlaying() else stopPlaying()

    private fun startPlaying() {
        player = MediaPlayer().apply {
            setOnCompletionListener { setCompleteUi() }
            try {
                setDataSource(recordFileName)
                prepare()
                start()
            } catch (e: IOException) {
                "prepare() failed".logDebug(this@AddCardActivity)
                Log.e(TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null

    }

    private fun record() {
        onRecord(recordFlag)
        when (recordFlag) {
            true -> {
                ctvAddcardRecordPlay.setBackgroundResource(R.drawable.ctv_record)
            }
            false -> {

                if (isExistRecordFile) {
                } else {

                }
            }
        }
        recordFlag = !recordFlag
    }

    private fun onRecord(start: Boolean) = if (start) startRecording() else stopRecording()

    private fun startRecording() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        recordFileName = "${externalCacheDir?.absolutePath}/audiorecord$timeStamp.m4a"

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
                    record_second_notice.text = "${audioTimer.count}초"
                }
            audioTimer.start()
            circleAnimation.start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null

        audioTimer.cancel()
        circleAnimation.cancel()
        setSaveBtn(true)


        tvAddCardRecordNotice.text = getString(R.string.record_notice)
        ctvAddcardRecordPlay.apply {
            isExistRecordFile = true
        }

        recorder?.release()
        recorder = null
        stopPlaying()
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
        if (edtAddcardTitle.text.isNullOrEmpty()) {
            result = false
        } else if (edtAddcardDesc.text.isNullOrEmpty()) {
            result = false
        } else if (!isCardImageFilled) {
            result = false
        }
        return result
    }

    private fun showSettingActivity() {
        dialogBuilder
            .setMessage("권한이 거부 되었습니다. 직접 권한을 허용하세요.")
            .setPositiveButton("상세") { _, _ -> this.startSettingActivity() }
            .setNegativeButton("취소") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }


    private fun checkPermission(permissionFlag: Int): Boolean {
        when (permissionFlag) {
            PERMISSION_RECORD_AUDIO -> {
                if (this.isPermissionNotGranted(Manifest.permission.RECORD_AUDIO)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.RECORD_AUDIO
                        )
                    ) {
                        showSettingActivity()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.RECORD_AUDIO
                            ),
                            PERMISSION_RECORD_AUDIO
                        )
                    }
                    return false
                } else {
                    return true
                }
            }
            PERMISSION_READ_EXTERNAL_STORAGE -> {
                if (this.isPermissionNotGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        showSettingActivity()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ),
                            PERMISSION_READ_EXTERNAL_STORAGE
                        )
                    }
                    return false
                } else {
                    return true
                }
            }
            PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                if (this.isPermissionNotGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        showSettingActivity()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            PERMISSION_WRITE_EXTERNAL_STORAGE
                        )
                    }
                    return false
                } else {
                    return true
                }
            }
            else -> {
                "There is no such permission flag".logDebug(this@AddCardActivity)
                return false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_RECORD_AUDIO -> {
                grantResults.filter { it < 0 }.forEach { _ ->
                    Toast.makeText(applicationContext, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_LONG)
                        .show()
                    checkPermission(PERMISSION_RECORD_AUDIO)
                    return
                }
            } // end 1111
            PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                grantResults.filter { it < 0 }.forEach { _ ->
                    Toast.makeText(applicationContext, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_LONG)
                        .show()
                    checkPermission(PERMISSION_WRITE_EXTERNAL_STORAGE)
                    return
                }
            }
            PERMISSION_READ_EXTERNAL_STORAGE -> {
                grantResults.filter { it < 0 }.forEach { _ ->
                    Toast.makeText(applicationContext, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_LONG)
                        .show()
                    checkPermission(PERMISSION_READ_EXTERNAL_STORAGE)
                    return
                }
            }
        }
    } // end onRequestPermissionResult()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "(requestCode : $requestCode) (resultCode: $resultCode)")

        when (requestCode) {
            IMAGE_PICK_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        // 정상적으로 이미지를 가져온 경우
                        Log.d(TAG, "Success Get Image from Gallery")
                        selectPicUri = data?.data!!
                        imgAddcardCardImg.setImageURI(selectPicUri)
                        isCardImageFilled = true
                        newcard_photo.isVisible = false
                        tvAddcardPhotoMessage.isVisible = false
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

    private fun uploadCard() {
        if (token == null) {
            token = "token"
        }

        // get cardData
        val title = edtAddcardTitle.text.toString()
        val desc = edtAddcardDesc.text.toString()
        //todo("visibility 가져와야함")
        val visibility = false

        val title_rb = RequestBody.create(MediaType.parse("text/plain"), title)
        val desc_rb = RequestBody.create(MediaType.parse("text/plain"), desc)

        val options = BitmapFactory.Options()
        val inputStream: InputStream = contentResolver.openInputStream(selectPicUri)!!
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)

        val photoBody = RequestBody.create(
            MediaType.parse("image/jpg"),
            byteArrayOutputStream.toByteArray()
        )


        val picture_rb = MultipartBody.Part.createFormData(
            "image",
            File(selectPicUri.toString()).name,
            photoBody
        )


        var audioFile: File? = null
        var audioUrl: Uri? = null
        var audioBody: RequestBody? = null
        var audio_rb: MultipartBody.Part? = null
        recordFileName?.let {
            audioFile = File(recordFileName)
            audioUrl = Uri.fromFile(File(recordFileName))
            audioBody = RequestBody.create(MediaType.parse("audio/mpeg"), audioFile)
            audio_rb = MultipartBody.Part.createFormData("audio", audioFile?.name, audioBody)
        }


        // region audio file 전송
//        val optionsA = BitmapFactory.Options()
//        val inputStreamA: InputStream = contentResolver.openInputStream(audioUri)!!
//        val bitmapA = BitmapFactory.decodeStream(inputStreamA, null, options)
//        val byteArrayOutputStreamA = ByteArrayOutputStream()
//
//        val audioBody = RequestBody.create(MediaType.parse("audio/mpeg"), audioFile)
//        val audioBody = RequestBody.create(MediaType.parse(contentResolver.getType(audioUri)), audioFile)
//        val audio_rb = MultipartBody.Part.createFormData("audio", audioFile.name, audioBody)

        "token: $token, title: $title, desc: $desc, visiblity: $visibility, picture_rb $picture_rb, selectPicUri : $selectPicUri, audioFileName : ${audioFile?.name}   audio_rb : $audio_rb".logDebug(
            this@AddCardActivity
        )

        cardDataRepository
            .addCard(
                token!!,
                title_rb,
                desc_rb,
                visibility,
                picture_rb,
                audio_rb
            )
            .enqueue(object : Callback<ResDownCard> {
                override fun onFailure(call: Call<ResDownCard>, t: Throwable) {
                    "Fail to Add Card, message:${t.message}".logDebug(this@AddCardActivity)
                }

                override fun onResponse(call: Call<ResDownCard>, response: Response<ResDownCard>) {
                    if (response.isSuccessful) {
                        response.body()
                            ?.let {
                                "status : ${it.status}, success: ${it.success}, data: ${it.data}, message : ${it.message}".logDebug(
                                    this@AddCardActivity
                                )
                                if (it.success) {
                                    var card = it.data
                                    card?.imageUrl = selectPicUri.toString()
                                    val intent =
                                        Intent(this@AddCardActivity, DetailCardActivity::class.java)

                                    intent.putExtra("DOWN_CARD", it.data)
                                    startActivity(intent)
                                    isCardImageFilled = true

                                } else {
                                    "Add Card Body is not Success".logDebug(this@AddCardActivity)
                                }

                            }

                    }
                    // endregion

                }

            })


    }


    private fun setSaveBtn(isOn: Boolean) {

        //확인버튼
        btnAddcardSaveRecord.isEnabled = isOn
        btnAddcardSaveRecord.isSelected = isOn

    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1111

        private const val PERMISSION_RECORD_AUDIO = 2222
        private const val PERMISSION_READ_EXTERNAL_STORAGE = 3333
        private const val PERMISSION_WRITE_EXTERNAL_STORAGE = 4444

    }

}


