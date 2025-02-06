package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.version.v1.file_part

import androidx.compose.ui.graphics.Color
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part.MDJsonFileTagPart
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.validate
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.strHex
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MDJsonFileTagPartV1(
    @SerialName(VALUE_KEY)
    override val name: String,
    @SerialName(COLOR_KEY)
    override val color: String? = null,
    @SerialName(PASS_COLOR_KEY)
    override val passColorToChildren: Boolean? = null,
) : MDJsonFileTagPart {
    override fun toTag(): Tag? = name.takeIf {
        it.isNotBlank()
    }?.let {
        Tag(
            value = this.name,
            color = this.color?.let { Color.strHex(it) },
            passColorToChildren = this.passColorToChildren == true,
            currentColorIsPassed = false,
        ).validate()
    }

    override suspend fun jsonStringify(json: Json): String {
        return json.encodeToString(serializer(), this)
    }

    companion object Companion {
        const val VALUE_KEY = "value"
        const val COLOR_KEY = "color"
        const val PASS_COLOR_KEY = "passColor"
    }
}
