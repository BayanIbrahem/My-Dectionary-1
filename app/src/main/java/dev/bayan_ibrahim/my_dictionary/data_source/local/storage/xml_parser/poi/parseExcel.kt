package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser.poi

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream

fun parseExcel(inputStream: InputStream): List<List<String>> {
    val workbook: Workbook = XSSFWorkbook(inputStream)
    val sheet: Sheet = workbook.getSheetAt(0) // Assuming you want the first sheet
    val data = mutableListOf<List<String>>()

    for (row: Row in sheet) {
        val rowData = mutableListOf<String>()
        for (cell: Cell in row) {
            when (cell.cellType) {
                Cell.CELL_TYPE_STRING -> rowData.add(cell.stringCellValue)
                Cell.CELL_TYPE_NUMERIC -> rowData.add(cell.numericCellValue.toString())
                Cell.CELL_TYPE_BOOLEAN -> rowData.add(cell.booleanCellValue.toString())
                Cell.CELL_TYPE_BLANK -> rowData.add("") // Handle blank cells
                Cell.CELL_TYPE_ERROR -> rowData.add("ERROR") // Handle error cells
                Cell.CELL_TYPE_FORMULA -> {
                    when (cell.cachedFormulaResultType) {
                        Cell.CELL_TYPE_STRING -> rowData.add(cell.stringCellValue)
                        Cell.CELL_TYPE_NUMERIC -> rowData.add(cell.numericCellValue.toString())
                        Cell.CELL_TYPE_BOOLEAN -> rowData.add(cell.booleanCellValue.toString())
                        Cell.CELL_TYPE_ERROR -> rowData.add("FORMULA ERROR")
                        else -> rowData.add("UNKNOWN FORMULA TYPE")
                    }
                }

                else -> rowData.add("UNKNOWN CELL TYPE")
            }
        }
        data.add(rowData)
    }

    workbook.close()
    return data
}