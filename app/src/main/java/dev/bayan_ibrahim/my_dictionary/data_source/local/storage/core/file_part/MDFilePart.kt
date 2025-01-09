package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part

import kotlinx.serialization.json.Json

interface MDFilePart {
    suspend fun stringify(stringifier: MDFilePartStringifier): String {
        throw IllegalArgumentException("No stringifier implemented")
    }
}

interface MDFilePartStringifier
