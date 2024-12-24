package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonDataTemplateIdWithName1(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)