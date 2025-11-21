package dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet

import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDSheetDataType

data class MDSheetImportProgress(
    val sheetName: String,
    val sheetDataType: MDSheetDataType,
    val rows: Int,
    val importedRows: Int,
    val status: MDSheetImportProgressStatus
) {
    val progress: Float
        get() {
            if (rows == 0) return 0f
            return (importedRows / rows.toFloat()).coerceIn(0f, 1f)
        }
}
