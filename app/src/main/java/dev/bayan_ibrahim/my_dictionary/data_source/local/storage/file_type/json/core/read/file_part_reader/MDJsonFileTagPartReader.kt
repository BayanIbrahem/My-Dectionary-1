package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.read.file_part_reader

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.read.MDFilePartReader
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part.MDJsonFileTagPart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.getJsonPartKeyOfVersion
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.read.MDJsonFileReader
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

abstract class MDJsonFileTagPartReader<Tag : MDJsonFileTagPart>(
    override val version: Int,
    jsonObjectFlow: Flow<JsonObject>,
    json: Json,
    deserializer: DeserializationStrategy<Tag>,
) : MDFilePartReader.Tag, MDJsonFilePartReader<Tag>(
    jsonObjectFlow = jsonObjectFlow,
    json = json,
    deserializer = deserializer
) {
    override suspend fun readFile(): Sequence<Tag> {
        return readFileContent()
    }

    override fun getPartKey(): String {
        return getJsonPartKeyOfVersion(MDFilePartType.Tag, version)
    }
}
