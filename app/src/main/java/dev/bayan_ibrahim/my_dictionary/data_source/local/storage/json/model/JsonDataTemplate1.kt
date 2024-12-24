package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonDataTemplate1(
    @SerialName("version")
    val version: Int,
    @SerialName("languages")
    val languages: List<JsonDataTemplateLanguage1>,
    @SerialName("typeTags")
    val typeTags: List<JsonDataTemplateTypeTag1>,
    @SerialName("contextTags")
    val contextTags: List<JsonDataTemplateIdWithName1>,
    @SerialName("words")
    val words: List<JsonDataTemplateWord1>
)