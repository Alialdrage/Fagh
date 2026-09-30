package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Text-To-Speech manager configured for Arabic language voice synthesis.
 */
class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsEngine = tts ?: return
            val result = ttsEngine.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("TtsManager", "Arabic TTS language is missing data or not supported. Falling back to default locale.")
                ttsEngine.setLanguage(Locale.getDefault())
            }
            ttsEngine.setPitch(1.0f)
            ttsEngine.setSpeechRate(0.95f) // slightly slower for clear religious Arabic reading
            ttsEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    _currentUtteranceId.value = utteranceId
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentUtteranceId.value = null
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentUtteranceId.value = null
                }
            })
            isInitialized = true
        } else {
            Log.e("TtsManager", "TextToSpeech initialization failed with status: $status")
        }
    }

    fun speak(text: String, utteranceId: String = "general_speech") {
        if (!isInitialized || tts == null) return
        stop()
        _currentUtteranceId.value = utteranceId
        _isSpeaking.value = true
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        if (tts != null) {
            tts?.stop()
        }
        _isSpeaking.value = false
        _currentUtteranceId.value = null
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
