package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonDataTemplateWord1(
    @SerialName("language")
    val language: String,
    @SerialName("meaning")
    val meaning: String,
    @SerialName("translation")
    val translation: String,
    @SerialName("transcription")
    val transcription: String,
    @SerialName("additionalTranslations")
    val additionalTranslations: List<String>,
    @SerialName("examples")
    val examples: List<String>,
    @SerialName("contextTags")
    val contextTags: List<Int>,
    @SerialName("typeTag")
    val typeTag: Int,
    @SerialName("relatedWords")
    val relatedWords: List<JsonDataTemplateIdWithName1>,
)