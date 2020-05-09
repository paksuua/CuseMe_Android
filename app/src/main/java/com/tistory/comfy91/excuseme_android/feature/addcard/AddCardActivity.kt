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
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.view.isVisible
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.answer.ResAddCard
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.feature.detailcard.DetailCardActivity
import com.tistory.comfy91.excuseme_android.logDebug
import com.tistory.comfy91.excuseme_android.toast
import kotlinx.android.synthetic.main.activity_add_card.*
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
    private val SECTION_INIT = 1
    private val SECTION_PLAY = 2
    private val SECTION_FINISH = 3

    private var recordFileName: String? = null
    private var recorder: MediaRecorder? = null
    private lateinit var audioTimer: AudioTimer
    private var state: Boolean = false
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var circleAnimation: ValueAnimator

    //permission
    private var permissionsRequired = arrayOf(
                                                Manifest.permission.RECORD_AUDIO,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.READ_EXTERNAL_STORAGE
                                                )
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


    }

    private fun setRecordView(section : Int){
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
            if(checkPermission(CHECK_PERMISSION_WRITE_AND_RECORD)){
                reqPermission(CHECK_PERMISSION_WRITE_AND_RECORD)
            }
            else{
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
                        val value: Float = animation?.animatedValue as Float
                        recordPlay_CenterCircle.angle = value
                    }
            }

        imgAddcardCardImg.setOnClickListener {
            if(!checkPermission(CHECK_PERMISSION_READ)){
                reqPermission(CHECK_PERMISSION_READ)
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

        btnAddcardBack.setOnClickListener { finish() }

    }

    //녹음 시작
    private fun startRecording() {

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

    private fun checkPermission(checkWhich: Int) : Boolean{
        var result = false
        when(checkWhich){
            CHECK_PERMISSION_WRITE_AND_RECORD ->{
                if(checkSelfPermission(this@AddCardActivity, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED){
                    return result
                }
                if(checkSelfPermission(this, permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED){
                    return result
                }
                result = true
                return result
            }

            CHECK_PERMISSION_READ ->
                result = checkSelfPermission(this, permissionsRequired[2]) == PackageManager.PERMISSION_GRANTED
        }
        return result
    }

    private fun reqPermission(whichPermission: Int) {
        when(whichPermission){
            CHECK_PERMISSION_WRITE_AND_RECORD -> {
                if(
                    shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                ){
                    showAlertDialog()
                }
                else{
                   requestPermissions(
                            arrayOf(
                                permissionsRequired[0],
                                permissionsRequired[1]
                            ),
                            PERMISSION_CALLBACK_CONSTANT
                       )
                }
            }

            CHECK_PERMISSION_READ ->{
                if(shouldShowRequestPermissionRationale(this, permissionsRequired[2])){
                    showAlertDialog()
                }
                else{
                    requestPermissions(
                        arrayOf(permissionsRequired[2]),
                        PERMISSION_CALLBACK_CONSTANT
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_PERMISSION_SETTING -> {
                for (i in grantResults) {
                    if(i < 0){
                        Toast.makeText(applicationContext,
                            "해당권한을 활성화하셔야 합니다.",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage("권한이 거부되었습니다. 직접 권한을 허용해야 합니다.")
        builder.setPositiveButton("예") { dialog, _->
            dialog.cancel()
            intent = Intent()
            intent.apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
                //intent.putExtra("package", getPackageName()); // 어떤 애플리의 셋팅을 열지 모르기 때문에 우리 애플리케이션의 셋팅을 열도록 패키지를 넣어줘야함
            }
            startActivity(intent)
        }
        builder.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        builder.show()
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
        val visibility = false

        val titleRb = RequestBody.create(MediaType.parse("text/plain"), title)
        val descRb = RequestBody.create(MediaType.parse("text/plain"), desc)
        val visibilityRb = RequestBody.create(MediaType.parse("text/plain"), "false")

        val options = BitmapFactory.Options()
        val inputStream: InputStream = contentResolver.openInputStream(selectPicUri)!!
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)

        val photoBody = RequestBody.create(
            MediaType.parse("image/jpg"),
            byteArrayOutputStream.toByteArray()
        )

        val pictureRb = MultipartBody.Part.createFormData(
            "image",
            File(selectPicUri.toString()).name,
            photoBody
        )

        var audioFile: File? = null
        var audioRb: MultipartBody.Part? = null
        recordFileName?.let{
            val audioFile = File(it)
            val audioUri = Uri.fromFile(audioFile)
            val audioBody =
                RequestBody.create(
                    MediaType.parse(contentResolver.getType(audioUri).toString()),
                    audioFile
                )
            audioRb =
                MultipartBody.Part.createFormData("record", audioFile.name + ".mp3", audioBody)

        }


        // 수현 코드
//        var audioFile: File? = null
//        var audioUrl: Uri? = null
//        var audioBody: RequestBody? = null
//        var audioRb: MultipartBody.Part? = null
//        recordFileName?.let {
//            audioFile = File(it)
//            audioUrl = Uri.fromFile(audioFile)
//            audioBody =
//                RequestBody.create(
//                    MediaType.parse(contentResolver.getType(audioUrl).toString()),
//                    audioFile
//                )
//            audioBody = RequestBody.create(MediaType.parse("*/*"), audioFile!!)
//            audioRb = MultipartBody.Part.createFormData(
//                "audio",
//                audioFile?.name,
//                audioBody!!)
//        }


        "token: $token, title: $title, desc: $desc, visiblity: $visibility, picture_rb $pictureRb, selectPicUri : $selectPicUri, audioFileName : ${audioFile?.name}}".logDebug(
            this@AddCardActivity
        )

        cardDataRepository
            .addCard(
                token!!,
                titleRb,
                descRb,
                visibilityRb,
                pictureRb,
                audioRb
            )
            .enqueue(object : Callback<ResAddCard> {
                override fun onFailure(call: Call<ResAddCard>, t: Throwable) {
                    "Fail to Add Card, message:${t.message}".logDebug(this@AddCardActivity)
                }

                override fun onResponse(call: Call<ResAddCard>, response: Response<ResAddCard>) {
                    "onResponse : ${response.code()}".logDebug(this@AddCardActivity)
                    if (response.isSuccessful) {
                        response.body()
                            ?.let {
                                "status : ${it.status}, success: ${it.success}, data: ${it.data}, message : ${it.message}".logDebug(
                                    this@AddCardActivity
                                )
                                if (it.success) {
                                    val intent =
                                        Intent(this@AddCardActivity, DetailCardActivity::class.java)

                                    intent.putExtra("ADD_CARD", it.data?.cardIdx)
                                    startActivity(intent)
                                    this@AddCardActivity.finish()
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

    private fun getImageFromAlbum() {
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
        private const val CHECK_PERMISSION_READ = 102
        private const val CHECK_PERMISSION_WRITE_AND_RECORD = 103
    }

}

