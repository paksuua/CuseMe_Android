package com.tistory.comfy91.excuseme_android.feature.addcard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.logDebug
import kotlinx.android.synthetic.main.activity_add_card.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddCardActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private lateinit var dialogBuilder: AlertDialog.Builder
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private lateinit var audioTimer: AudioTimer

    private lateinit var recordFileName: String
    private var recordFlag = true
    private var playFlag = true
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

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
        recordFileName = "${externalCacheDir?.absolutePath}/audiorecord$timeStamp.3gp"
        bottomSheetBehavior = BottomSheetBehavior.from(lyAddCardRecord)

    }

    private fun initUI() {
        dialogBuilder = AlertDialog.Builder(this)

        // 이미지 가져오기 리스너 설정
        imgAddcardCardImg.setOnClickListener {
            getImageFromAlbum()
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        // 녹음 버튼 리스너 설정
        btnAddcardRecord.setOnClickListener {
            it.requestFocus()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            btnAddcardRecord.isVisible = false
        }
        // 녹음 버튼
        btnAddcardTogRecord.setOnClickListener { record() }

        // 실행(count) 버튼 리스너 설정
        tvAddcardRecordPlay.setOnClickListener{ play() }



        btnAddcardSaveRecord.isEnabled = false
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
                "prepare() failed".logDebug(this@AddCardActivity)
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

    private fun onRecord(start: Boolean) = if (start) startRecording() else stopRecording()

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

            audioTimer =
                AudioTimer(this@AddCardActivity) {
                    tvAddCardRecordNotice.text = "${audioTimer.count}초"
                }
            audioTimer.start()

            tvAddcardRecordPlay.isVisible = false
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
        tvAddcardRecordPlay.apply{
            isVisible = true
            isEnabled = true
        }
    }

    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,
            IMAGE_PICK_CODE
        )
    }

    fun Context.isPermissionNotGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
    }

    fun Context.startSettingActivity() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        })
    }

    private fun checkPermission() {
        if (isPermissionNotGranted(Manifest.permission.RECORD_AUDIO)
            || isPermissionNotGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
            || isPermissionNotGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                    .setPositiveButton("상세") { _, _ -> startSettingActivity() }
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
                        imgAddcardCardImg.setImageURI(data?.data)
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
    }
}
