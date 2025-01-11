package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFileLanguagePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFileTagPart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFileWordPart
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import java.io.Writer

interface MDFilePartWriter<out Data : MDFilePart> {
    val type: MDFilePartType
    val version: Int

    /**
     * @return true if data decoded data is not empty
     */
    suspend fun writePart(
        writer: Writer,
        onProgress: suspend (index: Int, total: Int) -> Unit = { _, _ -> },
    ): Boolean

    interface Language : MDFilePartWriter<MDFileLanguagePart> {
        override val type: MDFilePartType get() = MDFilePartType.Language
    }

    interface Tag : MDFilePartWriter<MDFileTagPart> {
        override val type: MDFilePartType get() = MDFilePartType.Tag
    }

    interface Word : MDFilePartWriter<MDFileWordPart> {
        override val type: MDFilePartType get() = MDFilePartType.Word
    }
}