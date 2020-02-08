package com.tistory.comfy91.excuseme_android.feature

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.*

class TTS{
    companion object{
        private var instance: TextToSpeech? = null

        fun getInstance(context: Context): TextToSpeech =
            instance ?: synchronized(this){
                instance?: TextToSpeech(context,
                    TextToSpeech.OnInitListener { status ->
                        if (status == TextToSpeech.SUCCESS) {
                            instance?.setLanguage(Locale.KOREA).let {
                                if (it == TextToSpeech.LANG_MISSING_DATA
                                    || it == TextToSpeech.LANG_NOT_SUPPORTED
                                ) {
                                    Toast.makeText(context, "지금 지원되지 않습니다.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }).also{
                    instance = it
                }
            }
    }
}