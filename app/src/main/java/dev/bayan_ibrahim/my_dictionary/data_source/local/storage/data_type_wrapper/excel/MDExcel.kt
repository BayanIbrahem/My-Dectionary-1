package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.data_type_wrapper.excel

import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDRowCell
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDSheet
import java.io.Closeable
import java.io.OutputStream

/**
 * excel file wrapper, used for single file instance and should be disposed calling [close] or
 * use [use] block
 */
interface MDExcel : Closeable {
    /**
     * Retrieves a list of sheets from the Excel file, with their types extracted from their names.
     *
     * @return A list of [MDSheet] objects representing the sheets in the file.
     */
    suspend fun getSheets(): List<MDSheet>

    /**
     * Reads the rows from a specified sheet and provides the parsed data to a callback function.
     *
     * @param sheetName The name of the sheet to read.
     * @param startRow optional value of the start row, if null first valid row in sheet would be taken
     * @param endRow optional value of the end row, if null last valid row in sheet would be taken
     * @param limit max lines of rows to read
     * @param countInvalidRows if true then blank and invalid rows would be counted for the limit
     * @param onRowRead A callback function that receives a list of [MDCellData] objects for each row.
     * @return true if the sheet is found (even if it is empty) false otherwise
     *
     * if possible use [readSheetRows] that takes the index instead of this, cause this sheet search for the sheet in sheets
     */
    suspend fun readSheetRows(
        sheetName: String,
        startRow: Int? = null,
        endRow: Int? = null,
        limit: Int? = null,
        countInvalidRows: Boolean = false,
        onRowRead: suspend (rowNumber: Int, rowData: List<MDRowCell>) -> Unit,
    ): Boolean

    /**
     * Reads the rows from a specified sheet and provides the parsed data to a callback function.
     *
     * @param sheetIndex The index of the sheet to read.*
     * @param startRow optional value of the start row, if null first valid row in sheet would be taken
     * @param endRow optional value of the end row, if null last valid row in sheet would be taken
     * @param limit max lines of rows to read
     * @param countInvalidRows if true then blank and invalid rows would be counted for the limit
     * @param onRowRead A callback function that receives a list of [MDCellData] objects for each row.
     * @return true if the sheet is found (even if it is empty) false otherwise
     * **NOTE** this method is faster than [readSheetRows] that takes the sheet name
     */
    suspend fun readSheetRows(
        sheetIndex: Int,
        startRow: Int? = null,
        endRow: Int? = null,
        limit: Int? = null,
        countInvalidRows: Boolean = false,
        onRowRead: suspend (rowNumber: Int, rowData: List<MDRowCell>) -> Unit,
    ): Boolean

    /**
     * Reads the rows from a specified sheet and provides the parsed data to a callback function.
     *
     * @param sheet The sheet to read (in the code we use the [MDSheet.index] with [readSheetRows]).
     * @param startRow optional value of the start row, if null first valid row in sheet would be taken
     * @param endRow optional value of the end row, if null last valid row in sheet would be taken
     * @param limit max lines of rows to read
     * @param countInvalidRows if true then blank and invalid rows would be counted for the limit
     * @param onRowRead A callback function that receives a list of [MDCellData] objects for each row.
     * @return true if the sheet is found (even if it is empty) false otherwise
     */
    suspend fun readSheetRows(
        sheet: MDSheet,
        startRow: Int? = null,
        endRow: Int? = null,
        limit: Int? = null,
        countInvalidRows: Boolean = false,
        onRowRead: suspend (rowNumber: Int, rowData: List<MDRowCell>) -> Unit,
    ): Boolean {
        return readSheetRows(
            sheetIndex = sheet.index,
            startRow = startRow,
            endRow = endRow,
            limit = limit,
            countInvalidRows = countInvalidRows,
            onRowRead = onRowRead
        )
    }


    /**
     * Creates a copy of an existing sheet within the opened Excel file.
     *
     * @param sourceSheetName The name of the sheet to copy.
     * @param destinationSheetName The name of the new sheet.
     * @return the index of new sheet or -1 if the sheet can not be cloned
     */
    suspend fun copySheet(sourceSheetName: String, destinationSheetName: String): Result<Int>
    suspend fun copySheet(sourceSheet: MDSheet, destinationSheetName: String): Result<Int> {
        return copySheet(sourceSheet.index, destinationSheetName)
    }

    suspend fun copySheet(
        sourceSheetIndex: Int,
        destinationSheetName: String,
    ): Result<Int>

    /**
     * Writes rows to a specified sheet using a callback function to generate row data.
     *
     * @param sheetName The name of the sheet to write to.
     * @param startRow The row index to start writing from (e.g., to skip headers). Defaults to 0.
     * @param writeProgress A callback function that provides the current row count after each row is written. Defaults to an empty function.
     * @param rowDataProvider A callback function that generates the data for each row. Returning null indicates the end of writing.
     * @return A [Result] containing the number of rows written if successful, or an exception if an error occurs.
     */
    suspend fun writeSheetRows(
        sheetName: String,
        startRow: Int = 0,
        writeProgress: (rowCount: Int) -> Unit = {},
        rowDataProvider: suspend () -> List<MDRowCell>?,
    ): Result<Int>

    /**
     * Writes rows to a specified sheet using a callback function to generate row data.
     *
     * @param sheetIndex The index of the sheet to write to.
     * @param startRow The row index to start writing from (e.g., to skip headers). Defaults to 0.
     * @param writeProgress A callback function that provides the current row count after each row is written. Defaults to an empty function.
     * @param rowDataProvider A callback function that generates the data for each row. Returning null indicates the end of writing.
     * @return A [Result] containing the number of rows written if successful, or an exception if an error occurs.
     */
    suspend fun writeSheetRows(
        sheetIndex: Int,
        startRow: Int = 0,
        writeProgress: (rowCount: Int) -> Unit = {},
        rowDataProvider: suspend () -> List<MDRowCell>?,
    ): Result<Int>

    /**
     * Writes rows to a specified sheet using a callback function to generate row data.
     *
     * @param sheet The [MDSheet] to write to.
     * @param startRow The row index to start writing from (e.g., to skip headers). Defaults to 0.
     * @param writeProgress A callback function that provides the current row count after each row is written. Defaults to an empty function.
     * @param rowDataProvider A callback function that generates the data for each row. Returning null indicates the end of writing.
     * @return A [Result] containing the number of rows written if successful, or an exception if an error occurs.
     */
    suspend fun writeSheetRows(
        sheet: MDSheet,
        startRow: Int = 0,
        writeProgress: (rowCount: Int) -> Unit = {},
        rowDataProvider: suspend () -> List<MDRowCell>?,
    ): Result<Int> = writeSheetRows(
        sheetIndex = sheet.index,
        startRow = startRow,
        writeProgress = writeProgress,
        rowDataProvider = rowDataProvider
    )

    /**
     * Exports the modified Excel file to a specified output stream.
     *
     * @param outputStream The output stream to write the exported file to.
     */
    suspend fun export(outputStream: OutputStream): Result<Unit>
}
