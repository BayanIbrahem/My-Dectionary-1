package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.data_type_wrapper.excel.poi

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.atDefaultStartOfDay
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.data_type_wrapper.excel.MDExcel
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDCellData
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDExcelFileType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDRowCell
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet.MDSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.Date

/**
 * An [MDExcel] implementation for Android using Apache POI to handle Excel files.
 *
 * This class provides functionality for reading, writing, and manipulating Excel files,
 * including creating copies of templates, copying sheets, and exporting the modified file.
 *
 * The class manages the workbook lifecycle, including disposal, and ensures that the
 * workbook is not accessed after it has been disposed.
 *
 * @property isDisposed Indicates whether the workbook has been disposed.
 */
class AndroidPoiMDExcel private constructor(private val workbook: Workbook, override val type: MDExcelFileType) : MDExcel {
    var isDisposed: Boolean = false
        private set

    /**
     * Checks if the workbook has been disposed and throws an [IllegalStateException] if it has.
     *
     * This method should be called at the beginning of any method that accesses the workbook
     * to ensure that the workbook is still valid.
     *
     * @throws IllegalStateException If the workbook has been disposed.
     */
    private fun requireNotDisposed() = require(!isDisposed) {
        "Try to use workbook after disposing it, try to generate new Excel manager using new input stream to continue"
    }

    companion object {
        /**
         * Creates an [AndroidPoiMDExcel] instance for working with XLSX files.
         *
         * * **File Format**: Supports the modern, XML-based `.xlsx` format (Office Open XML).
         * * **Memory Usage**: Loads the entire file into memory, suitable for moderate-sized files.
         * * **Performance**: Generally sufficient, but may be slower than [createHSSF] for small files due to XML parsing.
         * * **Limitations**: Handles up to 1,048,576 rows and 16,384 columns.
         * * **Use Cases**: Recommended for most `.xlsx` file operations.
         *
         * @param inputStream The [InputStream] containing the XLSX file data.
         * @return A [Result] containing the created [AndroidPoiMDExcel], or an error.
         * @see createSXSSF For very large XLSX files.
         * @see createHSSF For legacy XLS files.
         */
        suspend fun createXSSF(inputStream: InputStream): Result<AndroidPoiMDExcel> {
            return runCatching {
                val workbook = XSSFWorkbook(inputStream)
                return@runCatching AndroidPoiMDExcel(workbook, MDExcelFileType.XSSF)
            }
        }

        /**
         * Creates an [AndroidPoiMDExcel] instance for processing very large XLSX files using streaming.
         *
         * * **File Format**: Supports the modern, XML-based `.xlsx` format.
         * * **Memory Usage**: Minimizes memory usage by streaming data to disk, ideal for large files.
         * * **Performance**: Slower than [createXSSF] due to disk I/O.
         * * **Limitations**:
         * - Sequential row access only; rows cannot be modified after being written.
         * - May not support all advanced Excel features.
         * * **Use Cases**: Use when memory is a primary concern for very large XLSX files.
         *
         * @param inputStream The [InputStream] containing the XLSX file data.
         * @param rowAccessWindowSize The number of rows to keep in memory (default: 100).
         * @param compressTmpFiles Whether to compress temporary files (default: false).
         * @param useSharedStringsTable Whether to use a shared strings table (default: false).
         * @return A [Result] containing the created [AndroidPoiMDExcel], or an error.
         */
        suspend fun createSXSSF(
            inputStream: InputStream,
            rowAccessWindowSize: Int = 100,
            compressTmpFiles: Boolean = false,
            useSharedStringsTable: Boolean = false,
        ): Result<AndroidPoiMDExcel> {
            return runCatching {
                val xssfWorkbook = XSSFWorkbook(inputStream)
                val workbook = SXSSFWorkbook(xssfWorkbook, rowAccessWindowSize, compressTmpFiles, useSharedStringsTable)
                return@runCatching AndroidPoiMDExcel(workbook, MDExcelFileType.SXSSF)
            }
        }

        /**
         * Creates an [AndroidPoiMDExcel] instance for working with legacy XLS files.
         *
         * * **File Format**: Supports the older, binary `.xls` format (Excel 97-2003).
         * * **Memory Usage**: Loads the entire file into memory, which can cause issues for large files.
         * * **Performance**: Generally faster for small files.
         * * **Limitations**: Handles up to 65,536 rows and 256 columns.
         * * **Use Cases**: Use for reading or writing smaller `.xls` files.
         *
         * @param inputStream The [InputStream] containing the XLS file data.
         * @return A [Result] containing the created [AndroidPoiMDExcel], or an error.
         */
        suspend fun createHSSF(inputStream: InputStream): Result<AndroidPoiMDExcel> {
            return runCatching {
                val workbook = HSSFWorkbook(inputStream)
                return@runCatching AndroidPoiMDExcel(workbook, MDExcelFileType.HSSF)
            }
        }
    }

    override suspend fun getSheets(): List<MDSheet> {
        requireNotDisposed()
        return workbook.numberOfSheets.takeIf { it > 0 }?.let { count ->
            0..<count
        }?.map { index ->
            val sheet = workbook.getSheetAt(index)
            sheet.sheetName
            MDSheet(
                rawName = sheet.sheetName,
                index = index,
                firstRow = sheet.firstRowNum,
                lastRow = sheet.lastRowNum
            )
        } ?: emptyList()
    }

    override suspend fun readSheetRows(
        sheetName: String,
        startRow: Int?,
        endRow: Int?,
        limit: Int?,
        countInvalidRows: Boolean,
        onRowRead: suspend (rowNumber: Int, rowData: List<MDRowCell>) -> Unit,
    ): Boolean {
        requireNotDisposed()
        val sheet = workbook.getSheet(sheetName) ?: return false
        readSheetData(
            sheet = sheet,
            startRow = startRow,
            endRow = endRow,
            limit = limit,
            countInvalidRows = countInvalidRows,
            onRowRead = onRowRead,
        )
        return true
    }

    override suspend fun readSheetRows(
        sheetIndex: Int,
        startRow: Int?,
        endRow: Int?,
        limit: Int?,
        countInvalidRows: Boolean,
        onRowRead: suspend (rowNumber: Int, rowData: List<MDRowCell>) -> Unit,
    ): Boolean {
        requireNotDisposed()
        val sheet = workbook.getSheetAt(sheetIndex) ?: return false
        readSheetData(
            sheet = sheet,
            startRow = startRow,
            endRow = endRow,
            limit = limit,
            countInvalidRows = countInvalidRows,
            onRowRead = onRowRead,
        )
        return true
    }

    /**
     * Reads the rows from a specified sheet and provides the parsed data to a callback function.
     *
     * @param sheet The sheet to read.
     * @param startRow The starting row index (optional, defaults to the first row), coerced at least to sheet first row.
     * @param endRow The ending row index (optional, defaults to the last row), coerced at most to sheet last row.
     * @param limit max lines of rows to read
     * @param countInvalidRows if true then blank and invalid rows would be counted for the limit
     * @param onRowRead A callback function that receives the row number and a list of [MDRowCell] objects for each row.
     */
    private suspend fun readSheetData(
        sheet: Sheet,
        startRow: Int? = null,
        endRow: Int? = null,
        limit: Int? = null,
        countInvalidRows: Boolean = false,
        onRowRead: suspend (rowNumber: Int, rowData: List<MDRowCell>) -> Unit,
    ) {
        requireNotDisposed()
        val firstRow = startRow?.coerceAtLeast(sheet.firstRowNum) ?: sheet.firstRowNum
        val lastRow = endRow?.coerceAtMost(sheet.lastRowNum) ?: sheet.lastRowNum

        var readRowsCount = 0

        for (rowIndex in firstRow..lastRow) {
            if (limit != null && readRowsCount >= limit) break
            val row = sheet.getRow(rowIndex)
            val rowData = mutableListOf<MDRowCell>()

            val iterator = row?.cellIterator() ?: (emptyList<Cell>().listIterator())
            for (cell in iterator) {
                rowData.add(getRowCell(cell))
            }
            if (countInvalidRows || rowData.isNotEmpty()) {
                readRowsCount++
            }

            onRowRead(rowIndex, rowData)
        }
    }

    /**
     * @param sheetName name of the sheet
     * return sheet rows  if the sheet exists
     */
    override fun readSheetRows(
        sheetName: String,
    ): Result<Pair<Int, Int>> = runCatching {
        requireNotDisposed()
        val sheet = workbook.getSheet(sheetName) ?: throw IllegalStateException("invalid sheet")
        readSheetRows(sheet)
    }

    /**
     * @param sheetIndex index of the sheet
     * return sheet rows  if the sheet exists
     */
    override fun readSheetRows(
        sheetIndex: Int,
    ): Result<Pair<Int, Int>> = runCatching {
        requireNotDisposed()
        val sheet = workbook.getSheetAt(sheetIndex) ?: throw IllegalStateException("invalid sheet")
        readSheetRows(sheet)
    }

    private fun readSheetRows(
        sheet: Sheet,
    ): Pair<Int, Int> = Pair(sheet.firstRowNum, sheet.lastRowNum)


    override suspend fun copySheet(sourceSheetName: String, destinationSheetName: String): Result<Int> = runCatching {
        requireNotDisposed()
        val sourceSheet = workbook.getSheet(sourceSheetName) ?: return@runCatching -1
        val index = workbook.getSheetIndex(sourceSheet)
        return@runCatching copySheet(index, destinationSheetName).getOrThrow()
    }


    override suspend fun copySheet(sourceSheetIndex: Int, destinationSheetName: String): Result<Int> = runCatching {
        requireNotDisposed()
        workbook.getSheetAt(sourceSheetIndex) ?: return@runCatching -1
        val newSheet = workbook.cloneSheet(sourceSheetIndex)
        val newSheetIndex = workbook.getSheetIndex(newSheet)
        workbook.setSheetName(newSheetIndex, destinationSheetName)
        return@runCatching newSheetIndex
    }

    override suspend fun writeSheetRows(
        sheetName: String,
        startRow: Int,
        writeProgress: (rowCount: Int) -> Unit,
        rowDataProvider: suspend () -> List<MDRowCell>?,
    ): Result<Int> = runCatching {
        val sheet = workbook.getSheet(sheetName) ?: throw IllegalArgumentException("No sheet with name $sheetName")
        return@runCatching writeSheetData(sheet, startRow, writeProgress, rowDataProvider).getOrThrow()
    }

    override suspend fun writeSheetRows(
        sheetIndex: Int,
        startRow: Int,
        writeProgress: (rowCount: Int) -> Unit,
        rowDataProvider: suspend () -> List<MDRowCell>?,
    ): Result<Int> = runCatching {
        val sheet = workbook.getSheetAt(sheetIndex) ?: throw IllegalArgumentException("No sheet at index $sheetIndex")
        writeSheetData(sheet, startRow, writeProgress, rowDataProvider).getOrThrow()
    }

    /**
     * Writes data to a specified sheet.
     *
     * @param sheet The sheet to write to.
     * @param startRow The starting row index.
     * @param writeProgress A callback function to track write progress.
     * @param rowDataProvider A callback function that provides row data.
     * @return A [Result] containing the number of rows written, or an error.
     */
    private suspend fun writeSheetData(
        sheet: Sheet,
        startRow: Int,
        writeProgress: (rowCount: Int) -> Unit = {},
        rowDataProvider: suspend () -> List<MDRowCell>?,
    ): Result<Int> = runCatching {
        requireNotDisposed()
        var rowIndex = startRow
        var rowCount = 0
        while (true) {
            val rowData = rowDataProvider() ?: break // Stop if rowData is null

            val row = sheet.createRow(rowIndex)
            rowData.forEach { rowCell ->
                val cell = row.createCell(rowCell.columnIndex)
                setCellData(cell, rowCell.data)
            }
            writeProgress(rowCount++)
            rowIndex++
        }
        rowCount
    }


    override suspend fun export(outputStream: OutputStream): Result<Unit> = runCatching {
        requireNotDisposed()
        workbook.write(outputStream)
    }

    override fun close() {
        isDisposed = true
        workbook.close()
    }

    /**
     * Copies the current [AndroidPoiMDExcel] instance into a new instance using XSSFWorkbook with streaming.
     *
     * @return A [Result] containing the new [AndroidPoiMDExcel] instance, or an error.
     */
    suspend fun copyAsXSSF(): Result<AndroidPoiMDExcel> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val pipedInputStream = PipedInputStream()
            val pipedOutputStream = PipedOutputStream(pipedInputStream)

            launch {
                workbook.write(pipedOutputStream)
                pipedOutputStream.close()
            }

            return@runCatching createXSSF(pipedInputStream).getOrThrow()
        }
    }

    /**
     * Copies the current [AndroidPoiMDExcel] instance into a new instance using SXSSFWorkbook with streaming.
     *
     * @param rowAccessWindowSize The number of rows to keep in memory (default: 100).
     * @param compressTmpFiles Whether to compress temporary files (default: false).
     * @param useSharedStringsTable Whether to use a shared strings table (default: false).
     * @return A [Result] containing the new [AndroidPoiMDExcel] instance, or an error.
     */
    suspend fun copyAsSXSSF(
        rowAccessWindowSize: Int = 100,
        compressTmpFiles: Boolean = false,
        useSharedStringsTable: Boolean = false,
    ): Result<AndroidPoiMDExcel> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val pipedInputStream = PipedInputStream()
            val pipedOutputStream = PipedOutputStream(pipedInputStream)

            launch {
                workbook.write(pipedOutputStream)
                pipedOutputStream.close()
            }
            return@runCatching createSXSSF(pipedInputStream, rowAccessWindowSize, compressTmpFiles, useSharedStringsTable).getOrThrow()
        }
    }

    /**
     * Copies the current [AndroidPoiMDExcel] instance into a new instance using HSSFWorkbook with streaming.
     *
     * @return A [Result] containing the new [AndroidPoiMDExcel] instance, or an error.
     */
    suspend fun copyAsHSSF(): Result<AndroidPoiMDExcel> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val pipedInputStream = PipedInputStream()
            val pipedOutputStream = PipedOutputStream(pipedInputStream)

            launch {
                workbook.write(pipedOutputStream)
                pipedOutputStream.close()
            }

            return@runCatching createHSSF(pipedInputStream).getOrThrow()
        }
    }

    /**
     * Converts an Apache POI [Cell] to an [MDRowCell].
     *
     * @param cell The Apache POI cell.
     * @return The corresponding [MDRowCell].
     * @see [getCellData]
     */
    private fun getRowCell(cell: Cell): MDRowCell = getCellData(cell).toRowCell(cell.columnIndex)

    /**
     * Converts an Apache POI [Cell] to an [MDCellData].
     *
     * @param cell The Apache POI cell.
     * @return The corresponding [MDCellData].
     */
    private fun getCellData(cell: Cell): MDCellData {
        return when (cell.cellType) {
            Cell.CELL_TYPE_STRING -> getCellTextData(cell)
            Cell.CELL_TYPE_NUMERIC -> getCellNumericOrDateData(cell)
            Cell.CELL_TYPE_BOOLEAN -> getCellBooleanData(cell)
            Cell.CELL_TYPE_BLANK -> getCellBlankData()
            Cell.CELL_TYPE_ERROR -> MDCellData.Text("ERROR")
            Cell.CELL_TYPE_FORMULA -> {
                when (cell.cachedFormulaResultType) {
                    Cell.CELL_TYPE_STRING -> getCellTextData(cell)
                    Cell.CELL_TYPE_NUMERIC -> getCellNumericOrDateData(cell)
                    Cell.CELL_TYPE_BOOLEAN -> getCellBooleanData(cell)
                    Cell.CELL_TYPE_ERROR -> MDCellData.Error(
                        error = "FORMULA ERROR",
                        code = MDCellData.Error.FORMULA_TYPE_ERROR
                    )

                    else -> MDCellData.Error(
                        error = "UNKNOWN FORMULA TYPE",
                        code = MDCellData.Error.UNKNOWN_FORMULA_TYPE_ERROR
                    )
                }
            }

            else -> MDCellData.Error(
                error = "UNKNOWN CELL TYPE",
                code = MDCellData.Error.UNKNOWN_CELL_TYPE_ERROR
            )
        }
    }

    /**
     * Creates an [MDCellData.Blank] instance.
     *
     * @return An [MDCellData.Blank] instance.
     */
    private fun getCellBlankData() = MDCellData.Blank

    /**
     * Creates an [MDCellData.Bool] instance from an Apache POI [Cell].
     *
     * @param cell The Apache POI cell.
     * @return An [MDCellData.Bool] instance.
     */
    private fun getCellBooleanData(cell: Cell) = MDCellData.Bool(cell.booleanCellValue)

    /**
     * Creates an [MDCellData.Numeric] or [MDCellData.Date] instance from an Apache POI [Cell].
     *
     * @param cell The Apache POI cell.
     * @return An [MDCellData.Numeric] or [MDCellData.Date] instance.
     */
    private fun getCellNumericOrDateData(cell: Cell): MDCellData {
        val numericCellValue = cell.numericCellValue
        return if (DateUtil.isCellDateFormatted(cell)) {
            val epochDays: Int? =
                DateUtil
                    .getJavaDate(numericCellValue)
                    .toInstant()
                    ?.toKotlinInstant()
                    ?.toLocalDateTime(TimeZone.UTC)
                    ?.date
                    ?.toEpochDays()
            if (epochDays != null) {
                MDCellData.Date(LocalDate.fromEpochDays(epochDays))
            } else {
                MDCellData.Numeric(numericCellValue)
            }
        } else {
            MDCellData.Numeric(numericCellValue)
        }
    }

    /**
     * Creates an [MDCellData.Text] instance from an Apache POI [Cell].
     *
     * @param cell The Apache POI cell.
     * @return An [MDCellData.Text] instance.
     */
    private fun getCellTextData(cell: Cell) = MDCellData.Text(cell.stringCellValue)

    /**
     * Sets the value of an Apache POI [Cell] based on an [MDCellData] instance.
     *
     * @param cell The Apache POI cell.
     * @param cellData The [MDCellData] instance.
     */
    private fun setCellData(cell: Cell, cellData: MDCellData) {
        when (cellData) {
            is MDCellData.Text -> cell.setCellValue(cellData.text)
            is MDCellData.Numeric -> cell.setCellValue(cellData.number)
            is MDCellData.Date -> cell.setCellValue(DateUtil.getExcelDate(Date.from(cellData.date.atDefaultStartOfDay().toJavaInstant())))
            is MDCellData.Bool -> cell.setCellValue(cellData.value)
            MDCellData.Blank -> {} // Blank cells don't need any value set
            is MDCellData.Error -> cell.setCellValue(cellData.error)
        }
    }
}




