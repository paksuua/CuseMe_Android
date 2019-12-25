package com.tistory.comfy91.excuseme_android

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_add_card.*
import java.io.File
import java.io.IOException

class AddCardActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private lateinit var dialogBuilder: AlertDialog.Builder
    private var recorder: MediaRecorder? = null
    private lateinit var recordFileName: String
    private var startRecordFlag = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        dataInit()
        uiInit()

        checkPermission()
    } // end onCrate()

    companion object{
        // image pick code
        private val IMAGE_PICK_CODE = 1000
        // permission code
        private val PERMISSION_CODE = 1111

    }

    private fun dataInit(){
        // 파일 저장경로 지정
        Environment.getExternalStorageDirectory().let {
            File(it, "recorded.mp4").let{
                recordFileName = it.absolutePath
            }
        }
    }

    private fun uiInit(){

        dialogBuilder = AlertDialog.Builder(this)
        tvAddcardRecordCount.text = "10초"

        // 이미지 가져오기 리스너 설정
        imgAddcardCardImg.setOnClickListener{
            getImageFromAlbum()
        }

        // 녹음 버튼 리스너 설정
        btnAddcardRecord.setOnClickListener {
            lyAddcardRecord.isVisible = true
        }


        // 녹음 버튼
        btnAddcardRecord.setOnClickListener {
            startRecord()
        }


    }

    private fun getImageFromAlbum(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,IMAGE_PICK_CODE )
    }

    private fun checkPermission(){
        // 권한 동의 체크
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // 권한 동의 안된 경우
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {

                    // 사용자 권한 동의 팝업 띄움
                    dialogBuilder.setMessage("권한이 거부 되었습니다. 직접 권한을 허용하세요.")
                        .setPositiveButton("상세", DialogInterface.OnClickListener{dialogInterface, i ->
                            intent = Intent()
                                .apply {
                                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    data = Uri.fromParts("package", packageName, null)
                                    startActivity((intent))

                                }

                        })
                        .setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->
                            finish()
                        })

            }
            else{
                // 사용자 권한 동의 팝업 표시
                ActivityCompat.requestPermissions(this,
                    arrayOf<String>(
                        (Manifest.permission.READ_EXTERNAL_STORAGE),
                        (Manifest.permission.RECORD_AUDIO)
                    ),
                    PERMISSION_CODE)

            } // end if...else

        }
    } // end checkPermission()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            PERMISSION_CODE ->{
                for(i in 0 until grantResults.size){
                    if(grantResults[i] < 0){
                        Toast.makeText(applicationContext, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_LONG).show()
                        break
                    }
                }

            } // end 1111
        }
    } // end onRequestPermissionREsult()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "(requestCode : $requestCode) (resulrCode: $resultCode)")

        if(resultCode == Activity.RESULT_OK && requestCode== IMAGE_PICK_CODE){
            // 정상적으로 이미지를 가져온 경우
            Log.d(TAG, "Success Get Image from Gallery")
            imgAddcardCardImg.setImageURI(data?.data)
        }
    } // end onActivityResult()

    // 오디오 녹음
    private fun onAudioRecord(start: Boolean) = if(start) {
        startRecording()
    }
    else{
        stopRecording()
    }

    // 녹음 시작
    private fun startRecording(){
        recorder = MediaRecorder().apply{
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(recordFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try{
                prepare()
                start()
            }catch (e: IOException){
                Log.d(TAG,"Audio Record prepare() failed")
            }


        }
    } // end startRecording()

    private fun stopRecording(){
        recorder?.apply {
            stop()
            release()
            }

        recorder = null
    }  // end stopRecording()

    private fun startRecord(){
        onAudioRecord(startRecordFlag)
        startRecordFlag = !startRecordFlag
    }



} // end class
