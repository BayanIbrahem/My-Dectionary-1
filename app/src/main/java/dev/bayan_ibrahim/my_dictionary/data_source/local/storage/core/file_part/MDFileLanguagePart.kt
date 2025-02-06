package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part

import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language

interface MDFileLanguagePart : MDFilePart {
    val code: String
    val wordsClasses: List<LanguageWordClass>

    interface LanguageWordClass: StrIdentifiable {
        val relations: List<StrIdentifiable>
        fun toWordClass(language: Language): WordClass
    }

    fun toLanguage(): Language
    fun toWordsClasses(): List<WordClass> = wordsClasses.map { it.toWordClass(toLanguage()) }
}
