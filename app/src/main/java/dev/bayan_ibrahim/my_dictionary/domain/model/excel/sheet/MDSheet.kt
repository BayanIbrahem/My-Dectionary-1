package dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet

import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDSheetDataType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDSheetLanguageType

interface MDSheet {
    val languageType: MDSheetLanguageType
    val dataType: MDSheetDataType
    val name: String
    val index: Int
    val firstRow: Int
    val lastRow: Int

    companion object {
        operator fun invoke(
            rawName: String,
            index: Int,
            firstRow: Int,
            lastRow: Int,
        ): MDSheet {
            val result = languageRegex.findAll(rawName).toList()
            val languageCode = result.firstOrNull()?.value?.lowercase()
            val languageType = if (languageCode == null) {
                MDSheetLanguageType.Global
            } else {
                MDSheetLanguageType.LanguageSpecific(languageCode)
            }
            val name = result.getOrNull(1)?.value?.lowercase()
            val dataType = MDSheetDataType(name)
            return MDSheetDelegate(
                languageType = languageType,
                dataType = dataType,
                name = rawName,
                index = index,
                firstRow = firstRow,
                lastRow = lastRow,
            )
        }

        operator fun invoke(
            languageType: MDSheetLanguageType,
            dataType: MDSheetDataType,
            name: String,
            index: Int,
            firstRow: Int,
            lastRow: Int,
        ): MDSheet = MDSheetDelegate(
            languageType = languageType,
            dataType = dataType,
            name = name,
            index = index,
            firstRow = firstRow,
            lastRow = lastRow,
        )

        val languageRegex = "([a-zA-Z]{2,3})-(.+)".toRegex()
    }
}

data class MDSheetDelegate(
    override val languageType: MDSheetLanguageType,
    override val dataType: MDSheetDataType,
    override val name: String,
    override val index: Int,
    override val firstRow: Int,
    override val lastRow: Int,
) : MDSheet