package com.tistory.comfy91.excuseme_android.feature.detailcard

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.*
import com.tistory.comfy91.excuseme_android.data.*
import com.tistory.comfy91.excuseme_android.data.answer.ResCardDetail
import com.tistory.comfy91.excuseme_android.data.answer.ResCards
import com.tistory.comfy91.excuseme_android.data.answer.ResDownCard
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.data.server.BodyChangeVisibility
import com.tistory.comfy91.excuseme_android.feature.disabled.DisabledActivity
import com.tistory.comfy91.excuseme_android.feature.modcard.ModCardActivity
import kotlinx.android.synthetic.main.activity_detail_card.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*

class DetailCardActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null
    private var playFlag = false
    private var card: CardBean? = null
    private lateinit var dialogBuilder: AlertDialog.Builder
    private val cardDataRepository = ServerCardDataRepository()
    private var token = SingletoneToken.getInstance().token

    //TTS
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_card)

        initData()
        getCard()
        initUi()
    }

    private fun initData() {
        dialogBuilder = AlertDialog.Builder(this)
        tts = TextToSpeech(
            applicationContext,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.KOREA).let {
                        if (it == TextToSpeech.LANG_MISSING_DATA
                            || it == TextToSpeech.LANG_NOT_SUPPORTED
                        ) {
                            Toast.makeText(
                                this@DetailCardActivity,
                                "현재 음성 기능이 지원되지 않습니다.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        )
    }

    private fun getCard() {
        intent.getSerializableExtra("CARD_DATA")?.let {
            card = it as CardBean
            return
        }

        intent.getSerializableExtra("DOWN_CARD")?.let {
            card = it as CardBean
//            imageUri= Uri.parse(intent.getStringExtra("MOD_CARD_IMG_URI"))
//            showSelectVisibility(card!!)
            return
        }

        intent.getSerializableExtra("ADD_CARD")?.let {
            val cardIdx = it as String
            showSelectVisibility(cardIdx)
        }

        intent.getSerializableExtra("FROM_NEW_HELPER")?.let {
            card = it as CardBean
            btnDetailEdit.performClick()
            return
        }


    }

    private fun showSelectVisibility(cardIdx: String) {

        requestCardDetail(cardIdx)

        dialogBuilder.apply {
            setMessage("보이는 카드 목록에\n바로 추가하시겠습니까?")
            setPositiveButton("추가") { dialogInterface, _ ->

                dialogInterface.cancel()

            }

            setNegativeButton("취소") { dialogInterface, _ ->

                ctvDetailTog.isChecked = false
                requestHide()
                dialogInterface.cancel()
            }
            setCancelable(false)
            show()
        }
    }

    private fun initUi() {
        card?.let {
            Glide.with(this).load(it.imageUrl).into(imgDetailCardImg)
            tvDetailCardTitle.text = it.title
            tvDetailCardDesc.text = it.desc
            ctvDetailTog.isChecked = it.visibility
            tvCardNum.text = "일련번호 | ${it.serialNum}"
            ctvDetailTog.isChecked = card!!.visibility

        }

        ctvDetaliRecordPlay.setOnClickListener { play() }
        btnDetailBack.setOnSingleClickListener { finish() }
        btnDetailDelete.setOnSingleClickListener {
            dialogBuilder
                .setMessage("카드를 완젼히\n삭제하시겠습니까?")
                .setPositiveButton("삭제") { dialog, _ -> deleteCard(dialog) }
                .setNegativeButton("취소") { _, _ -> finish() }
                .setCancelable(false)
                .show()
        }

        btnDetailEdit.setOnClickListener {
            val intent = Intent(this@DetailCardActivity, ModCardActivity::class.java)
            intent.putExtra("CARD_DATA", card)
            startActivityForResult(intent, MODIFY_CARD)
        }


        // 실행(count) 버튼 리스너 설정
        ctvDetaliRecordPlay.setOnClickListener {
            play()
        }

    }

    private fun deleteCard(dialog: DialogInterface) {
        if (token == null) {
            token = "token"
        }
        requestDeleteCard(token!!, dialog)
    }


    private fun requestDeleteCard(token: String, dialog: DialogInterface) {
        cardDataRepository
            .deleteCard(token, card!!.cardIdx.toString())
            .enqueue(object : Callback<ResCards> {
                override fun onFailure(call: Call<ResCards>, t: Throwable) {
                    "request delete card is fail message: ${t.message}".logDebug(this@DetailCardActivity)
                }

                override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                    if (response.isSuccessful) {
                        response
                            .body()!!
                            .let { res ->
                                "request delete card is success".logDebug(this@DetailCardActivity)
                                if (res.success) {
                                    dialog.dismiss()
                                    this@DetailCardActivity.finish()

                                } else {
                                }
                            }
                    } else {
                        "request delete card is not success".logDebug(this@DetailCardActivity)
                    }
                }

            })
    }


    private fun getBitmapFromAssets(fileName: String): Bitmap? {
        return try {
            BitmapFactory.decodeStream(assets.open(fileName))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun play() {
        if (card?.audioUrl.isNullOrEmpty()) {
            tts.speak(card?.desc, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            onPlay(playFlag)
            playFlag = !playFlag
        }

    }

    private fun requestCardDetail(cardIdx: String) {
        if (token == null) {
            token =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWR4IjozOSwidXVpZCI6ImYzZDViM2E1LTkwYjYtNDVlMy1hOThhLTEyODE5OWNmZTg1MCIsImlhdCI6MTU3NzkwMTA1MywiZXhwIjoxNTc3OTg3NDUzLCJpc3MiOiJnYW5naGVlIn0.QytUhsXf4bJirRR_zF3wdACiNu9ytwUE4mrPSNLCFLk"
        }
        cardDataRepository.getCardDetail(token!!, cardIdx)
            .enqueue(object : Callback<ResCardDetail> {
                override fun onFailure(call: Call<ResCardDetail>, t: Throwable) {
                    "Fail Test Get Card Detail, message : ${t.message}".logDebug(this@DetailCardActivity)

                }

                override fun onResponse(
                    call: Call<ResCardDetail>,
                    response: Response<ResCardDetail>
                ) {
                    "responser code : ${response.code()}, response message:  ${response.message()}".logDebug(
                        this@DetailCardActivity
                    )
                    if (response.isSuccessful) {
                        response.body().let { body ->
                            "status: ${body!!.status} data : ${body.data}".logDebug(this@DetailCardActivity)
                            if (body.success) {
                                card = body.data
                                card?.let {
                                    Glide.with(this@DetailCardActivity).load(it.imageUrl)
                                        .into(imgDetailCardImg)
                                    tvDetailCardTitle.text = it.title
                                    tvDetailCardDesc.text = it.desc
                                    ctvDetailTog.isChecked = it.visibility
                                    tvCardNum.text = "일련번호 | ${it.serialNum}"
                                }

                                when (card?.audioUrl.isNullOrEmpty()) {
                                    true -> {
                                        card?.desc.let {
                                            tts.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)
                                        }
                                    }
                                    false -> {
                                        card?.audioUrl = body.data?.audioUrl.toString()
                                        onPlay(playFlag)
                                        playFlag != playFlag
                                    }
                                }
                            } else {
                                "Resonse is not Success : body is empty".logDebug(this@DetailCardActivity)
                            }
                        }
                    } else {
                        "response.is not success".logDebug(DisabledActivity::class.java)
                    }
                }

            })
    }


    private fun onPlay(start: Boolean) = if (start) startPlaying() else stopPlaying()

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(card?.audioUrl)
                prepare()
                start()
            } catch (e: IOException) {
                "prepare() failed".logDebug(this@DetailCardActivity)
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }


    //취소 선택 시 카드 숨김
    private fun requestHide() {
        cardDataRepository.changeVisibilty(
            token!!,
            BodyChangeVisibility(false),
            card?.cardIdx.toString()
        ).enqueue(object : Callback<ResCards> {
            override fun onFailure(call: Call<ResCards>, t: Throwable) {
                "카드 추가 숨김 실패했습니다: ${t.message}".logDebug(this@DetailCardActivity)
            }

            override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                if (response.isSuccessful) {
                    val resBody = response.body()
                    "${resBody?.success}".logDebug(this@DetailCardActivity)
                } else {
                    "${response.code()}".logDebug(this@DetailCardActivity) // 200~300말고 다른 코드를 알 수 ㅇ
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MODIFY_CARD -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        card = (data?.getSerializableExtra("MOD_CARD")) as CardBean?
                        card?.let {
//                            showSelectVisibility(it)
                        }


                    }
                    Activity.RESULT_CANCELED -> {
                        "카드 수정 취소함".logDebug(this@DetailCardActivity)
                    }
                    else -> "modify card result is not result ok, resultCode: ${resultCode}".logDebug(
                        this@DetailCardActivity
                    )
                }
            }
            DELETE_CARD -> {
                if (resultCode == Activity.RESULT_OK) {

                } else {
                    "delete card result is not result ok, resultCode: ${resultCode}".logDebug(this@DetailCardActivity)
                }
            }
            else -> {
                "requestCode : ${requestCode} resultCode : ${resultCode}".logDebug(this@DetailCardActivity)
            }
        }
    }


    override fun onStop() {
        super.onStop()
        stopPlaying()
    }


    companion object {
        private const val MODIFY_CARD = 3333
        private const val DELETE_CARD = 2222
    }
}