package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePartStringifier
import kotlinx.serialization.json.Json

interface MDJsonFilePart : MDFilePart {
    override suspend fun stringify(stringifier: MDFilePartStringifier): String {
        return if (stringifier is JsonStringifier) {
            jsonStringify(stringifier.json)
        } else {
            super.stringify(stringifier)
        }
    }

    suspend fun jsonStringify(json: Json): String
}

data class JsonStringifier(val json: Json) : MDFilePartStringifier

