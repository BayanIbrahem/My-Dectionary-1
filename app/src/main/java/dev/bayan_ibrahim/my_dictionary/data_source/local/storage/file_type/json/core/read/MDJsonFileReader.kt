package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.read

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.read.MDFileReader
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.getJsonPartKeyOfVersion
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
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
    private var _jsonObjectFlow: MutableStateFlow<Pair<MDDocumentData, JsonObject>?> = MutableStateFlow(null)
    protected val jsonObjectFlow: Flow<JsonObject> = _jsonObjectFlow.mapNotNull {
        it?.second
    }

    private var isInProgress = false
    private suspend fun getJsonObjectFromFile(
        data: MDDocumentData,
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

    override suspend fun getAvailablePartsOfFile(data: MDDocumentData): List<MDFilePartType> {
        getJsonObjectFromFile(data)
        val availableKeys = jsonObjectFlow.first().keys
        return MDFilePartType.entries.filter {
            getJsonPartKeyOfVersion(it, version) in availableKeys
        }
    }
}