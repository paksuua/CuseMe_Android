package com.tistory.comfy91.excuseme_android.feature.detailcard

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tistory.comfy91.excuseme_android.*
import com.tistory.comfy91.excuseme_android.data.CardBean
import com.tistory.comfy91.excuseme_android.data.ResCardDetail
import com.tistory.comfy91.excuseme_android.data.ResCards
import com.tistory.comfy91.excuseme_android.data.SingletoneToken
import com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepository
import com.tistory.comfy91.excuseme_android.data.server.BodyDeleteCard
import com.tistory.comfy91.excuseme_android.feature.helper.SelectSortFragment
import com.tistory.comfy91.excuseme_android.feature.helper_sort.HelperSortActivity
import com.tistory.comfy91.excuseme_android.feature.modcard.ModCardActivity
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_detail_card.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class DetailCardActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null
    private var playFlag = false
    private lateinit var recordFileName: String
    private var card: CardBean? = null
    private lateinit var dialogBuilder: AlertDialog.Builder
    private val cardDataRepository = ServerCardDataRepository()
    private var token = SingletoneToken.getInstance().token

    private lateinit var dialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_card)

        initData()
        getCard()
        initUi()

        ctvDetaliRecordPlay.setOnClickListener {
            if(playFlag){
                play()
            }else{
                stopPlaying()
            }
        }
    }

    private fun initData(){dialog = AlertDialog.Builder(this)}

    private fun getCard(){
        intent.getSerializableExtra("CARD_DATA")?.let{
            card = it as CardBean
            return
        }

        intent.getSerializableExtra("DOWN_CARD")?.let {
            card = it as CardBean
            showSelectVisibility(card!!)
            return
        }
    }

    private fun showSelectVisibility(card: CardBean){
        dialogBuilder.apply{
            setMessage("보이는 카드 목록에\n바로 추가하시겠습니까?")
            setPositiveButton("추가") { dialogInterface, _ ->
                card.visibility = true
                requestCardEdit(card,dialogInterface)
            }
            setNegativeButton("취소") { _, _ -> finish() }
            setCancelable(false)
            show()
        }
    }

    private fun requestCardEdit(card: CardBean, dialog: DialogInterface){
        if(token == null){
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWR4IjozOSwidXVpZCI6ImYzZDViM2E1LTkwYjYtNDVlMy1hOThhLTEyODE5OWNmZTg1MCIsImlhdCI6MTU3NzkwMTA1MywiZXhwIjoxNTc3OTg3NDUzLCJpc3MiOiJnYW5naGVlIn0.QytUhsXf4bJirRR_zF3wdACiNu9ytwUE4mrPSNLCFLk"
        }
        readyForRequest(dialog)
    }

    private fun readyForRequest(dialog: DialogInterface){
        val title_rb = RequestBody.create(MediaType.parse("text/plain"), card?.title)
        val content_rb = RequestBody.create(MediaType.parse("text/plain"), card?.desc)

        val options = BitmapFactory.Options()
        val uri = Uri.fromFile(File(card?.imageUrl))
        val inputStream: InputStream = contentResolver.openInputStream(uri)!!
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        val byteArrayOutPutStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutPutStream)

        // photo
        val photoBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutPutStream.toByteArray())
        val photo_rb = MultipartBody.Part.createFormData("image", File(uri.toString()).name, photoBody)

        // audio
        val audioFile = File(card?.audioUrl)
        val audioUri = Uri.fromFile(audioFile)
        val audioBody = RequestBody.create(MediaType.parse(contentResolver.getType(audioUri)), audioFile)
        val audio_rb = MultipartBody.Part.createFormData("audio", audioFile.name,audioBody)
        "token : $token, title_rb: $title_rb, content_rb: $content_rb".logDebug(this@DetailCardActivity)

        cardDataRepository.editCardDetail(
            token!!,
            card?.cardIdx.toString(),
            title_rb,
            content_rb,
            card?.visibility!!,
            photo_rb,
            audio_rb
        ).enqueue(object: Callback<ResCards>{
            override fun onFailure(call: Call<ResCards>, t: Throwable) {
                "Fail to Edit Card, message : ${t.message}".logDebug(this@DetailCardActivity)
            }

            override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                "code : ${response.code()}, message : ${response.message()}"
                if(response.isSuccessful){
                    response.body()?.let{
                        "status: ${it.status}, success: ${it.success}, message : ${it.message}, data: ${it.data}".logDebug(this@DetailCardActivity)
                        dialog.dismiss()
                    }
                }
                else{
                    "response is Not Success = Body is Empty".logDebug(this@DetailCardActivity)
                }
            }
        })
    }

    private fun initUi(){
        dialogBuilder = AlertDialog.Builder(this@DetailCardActivity)

        card?.let{
            Glide.with(this).load(it.imageUrl).into(imgDetailCardImg)
            tvDetailCardTitle.text = it.title
            tvDetailCardDesc.text = it.desc
        }

        btnDetailBack.setOnSingleClickListener { finish() }

        btnDetailDelete.setOnSingleClickListener{

            dialogBuilder
                .setMessage("카드를 완젼히\n삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ -> deleteCard() }
                .setNegativeButton("취소") { _, _ -> finish() }
                .setCancelable(false)
                .show()

        }

        btnDetailEdit.setOnClickListener {
            val intent = Intent(this@DetailCardActivity, ModCardActivity::class.java)
            intent.putExtra("CARD_DATA", card)
            startActivityForResult(intent, MODIFY_CARD)
        }

        // Record to the external cache directory for visibility
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        recordFileName = "${externalCacheDir?.absolutePath}/audiorecord$timeStamp.3gp"

        // 실행(count) 버튼 리스너 설정
        //tvAddcardRecordPlay.setOnClickListener{ play() }
    }


    private fun deleteCard(){
        if(token == null){
            token = "token"
        }
        requestDeleteCard(token!!)
    }

    private fun requestDeleteCard(token: String){
        cardDataRepository
            .deleteCard(token, BodyDeleteCard(card!!.cardIdx) )
            .enqueue(object: Callback<ResCards> {
                override fun onFailure(call: Call<ResCards>, t: Throwable) {
                    "request delete card is fail message: ${t.message}".logDebug(this@DetailCardActivity)
                }

                override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {
                    if(response.isSuccessful){
                        response
                            .body()!!
                            .let{res ->
                                "request delete card is success".logDebug(this@DetailCardActivity)
                                if(res.success){this@DetailCardActivity.finish()}
                                else{}
                            }
                    }
                    else{
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
                ctvAddcardRecordPlay.isChecked = false
            } catch (e: IOException) {
                "prepare() failed".logDebug(this@DetailCardActivity)
                //Log.e(TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            MODIFY_CARD ->{
                if(resultCode == Activity.RESULT_OK){
                    "카드가 수정되었습니다.".toast(this)
                }
                else{
                    "modify card result is not result ok, resultCode: ${resultCode}".logDebug(this@DetailCardActivity)
                }
            }
            DELETE_CARD ->{
                if(resultCode == Activity.RESULT_OK){

                }
                else{
                    "delete card result is not result ok, resultCode: ${resultCode}".logDebug(this@DetailCardActivity)
                }
            }
            else ->{
                "requestCode : ${requestCode} resultCode : ${resultCode}".logDebug(this@DetailCardActivity)
            }
        }
    }
    companion object{
        private const val MODIFY_CARD = 3333
        private const val DELETE_CARD = 2222
    }
}
