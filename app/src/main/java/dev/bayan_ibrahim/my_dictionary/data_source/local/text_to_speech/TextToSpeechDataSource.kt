package dev.bayan_ibrahim.my_dictionary.data_source.local.text_to_speech

import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

interface TextToSpeechDataSource {
    fun pushData(data: TextToSpeechData)
    val runningId: StateFlow<String?>
    fun isSupportedLanguage(language: LanguageCode): Boolean
    fun isSupportedLocale(locale: Locale): Boolean
}