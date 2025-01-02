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
    @SerialName(COLOR_KEY)
    override val color: String? = null,
    @SerialName(PASS_COLOR_KEY)
    override val passColorToChildren: Boolean = false,
) : MDJsonFileTagPart {
    @Transient
    override val version: Int = 1

    companion object Companion {
        const val ID_KEY = "id"// TODO, check serial name
        const val VALUE_KEY = "value"// TODO, check serial name
        const val COLOR_KEY = "color" // TODO, check serial name
        const val PASS_COLOR_KEY = "pass_color"
    }
}
