package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.version.v1.read.file_part_reader

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.read.file_part_reader.MDJsonFileWordPartReader
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.version.v1.file_part.MDJsonFileWordPartV1
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

data class MDJsonFileWordPartReaderV1(
    val json: Json,
    val jsonObjectFlow: Flow<JsonObject>,
    val deserializer: DeserializationStrategy<MDJsonFileWordPartV1>,
) : MDJsonFileWordPartReader<MDJsonFileWordPartV1>(
    version = 1,
    jsonObjectFlow = jsonObjectFlow,
    json = json,
    deserializer = deserializer
)
