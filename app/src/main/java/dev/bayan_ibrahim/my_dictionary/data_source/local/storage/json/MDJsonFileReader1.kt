package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.model.JsonDataTemplateIdWithName1
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.model.JsonDataTemplateLanguage1
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.model.JsonDataTemplateWord1
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.io.InputStream

class MDJsonFileReader1(
    val openInputStream: (MDFileData) -> InputStream?,
    val json: Json = Json,
) {
    private var jsonObject: JsonObject? = null

    private inline fun ensureInitialized(body: (JsonObject) -> Unit) {
        requireNotNull(jsonObject)
        require(!isInitializing)
        body(jsonObject!!)
    }

    private var isInitializing: Boolean = false
    private suspend fun getJsonObjectFromFile(file: MDFileData): JsonObject {
        require(!isInitializing) {
            "initializing is already in progress"
        }
        try {
            isInitializing = true
            return openInputStream(file)?.use {
                json.parseToJsonElement(
                    it.readBytes().decodeToString()
                )
            }?.jsonObject ?: throw IOException()
        } finally {
            isInitializing = false
        }
    }

    suspend fun initialize(file: MDFileData): Boolean {
        jsonObject = getJsonObjectFromFile(file)
        return true
    }

    suspend fun getVersion() = ensureInitialized {
        getVersion(it)
    }

    suspend fun getLanguages() = ensureInitialized {
        getLanguages(it)
    }

    suspend fun getContextTags() = ensureInitialized {
        getContextTags(it)
    }

    suspend fun getWords() = ensureInitialized {
        getWords(it)
    }

    private suspend fun getVersion(jsonObject: JsonObject): Int {
        return jsonObject["version"]!!.jsonPrimitive.int
    }

    private suspend fun getLanguages(jsonObject: JsonObject): Sequence<JsonDataTemplateLanguage1> {
        return jsonObject["languages"]?.jsonArray?.iterator()?.asSequence()?.map {
            json.decodeFromJsonElement<JsonDataTemplateLanguage1>(it)
        } ?: sequenceOf()
    }

    private suspend fun getContextTags(jsonObject: JsonObject): Sequence<JsonDataTemplateIdWithName1> {
        return jsonObject["contextTags"]?.jsonArray?.iterator()?.asSequence()?.map {
            json.decodeFromJsonElement<JsonDataTemplateIdWithName1>(it)
        } ?: sequenceOf()
    }

    private suspend fun getWords(jsonObject: JsonObject): Sequence<JsonDataTemplateWord1> {
        return jsonObject["contextTags"]?.jsonArray?.iterator()?.asSequence()?.map {
            json.decodeFromJsonElement<JsonDataTemplateWord1>(it)
        } ?: sequenceOf()
    }
}