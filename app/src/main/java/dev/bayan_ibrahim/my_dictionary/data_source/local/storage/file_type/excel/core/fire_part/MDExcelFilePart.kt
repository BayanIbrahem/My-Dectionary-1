package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.excel.core.fire_part

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.MDFilePartStringifier

interface MDExcelFilePart : MDFilePart {
    override suspend fun stringify(stringifier: MDFilePartStringifier): String {
        throw UnsupportedOperationException("Excel file part can not be stringified")
    }
}
