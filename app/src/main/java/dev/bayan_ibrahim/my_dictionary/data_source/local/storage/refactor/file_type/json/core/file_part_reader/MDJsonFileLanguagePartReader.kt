package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.core.file_part_reader

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part_reader.MDFilePartReader
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.core.file_reader.MDJsonFileReader
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

abstract class MDJsonFileLanguagePartReader<Language : dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.core.file_part.MDJsonFileLanguagePart>(
    override val version: Int,
    json: Json,
    jsonObjectFlow: Flow<JsonObject>,
    deserializer: DeserializationStrategy<Language>,
) : MDFilePartReader.Language, MDJsonFilePartReader<Language>(
    jsonObjectFlow = jsonObjectFlow,
    json = json,
    deserializer = deserializer
) {
    override suspend fun readFile(): Sequence<Language> {
        return readFileContent()
    }

    override fun getPartKey(): String {
        return MDJsonFileReader.getPartKeyOfVersion(MDFilePartType.Language, version)
    }
}
