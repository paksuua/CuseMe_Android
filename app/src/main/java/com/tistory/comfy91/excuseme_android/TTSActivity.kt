package com.tistory.comfy91.excuseme_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_tts.*
import java.util.*

class TTSActivity : AppCompatActivity() {
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tts)

        tts = TextToSpeech(applicationContext,
            TextToSpeech.OnInitListener { status ->
                if(status == TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale.KOREA).let {
                        if(it == TextToSpeech.LANG_MISSING_DATA
                            || it == TextToSpeech.LANG_NOT_SUPPORTED){
                            Toast.makeText(this@TTSActivity, "지금 지원되지 않습니다.", Toast.LENGTH_LONG).show()
                        } else{
                            btnTTS.isEnabled = true
                        }
                    }
                }
            })
        btnTTS.isEnabled = false

        btnTTS.setOnClickListener {
            edtTTS.text.toString().let{
                tts.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        if(tts != null){
            tts.stop()
            tts.shutdown()
        }
    }
}
