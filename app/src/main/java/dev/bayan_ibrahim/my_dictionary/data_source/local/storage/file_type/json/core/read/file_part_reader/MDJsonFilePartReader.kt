package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.read.file_part_reader

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part.MDJsonFilePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.getJsonPartKeyOfVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray

abstract class MDJsonFilePartReader<Part : MDJsonFilePart>(
    private val jsonObjectFlow: Flow<JsonObject>,
    private val json: Json,
    private val deserializer: DeserializationStrategy<Part>,
) {
    /**
     * @throws IllegalArgumentException if failed to get array of part [getPartKey]
     * @throws [SerializationException] if the given JSON element is not a valid JSON input for the type [Part]
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [Part]
     */
    protected suspend fun readFileContent(): Sequence<Part> {
        val jsonObject = jsonObjectFlow.first()
        val languageJsonArray = getPartArray(jsonObject)
        return languageJsonArray.asSequence().map {
            json.decodeFromJsonElement(deserializer, it)
        }
    }

    protected abstract fun getPartKey(): String

    /**
     * @throws IllegalArgumentException if the key of language is not exists in json reader [getJsonPartKeyOfVersion]
     * @throws IllegalArgumentException if the key of language is not exists in [jsonObject]
     */
    private suspend fun getPartArray(
        jsonObject: JsonObject,
    ): JsonArray {
        val key = getPartKey()
        return jsonObject[key]?.jsonArray
            ?: throw IllegalArgumentException("can not find key with value $key for language data, json object with keys ${jsonObject.keys}, make sure the format of file is correct of its version is correct") // TODO, custom exception
    }
}