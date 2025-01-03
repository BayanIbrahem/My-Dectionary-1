package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.version.v1.file_part

import androidx.compose.ui.graphics.Color
import dev.bayan_ibrahim.my_dictionary.core.util.invalidIfNull
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part.MDJsonFileTagPart
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.strHex
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
    override fun toContextTag(): ContextTag = ContextTag(
        value = this.name,
        id = this.id.invalidIfNull(),
        color = this.color?.let { Color.strHex(it) },
        passColorToChildren = this.passColorToChildren,
    )

    override suspend fun jsonStringify(json: Json): String {
        return json.encodeToString(serializer(), this)
    }

    companion object Companion {
        const val ID_KEY = "id"// TODO, check serial name
        const val VALUE_KEY = "value"// TODO, check serial name
        const val COLOR_KEY = "color" // TODO, check serial name
        const val PASS_COLOR_KEY = "pass_color"
    }
}
