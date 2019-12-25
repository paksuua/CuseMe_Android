package com.tistory.comfy91.excuseme_android

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import org.w3c.dom.Text
import java.util.*

/**
 * 사용법:
 * 1. 사용하는 Activity에서 onDestroy 함수를 ovveride하고 거기에서 stop() 함수를 꼭 호출!!!
 */
class TTS(private val context: Context, private val listener: TextToSpeech.OnInitListener)
    : TextToSpeech.OnInitListener{
    private val textToSpeech = TextToSpeech(context, listener)
    init {

    }
    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){

            // 언어설정
            val result = textToSpeech.setLanguage(Locale.KOREA)

            if(result == TextToSpeech.LANG_MISSING_DATA){
                Toast.makeText(context, "지언하지 않는 언어입니다.", Toast.LENGTH_LONG).show()
                return
            }


        }

    } // onInit()

    /**
     * onDestroy에서 꼭 호출되어야 하는 함수
     */
    fun stop(){
        if(textToSpeech != null){
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }


    /**
     * TTS 실행
     */
    fun speakOut(text: String){
        if(textToSpeech != null){
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

}