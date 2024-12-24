package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonDataTemplateLanguage1(
    @SerialName("code")
    val code: String,
    @SerialName("typeTags")
    val typeTags: List<JsonDataTemplateIdWithName1>
)