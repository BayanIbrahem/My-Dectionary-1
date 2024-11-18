package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes

import androidx.annotation.Size
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.MDRawWordJsonSerializer
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.MDRawWordTypeTagRelationJsonSerializer
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.MDRawWordTypeTagJsonSerializer
import kotlinx.serialization.Serializable

@Serializable(MDRawWordJsonSerializer::class)
data class MDRawWord(
    val meaning: String,
    val translation: String,
    val language: String,
    val additionalTranslations: List<String> = emptyList(),
    val examples: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val transcription: String = "",
    val wordTypeTag: MDRawWordTypeTag? = null,
) {
    val isBlank: Boolean get() = this == blankRawWord

}
val blankRawWord = MDRawWord(meaning = "", translation = "", language = "")

@Serializable(MDRawWordTypeTagJsonSerializer::class)
data class MDRawWordTypeTag(
    val name: String,
    @Size(min = 1)
    val relations: List<MDRawWordRelation>,
) {
    val isBlank: Boolean get() = this == blankRawWordType

}
val blankRawWordType: MDRawWordTypeTag = MDRawWordTypeTag("", emptyList())

@Serializable(MDRawWordTypeTagRelationJsonSerializer::class)
data class MDRawWordRelation(
    val label: String,
    val relatedWord: String,
) {
    val isBlank: Boolean get() = this == blankRawWordRelation

}
val blankRawWordRelation = MDRawWordRelation("", "")
