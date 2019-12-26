package com.tistory.comfy91.excuseme_android

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_add_card.*
import java.io.File
import java.io.IOException

class AddCardActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private lateinit var dialogBuilder: AlertDialog.Builder
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private lateinit var recordFileName: String
    private var recordFlag = true
    private var playFlag = true
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        checkPermission()

        dataInit()
        uiInit()

    } // end onCrate()


    private fun dataInit(){
        // Record to the external cache directory for visibility
        recordFileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"
        bottomSheetBehavior = BottomSheetBehavior.from(lyAddCardRecord)
    }


    private fun uiInit() {

        dialogBuilder = AlertDialog.Builder(this)
        tvAddcardRecordCount.text = "10초"

        // 이미지 가져오기 리스너 설정
        imgAddcardCardImg.setOnClickListener {
            getImageFromAlbum()
        }


        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        // 녹음 버튼 리스너 설정
        btnAddcardRecord.setOnClickListener {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            btnAddcardRecord.isVisible = false
        }
        // 녹음 버튼
        btnAddcardTogRecord.setOnClickListener {
            record()
        }

        // 실행(count) 버튼 리스너 설정
        tvAddcardRecordCount.setOnClickListener{
            play()
        }

        btnAddcardCancelRecord.setOnClickListener {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
            btnAddcardRecord.isVisible = true

        }
    }

    private fun play(){
        onPlay(playFlag)
        tvAddCardRecordNotice.text = when (playFlag) {
            true -> "Stop playing"
            false -> "Start playing"
        }
        tvAddcardRecordCount.isVisible = true

        playFlag = !playFlag
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(recordFileName)
                prepare()
                start()
            } catch (e: IOException) {
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
        btnAddcardTogRecord.text = when (recordFlag) {
            true -> "Stop recording"
            false -> "Start recording"
        }

        recordFlag = !recordFlag
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(recordFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }



    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun checkPermission() {
        // 권한 동의 체크
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 동의 안된 경우
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

                // 사용자 권한 동의 팝업 띄움
                dialogBuilder.setMessage("권한이 거부 되었습니다. 직접 권한을 허용하세요.")
                    .setPositiveButton("상세", DialogInterface.OnClickListener { dialogInterface, i ->
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

            } else {
                // 사용자 권한 동의 팝업 표시
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
                for (i in 0 until grantResults.size) {
                    if (grantResults[i] < 0) {
                        Toast.makeText(applicationContext, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_LONG)
                            .show()
                        break
                    }
                }

            } // end 1111
        }
    } // end onRequestPermissionREsult()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "(requestCode : $requestCode) (resulrCode: $resultCode)")

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            // 정상적으로 이미지를 가져온 경우
            Log.d(TAG, "Success Get Image from Gallery")
            imgAddcardCardImg.setImageURI(data?.data)
        }
    } // end onActivityResult()
    // 오디오 녹음


    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }


    companion object {
        // image pick code
        private const val IMAGE_PICK_CODE = 1000
        // permission code
        private const val PERMISSION_CODE = 1111

    }

}
