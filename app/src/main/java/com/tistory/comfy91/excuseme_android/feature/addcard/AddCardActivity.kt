package com.tistory.comfy91.excuseme_android.feature.addcard

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import androidx.core.view.isVisible
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.answer.ResDownCard
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.feature.detailcard.DetailCardActivity
import com.tistory.comfy91.excuseme_android.logDebug
import com.tistory.comfy91.excuseme_android.toast
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_add_card.imgAddcardCardImg
import kotlinx.android.synthetic.main.addcard_recored_finish_layout.*
import kotlinx.android.synthetic.main.addcard_recored_init_layout.*
import kotlinx.android.synthetic.main.addcard_recored_play_layout.*
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

    //permission
    private var sentToSettings = false
    private var permissionsRequired = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var permissionStatus: SharedPreferences? = null

    // image
    private var isCardImageFilled = false
    private lateinit var selectPicUri: Uri

    //upload
    private var token = SingletoneToken.getInstance().token
    private val cardDataRepository = ServerCardDataRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        // btm record view init
        setRecordView(SECTION_INIT)
        // init
        init()

        permissionStatus = getSharedPreferences("permissionStatus", Context.MODE_PRIVATE)
        requestPermission()

    }

    override fun onResume() {
        super.onResume()

        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                Toast.makeText(applicationContext, "Allowed All Permissions", Toast.LENGTH_LONG).show()
            }
        }

    }

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

    fun init(){

        mediaPlayer = MediaPlayer()

        recordInit_CenterBtn.setOnClickListener{
            // start record
            if(!checkPermission()){
                requestPermission()
            }else{
                startRecording()
            }
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
            tvAddCardRecordFinish.text = "저장되었습니다."
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

        imgAddcardCardImg.setOnClickListener {

            if(!checkPermission()){
                requestPermission()
            }else{
                getImageFromAlbum()
            }
        }

        //upload
        btnAddCard.setOnClickListener {
            if (isAllCardInfoFilled()) {
                uploadCard()
            }
            else{
                "카드 만들기에 필요한 정보가 충분하지 않습니다".toast(this@AddCardActivity)
            }

        }


    }

    //녹음 시작
    fun startRecording() {

        tvAddCardRecordFinish.text = ""

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
            AudioTimer(this@AddCardActivity) {
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

    private fun checkPermission() : Boolean{
        return !(checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        if (checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || shouldShowRequestPermissionRationale(this, permissionsRequired[1])) {
                //Show Information about why you need the permission
                getAlertDialog()
            } else if (permissionStatus!!.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Need Multiple Permissions")
                builder.setMessage("This app needs permissions.")
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    sentToSettings = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                    Toast.makeText(applicationContext, "Go to Permissions to Grant ", Toast.LENGTH_LONG).show()
                }
                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.show()
            } else {
                //just request the permission
                requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT)
            }

            //   txtPermissions.setText("Permissions Required")

            val editor = permissionStatus!!.edit()
            editor.putBoolean(permissionsRequired[0], true)
            editor.commit()
        } else {
            //You already have the permission, just go ahead.
//            Toast.makeText(applicationContext, "Allowed All Permissions", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            var allgranted = false
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true
                } else {
                    allgranted = false
                    break
                }
            }

            if (allgranted) {
//                Toast.makeText(applicationContext, "Allowed All Permissions", Toast.LENGTH_LONG).show()
            } else if (shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || shouldShowRequestPermissionRationale(this, permissionsRequired[1])) {

                getAlertDialog()
            } else {
                Toast.makeText(applicationContext, "Unable to get Permission", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Multiple Permissions")
        builder.setMessage("This app needs permissions.")
        builder.setPositiveButton("Grant") { dialog, which ->
            dialog.cancel()
            requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT)
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    override fun onPostResume() {
        super.onPostResume()
        if (sentToSettings) {
            if (checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                Toast.makeText(applicationContext, "Allowed All Permissions", Toast.LENGTH_LONG).show()
            }
        }
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

                    "onResponse : ${response.code()}".logDebug(this@AddCardActivity)

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

    fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            IMAGE_PICK_CODE
        )
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CALLBACK_CONSTANT = 100
        private const val REQUEST_PERMISSION_SETTING = 101
    }

}

