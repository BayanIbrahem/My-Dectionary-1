package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.core.file_reader

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_reader.MDFileReader
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

abstract class MDJsonFileReader(
    override val version: Int,
    private val json: Json,
) : MDFileReader {
    private var _jsonObjectFlow: MutableStateFlow<Pair<MDFileData, JsonObject>?> = MutableStateFlow(null)
    protected val jsonObjectFlow: Flow<JsonObject> = _jsonObjectFlow.mapNotNull {
        it?.second
    }

    private var isInProgress = false
    private suspend fun getJsonObjectFromFile(
        data: MDFileData,
        /**
         * toggle reparse if the current value is a valid json object for a file its uri is the same of provided uri in [data]
         */
        reparseIfSameFileUri: Boolean = false,
    ) {
        if (isInProgress) return
        if (!reparseIfSameFileUri && data.uri == _jsonObjectFlow.value?.first?.uri) return
        isInProgress = true
        val jsonObject = Json.parseToJsonElement(
            openInputStream(data).use {
                it.readBytes().decodeToString()
            }
        ).jsonObject // TODO, custom exception
        _jsonObjectFlow.value = data to jsonObject
        isInProgress = false
    }

    override suspend fun getAvailablePartsOfFile(data: MDFileData): List<MDFilePartType> {
        getJsonObjectFromFile(data)
        val availableKeys = jsonObjectFlow.first().keys
        return MDFilePartType.entries.filter {
            getPartKeyOfVersion(it, version) in availableKeys
        }
    }

    companion object Companion {
        /**
         * this is labels of file part data data in json file for each version,
         * there must be a value for each valid version of the the app
         * @throws IllegalArgumentException if the version is not known
         */
        fun getPartKeyOfVersion(part: MDFilePartType, version: Int): String = when (part) {
            MDFilePartType.Language -> when (version) {
                1 -> "languages"
                else -> throw IllegalArgumentException("invalid version $version for language key") // TODO, custom exception
            }

            MDFilePartType.Tag -> when (version) {
                1 -> "tags"
                else -> throw IllegalArgumentException("invalid version $version for tag key") // TODO, custom exception
            }

            MDFilePartType.Word -> when (version) {
                1 -> "words"
                else -> throw IllegalArgumentException("invalid version $version for word key") // TODO, custom exception
            }
        }

    }
}