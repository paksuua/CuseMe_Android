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
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.*
import com.tistory.comfy91.excuseme_android.data.*
import com.tistory.comfy91.excuseme_android.data.answer.ResCardDetail
import com.tistory.comfy91.excuseme_android.data.answer.ResCards
import com.tistory.comfy91.excuseme_android.data.answer.ResDownCard
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.feature.disabled.DisabledActivity
import com.tistory.comfy91.excuseme_android.feature.modcard.ModCardActivity
import kotlinx.android.synthetic.main.activity_add_card.*
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
    private var recordFileName: String? = null
    private var card: CardBean? = null
    private lateinit var dialogBuilder: AlertDialog.Builder
    private val cardDataRepository = ServerCardDataRepository()
    private var imageUri: Uri? = null
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
            showSelectVisibility(card!!)
            return
        }

        intent.getSerializableExtra("FROM_NEW_HELPER")?.let {
            card = it as CardBean
            btnDetailEdit.performClick()
            return
        }



    }

    private fun showSelectVisibility(card: CardBean) {
        dialogBuilder.apply {
            setMessage("보이는 카드 목록에\n바로 추가하시겠습니까?")
            setPositiveButton("추가") { dialogInterface, _ ->
                card.visibility = true
                requestCardEdit(card, dialogInterface)
            }
            setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.cancel()}
            setCancelable(false)
            show()
        }
    }

    private fun requestCardEdit(card: CardBean, dialog: DialogInterface) {
        if (token == null) {
            token =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWR4IjozOSwidXVpZCI6ImYzZDViM2E1LTkwYjYtNDVlMy1hOThhLTEyODE5OWNmZTg1MCIsImlhdCI6MTU3NzkwMTA1MywiZXhwIjoxNTc3OTg3NDUzLCJpc3MiOiJnYW5naGVlIn0.QytUhsXf4bJirRR_zF3wdACiNu9ytwUE4mrPSNLCFLk"
        }
        readyForRequest(card, dialog)
    }

    private fun readyForRequest(card: CardBean, dialog: DialogInterface) {

        val title_rb = RequestBody.create(MediaType.parse("text/plain"), card.title)
        val content_rb = RequestBody.create(MediaType.parse("text/plain"), card.desc)

        var photo_rb: MultipartBody.Part? = null
        card.imageUrl.let{
            val uri = Uri.parse(it)
            val options = BitmapFactory.Options()

            val inputStream: InputStream = contentResolver.openInputStream(uri)!!
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            val byteArrayOutPutStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutPutStream)

            // photo
            val photoBody =
                RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutPutStream.toByteArray())
            photo_rb = MultipartBody.Part.createFormData("image", File(it.toString()).name, photoBody)
        }


        // audio
        var audio_rb: MultipartBody.Part? = null
        card.audioUrl = null
        card.audioUrl?.let{
            val audioFile = File(it)
//        val fileOutputStream = audioFile.outputStream()
            val audioBody = RequestBody.create(MediaType.parse("audio/mpeg"), audioFile)
            audio_rb = MultipartBody.Part.createFormData("audio", audioFile.name, audioBody)
        }
//        val audioFile = File(card?.audioUrl)
//        val fileOutputStream = audioFile.outputStream()
//        val audioBody = RequestBody.create(MediaType.parse("audio/mpeg"), audioFile)
//        val audio_rb = MultipartBody.Part.createFormData("audio", audioFile.name, audioBody)
        "token : $token, title_rb: $title_rb, content_rb: $content_rb".logDebug(this@DetailCardActivity)

        cardDataRepository.editCardDetail(
            token!!,
            card.cardIdx.toString(),
            title_rb,
            content_rb,
            card.visibility,
            photo_rb,
            audio_rb
        ).enqueue(object : Callback<ResDownCard> {
            override fun onFailure(call: Call<ResDownCard>, t: Throwable) {
                "Fail to Edit Card, message : ${t.message}".logDebug(this@DetailCardActivity)
            }

            override fun onResponse(call: Call<ResDownCard>, response: Response<ResDownCard>) {
                "code : ${response.code()}, message : ${response.message()}"
                if (response.isSuccessful) {
                    response.body()?.let {
                        "status: ${it.status}, success: ${it.success}, message : ${it.message}, data: ${it.data}".logDebug(
                            this@DetailCardActivity
                        )
                        dialog.dismiss()
                    }
                } else {
                    "response is Not Success = Body is Empty".logDebug(this@DetailCardActivity)
                }
            }
        })
    }

    private fun initUi() {
        card?.let {
            Glide.with(this).load(it.imageUrl).into(imgDetailCardImg)
            tvDetailCardTitle.text = it.title
            tvDetailCardDesc.text = it.desc
            ctvDetailTog.isChecked=it.visibility
            tvCardNum.text="일렬번호 | "+it.serialNum
        }

        ctvDetaliRecordPlay.setOnClickListener {play()}
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
            playCardAudio()
        }
    }

    private fun playCardAudio() {
        play()
    }


    private fun deleteCard(dialog: DialogInterface) {
        if (token == null) {
            token = "token"
        }
        requestDeleteCard(token!!, dialog)
    }

    private fun requestDeleteCard(token: String,dialog: DialogInterface) {
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
            requestCardDetail()
        } else {
            onPlay(playFlag)
            playFlag = !playFlag
        }

    }

    private fun requestCardDetail() {
        if (token == null) {
            token =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWR4IjozOSwidXVpZCI6ImYzZDViM2E1LTkwYjYtNDVlMy1hOThhLTEyODE5OWNmZTg1MCIsImlhdCI6MTU3NzkwMTA1MywiZXhwIjoxNTc3OTg3NDUzLCJpc3MiOiJnYW5naGVlIn0.QytUhsXf4bJirRR_zF3wdACiNu9ytwUE4mrPSNLCFLk"
        }
        cardDataRepository.getCardDetail(token!!, card?.cardIdx.toString())
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
                                when (body.data?.audioUrl.isNullOrEmpty()) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MODIFY_CARD -> {
                when(resultCode){
                    Activity.RESULT_OK ->{
                        card = (data?.getSerializableExtra("MOD_CARD")) as CardBean?
                        card?.let{
                            showSelectVisibility(it)
                        }


                    }
                    Activity.RESULT_CANCELED ->{"카드 수정 취소함".logDebug(this@DetailCardActivity)}
                    else ->"modify card result is not result ok, resultCode: ${resultCode}".logDebug(this@DetailCardActivity)
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
