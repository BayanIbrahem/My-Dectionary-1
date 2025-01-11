package dev.bayan_ibrahim.my_dictionary.data_source.local.text_to_speech

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class TextToSpeechData(
    val text: String,
    val language: LanguageCode,
    val speechRate: Float = 1.0f,
    val flushPrev: Boolean = true,
    val id: String = "$text-$language-$speechRate-$flushPrev",
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextToSpeechData) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}

private const val TAG = "text_to_speech"

class MDTllTextToSpeechDataSource(
    @ApplicationContext
    private val context: Context,
) : TextToSpeechDataSource {
    private val queue = mutableListOf<TextToSpeechData>()
    private var textToSpeech: TextToSpeech = TextToSpeech(context) {
        startNextText()
    }.also {
        it.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _runningId.value = utteranceId
            }

            override fun onDone(utteranceId: String?) {
                _runningId.value = null
                queue.removeAt(0)
                startNextText()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _runningId.value = null
            }
        })
    }
    private val _runningId = MutableStateFlow<String?>(null)
    override val runningId = _runningId.asStateFlow()

    override fun pushData(data: TextToSpeechData) {
        if (data.flushPrev) {
            queue.clear()
            queue.add(data)
        } else {
            val wasNotEmpty = queue.isNotEmpty()
            queue.add(data)
            if (wasNotEmpty) return
        }
        startNextText()
    }

    private fun startNextText() {
        val data = queue.firstOrNull() ?: return
        textToSpeech.stop()
        textToSpeech(data)
    }

    override fun isSupportedLanguage(
        language: LanguageCode,
    ): Boolean = isSupportedLocale(Locale.forLanguageTag(language.code))

    override fun isSupportedLocale(
        locale: Locale,
    ): Boolean = textToSpeech.isLanguageAvailable(locale) == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE

    private fun textToSpeech(
        data: TextToSpeechData,
    ) {
        val locale = Locale.forLanguageTag(data.language.code)
        if (isSupportedLocale(locale)) {
            textToSpeech.language = locale
            textToSpeech.setSpeechRate(data.speechRate)
            textToSpeech.speak(
                data.text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            )
        } else {
            Log.e(TAG, "unsupported locale, ${locale.language}")
            try {
                val intent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "language ${locale.language} is not existed and can not request downloading tts data for language")
            }
        }
    }
}