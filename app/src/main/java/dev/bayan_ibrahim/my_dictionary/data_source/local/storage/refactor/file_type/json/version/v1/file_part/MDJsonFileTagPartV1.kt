package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_part

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.core.file_part.MDJsonFileTagPart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MDJsonFileTagPartV1(
    @SerialName(ID_KEY)
    override val id: Long? = null,
    @SerialName(VALUE_KEY)
    override val name: String,
) : MDJsonFileTagPart {
    @Transient
    override val version: Int = 1

    companion object Companion {
        const val ID_KEY = "id"// TODO, check serial name
        const val VALUE_KEY = "value"// TODO, check serial name
    }
}
