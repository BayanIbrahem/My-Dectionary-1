package dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet

import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDSheetDataType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDSheetLanguageType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeader
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeaderRole

interface MDSheetImportDetails : MDSheet {
    val explicitLanguageType: MDSheetLanguageType?
    val safeLanguageType: MDSheetLanguageType
        get() = explicitLanguageType ?: languageType
    val explicitDataType: MDSheetDataType?
    val safeDataType: MDSheetDataType
        get() = explicitDataType ?: dataType

    val explicitFirstRow: Int?

    /**
     * return the first actual valid row, if [explicitFirstRow] is not null it returns [explicitFirstRow]
     * else it return `[firstRow] + 1` cause we must surpass the header
     */
    val actualFirstRow: Int get() = explicitFirstRow ?: firstRow.inc()

    /**
     * return the last actual valid row, if [explicitLastRow] is not null it returns [explicitLastRow]
     * else it return [lastRow]
     */
    val actualLastRow: Int get() = explicitLastRow ?: lastRow
    val explicitLastRow: Int?
    val headers: List<MDSheetHeader>
    val roles: Set<MDSheetHeaderRole>
    val status: MDSheetStatus

    companion object {
        operator fun invoke(
            languageType: MDSheetLanguageType,
            explicitLanguageType: MDSheetLanguageType?,
            explicitDataType: MDSheetDataType?,
            firstRow: Int,
            lastRow: Int,
            explicitFirstRow: Int?,
            explicitLastRow: Int?,
            headers: List<MDSheetHeader>,
            roles: Set<MDSheetHeaderRole>,
            status: MDSheetStatus,
            dataType: MDSheetDataType,
            name: String,
            index: Int,
        ): MDSheetImportDetails = MDSheetImportDetailsDelegate(
            languageType = languageType,
            explicitLanguageType = explicitLanguageType,
            explicitDataType = explicitDataType,
            firstRow = firstRow,
            lastRow = lastRow,
            explicitFirstRow = explicitFirstRow,
            explicitLastRow = explicitLastRow,
            headers = headers,
            roles = roles,
            status = status,
            dataType = dataType,
            name = name,
            index = index
        )

        operator fun invoke(
            sheet: MDSheet,
            explicitLanguageType: MDSheetLanguageType? = null,
            explicitDataType: MDSheetDataType? = null,
            explicitFirstRow: Int? = null,
            explicitLastRow: Int? = null,
            headers: List<MDSheetHeader>,
            roles: Set<MDSheetHeaderRole>,
            status: MDSheetStatus,
        ): MDSheetImportDetails = MDSheetImportDetailsDelegate(
            sheet = sheet,
            explicitLanguageType = explicitLanguageType,
            explicitDataType = explicitDataType,
            explicitFirstRow = explicitFirstRow,
            explicitLastRow = explicitLastRow,
            headers = headers,
            roles = roles,
            status = status
        )
    }
}

data class MDSheetImportDetailsDelegate(
    override val languageType: MDSheetLanguageType,
    override val explicitLanguageType: MDSheetLanguageType?,
    override val explicitDataType: MDSheetDataType?,
    override val firstRow: Int,
    override val lastRow: Int,
    override val explicitFirstRow: Int?,
    override val explicitLastRow: Int?,
    override val headers: List<MDSheetHeader>,
    override val roles: Set<MDSheetHeaderRole>,
    override val status: MDSheetStatus,
    override val dataType: MDSheetDataType,
    override val name: String,
    override val index: Int,
) : MDSheetImportDetails {
    constructor(
        sheet: MDSheet,
        explicitLanguageType: MDSheetLanguageType? = null,
        explicitDataType: MDSheetDataType? = null,
        explicitFirstRow: Int? = null,
        explicitLastRow: Int? = null,
        headers: List<MDSheetHeader>,
        roles: Set<MDSheetHeaderRole>,
        status: MDSheetStatus,
    ) : this(
        languageType = sheet.languageType,
        explicitLanguageType = explicitLanguageType,
        explicitDataType = explicitDataType,
        firstRow = sheet.firstRow,
        lastRow = sheet.lastRow,
        explicitFirstRow = explicitFirstRow,
        explicitLastRow = explicitLastRow,
        headers = headers,
        roles = roles,
        status = status,
        dataType = sheet.dataType,
        name = sheet.name,
        index = sheet.index
    )

    constructor(sheet: MDSheetImportDetails) : this(
        languageType = sheet.languageType,
        explicitLanguageType = sheet.explicitLanguageType,
        explicitDataType = sheet.explicitDataType,
        firstRow = sheet.firstRow,
        lastRow = sheet.lastRow,
        explicitFirstRow = sheet.explicitFirstRow,
        explicitLastRow = sheet.explicitLastRow,
        headers = sheet.headers,
        roles = sheet.roles,
        status = sheet.status,
        dataType = sheet.dataType,
        name = sheet.name,
        index = sheet.index
    )
}