package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part_reader

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part.MDFileLanguagePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part.MDFileTagPart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part.MDFileWordPart

sealed interface MDFilePartReader<Data : MDFilePart> {
    val type: MDFilePartType
    val version: Int
    suspend fun readFile(): Sequence<Data>
    interface Language : MDFilePartReader<MDFileLanguagePart> {
        override val type: MDFilePartType get() = MDFilePartType.Language
    }

    interface Tag : MDFilePartReader<MDFileTagPart> {
        override val type: MDFilePartType get() = MDFilePartType.Tag
    }

    interface Word : MDFilePartReader<MDFileWordPart> {
        override val type: MDFilePartType get() = MDFilePartType.Word
    }
}