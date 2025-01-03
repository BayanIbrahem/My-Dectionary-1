package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.file_part

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import kotlinx.serialization.json.Json

interface MDJsonFilePart : MDFilePart {
    suspend fun jsonStringify(json: Json): String
}

