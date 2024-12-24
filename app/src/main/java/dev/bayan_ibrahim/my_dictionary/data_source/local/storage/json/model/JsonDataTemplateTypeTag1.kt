package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonDataTemplateTypeTag1(
    @SerialName("tagId")
    val tagId: Int,
    @SerialName("relatedLabels")
    val relatedLabels: List<JsonDataTemplateIdWithName1>
)