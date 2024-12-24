package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_part_reader

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.core.file_part_reader.MDJsonFileTagPartReader
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_part.MDJsonFileTagPartV1
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

data class MDJsonFileTagPartReaderV1(
    val json: Json,
    val jsonObjectFlow: Flow<JsonObject>,
    val deserializer: DeserializationStrategy<MDJsonFileTagPartV1>,
) : MDJsonFileTagPartReader<MDJsonFileTagPartV1>(
    version = 1,
    jsonObjectFlow = jsonObjectFlow,
    json = json,
    deserializer = deserializer
)
