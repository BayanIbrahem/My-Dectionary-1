package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser

import android.util.Xml
import kotlinx.datetime.LocalDate
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream


object SheetParser {
    private val ns: String? = null

    fun parseSheet(inputStream: InputStream): Result<Map<SheetCellKey, SheetCell>> {
        return inputStream.use { inputStream ->
            runCatching {
                val parser: XmlPullParser = Xml.newPullParser().apply {
                    setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                    setInput(inputStream, null)
                }
                parser.navigateToTag(TAG_WORKSHEET)
                parser.parseWorkSheet().getOrThrow()
            }
        }
    }

    /**
     * init tag must be [TAG_WORKSHEET]
     */
    private fun XmlPullParser.parseWorkSheet(): Result<Map<SheetCellKey, SheetCell>> {
        return navigateToTag(TAG_SHEET_DATA).mapCatching {
            if (it) {
                parserSheetData()
            } else {
                throw IllegalArgumentException("Work sheet doesn't exists")
            }
        }
    }

    /**
     * init tag must [TAG_SHEET_DATA]
     */
    private fun XmlPullParser.parserSheetData(): Map<SheetCellKey, SheetCell> {
        val cells = mutableMapOf<SheetCellKey, SheetCell>()
        var rowResult = parserSheetRow(cells)
        while (rowResult.isSuccess) {
            rowResult = parserSheetRow(cells)
        }
        return cells
    }

    /**
     * @return true if row is parsed or false if faced an exception
     */
    private fun XmlPullParser.parserSheetRow(
        cells: MutableMap<SheetCellKey, SheetCell>,
    ): Result<Boolean> = navigateToTag(TAG_SHEET_DATA_ROW).mapCatching {
        if (it) {
            var cell = parserSheetCell()
            while (cell.isSuccess) {
                val data = cell.getOrNull() ?: continue
                cells[data.key] = data
                cell = parserSheetCell()
            }
            true
        } else {
            throw IllegalArgumentException("row is found not found")
        }
    }

    private fun XmlPullParser.parserSheetCell(): Result<SheetCell> = navigateToTag(TAG_SHEET_DATA_CELL).mapCatching { exists ->
        if (exists) {
            val attrs = (0..<this.attributeCount).mapNotNull {
                Pair(
                    first = getAttributeName(it) ?: return@mapNotNull null,
                    second = getAttributeValue(it) ?: return@mapNotNull null
                )
            }.toMap()

            val key = attrs[ATTR_CELL_ID]?.let { SheetCellKey(it) } ?: throw IllegalArgumentException("Cell doesn't have a cell id")
            val type = attrs[ATTR_CELL_TYPE]
            return@mapCatching when (type) {
                VALUE_CELL_TYPE_STRING_REF -> {
//                    <c r="A1" s="1" t="s"><v>0</v></c>
                    val cellValue = parseStringRefCellValue()
                    SheetCell(key, cellValue)
                }

                VALUE_CELL_TYPE_INLINE_STRING -> {
//                    <c r="A1"t="inlineStr"> <is><t>Hello</t></is></c>
                    val cellValue = parseInlineStringCellValue()
                    SheetCell(key, cellValue)
                }

                VALUE_CELL_TYPE_BOOL -> {
//                    <c r="B4" s="2" t="b"> <f>TRUE</f> <v>1</v> </c>
//                    <c r="C4" s="2" t="b"> <f>FALSE</f> <v>0</v> </c>
                    val cellValue = parseBooleanCellValue()
                    SheetCell(key, cellValue)
                }

                VALUE_CELL_TYPE_ERROR -> {
//                    <c r="B6" s="2" t="e"> <f>1/0</f> <v>#DIV/0!</v> </c>
                    val cellValue = parseErrorCellValue()
                    SheetCell(key, cellValue)
                }

                else -> {
//                    <c r="B3" s="3"> <f>25569</f> <v>25569</v> </c>
//                    <c r="C3" s="3"> <v>1</v> </c>
                    // those values are either a text, number, or a date, so we generate raw number or a text from it to make it a date or text later
                    // it may be empty cell without value

                    val styleIndex = attrs[ATTR_CELL_STYLE]?.toIntOrNull()
                    val cellValue = parseNormalCellValue(styleIndex)
                    SheetCell(key, cellValue)
                }
            }
        } else {
            throw IllegalArgumentException("Cell is not found")
        }
    }

    /**
     * parse cell value if it doesn't have a specific type
     * possible values are:
     * * [SheetCell.Value.Empty] if:
     * - failed to navigate to value tag [TAG_SHEET_DATA_CELL_VALUE] or that tag doesn't exists
     * - content of value tag [TAG_SHEET_DATA_CELL_VALUE] is an empty text
     * * [SheetCell.Value.Text] if it has a valid non empty text in value tag [TAG_SHEET_DATA_CELL_VALUE] but:
     * - the [styleIndex] is null
     * - the text value  can not be parsed to an int
     * * [SheetCell.Value.RawNumber] :
     * which can be mapped later to [SheetCell.Value.Text] or [SheetCell.Value.Date] according to [styleIndex] value,
     * this require an integer value for the text content and non null style value
     * @throws IllegalArgumentException if it has a value tag [TAG_SHEET_DATA_CELL_VALUE] but can not get the text value
     */
    private fun XmlPullParser.parseNormalCellValue(styleIndex: Int?): SheetCell.Value {
        val cellValue = navigateToTag(TAG_SHEET_DATA_CELL_VALUE).fold(
            onFailure = {
//                throw IllegalArgumentException("Cell type is normal text or raw text, and couldn't navigate to value tag with error: $it")
                SheetCell.Value.Empty
            },
            onSuccess = {
                if (it) {
                    getNextText().fold(
                        onFailure = {
                            throw IllegalArgumentException("Cell type is normal text or raw text, and couldn't get inner text with error: $it")
                        },
                        onSuccess = { text ->
                            if (text.isEmpty()) {
                                SheetCell.Value.Empty
                            } else if (styleIndex == null) {
                                SheetCell.Value.Text(text)

                            } else {
                                text.toIntOrNull()?.let {
                                    SheetCell.Value.RawNumber(it, styleIndex)
                                } ?: SheetCell.Value.Text(text)

                            }
                        }
                    )
                } else {
//                    throw IllegalArgumentException("Cell type is normal text or raw text, and couldn't find value tag")
                    SheetCell.Value.Empty
                }
            }
        )
        return cellValue
    }

    /**
     * return error cell value without throwing any exception
     */
    private fun XmlPullParser.parseErrorCellValue(): SheetCell.Value.Error {
        val function = navigateToTag(TAG_SHEET_DATA_CELL_FUNCTION).mapCatching {
            getNextText().fold(
                onFailure = {
                    throw IllegalArgumentException("Cell type is boolean, can not get the text within the function")
                },
                onSuccess = { it },
            )
        }
        val value = navigateToTag(TAG_SHEET_DATA_CELL_VALUE).mapCatching {
            if (it) {
                getNextText().fold(
                    onFailure = { valueError ->
                        throw IllegalArgumentException("Cell type is error, failed to get inner text with error: $valueError")
                    },
                    onSuccess = { it }
                )

            } else {
                throw IllegalArgumentException("Cell type is error, and failed to find value tag ")
            }
        }
        return SheetCell.Value.Error(function.getOrNull(), value.getOrNull())
    }

    /**
     * try to return the cell value either from the function or from the value, if the value is int then it would be true if it is not zero
     */
    private fun XmlPullParser.parseBooleanCellValue(): SheetCell.Value.Bool {
        val function = navigateToTag(TAG_SHEET_DATA_CELL_FUNCTION).mapCatching {
            getNextText().fold(
                onFailure = {
                    throw IllegalArgumentException("Cell type is boolean, can not get the text within the function")
                },
                onSuccess = {
                    it.lowercase().let {
                        when (it) {
                            VALUE_CELL_BOOLEAN_TRUE.lowercase() -> true
                            VALUE_CELL_BOOLEAN_FALSE.lowercase() -> false
                            else -> throw IllegalArgumentException("Cell type is boolean, invalid function value $it should be (ignore case) $VALUE_CELL_BOOLEAN_TRUE or $VALUE_CELL_BOOLEAN_FALSE")

                        }
                    }
                },
            )
        }
        return function.fold(
            onSuccess = {
                SheetCell.Value.Bool(it)
            },
            onFailure = { functionError ->
                navigateToTag(TAG_SHEET_DATA_CELL_VALUE).fold(
                    onFailure = { valueError ->
                        throw IllegalArgumentException("Cell type is boolean, failed to get function value with error $functionError, and failed to navigate to value tag with error: $valueError")
                    },
                    onSuccess = {
                        if (it) {
                            getNextText().fold(
                                onFailure = { valueError ->
                                    throw IllegalArgumentException("Cell type is boolean, failed to get function value with error $functionError, and failed to get value inner text with error: $valueError")
                                },
                                onSuccess = { text ->
                                    val intValue = text.toIntOrNull()
                                        ?: throw IllegalArgumentException("Cell type is boolean, failed to get function value with error $functionError, and failed to parse int from text ($text)")
                                    SheetCell.Value.Bool(intValue != 0)
                                }
                            )

                        } else {
                            throw IllegalArgumentException("Cell type is boolean, failed to get function value with error $functionError, and failed to find value tag ")
                        }
                    }
                )
            },
        )
    }

    /** return cell value or throw an exception */
    private fun XmlPullParser.parseInlineStringCellValue(): SheetCell.Value.Text {
        return navigateToTag(TAG_SHEET_DATA_CELL_INLINE_STRING).fold(
            onFailure = {
                throw IllegalArgumentException("Cell type is inline string, error navigating to inline string tag from a cell of type inline string, error: $it")
            },
            onSuccess = {
                if (it) {  // exists
                    navigateToTag(TAG_SHEET_DATA_CELL_INLINE_STRING_TEXT).fold(
                        onFailure = {
                            throw IllegalArgumentException("Cell type is inline string, error navigating to inline string text tag from a cell of type inline string after navigating to inline string tag, error: $it")
                        },
                        onSuccess = {
                            if (it) {
                                getNextText().fold(
                                    onFailure = {
                                        throw IllegalArgumentException("Cell type is inline string, error getting text value: $it")
                                    },
                                    onSuccess = { text ->
                                        SheetCell.Value.Text(text)
                                    }
                                )

                            } else {
                                throw IllegalArgumentException("Cell type is inline string but couldn't find the inline string text tag within the cell content inline string tag")
                            }
                        }
                    )
                } else {
                    throw IllegalArgumentException("Cell type is inline string but couldn't find the inline string tag within the cell tag")
                }
            }
        )
    }

    /**
     * return a cell value or throw an exception
     */
    private fun XmlPullParser.parseStringRefCellValue(): SheetCell.Value.Ref {
        return navigateToTag(TAG_SHEET_DATA_CELL_VALUE).fold(
            onFailure = {
                throw IllegalArgumentException("Cell type is string ref but, an error accorded when getting the cell value, error: $it")
            },
            onSuccess = { valueExists ->
                if (valueExists) {
                    getNextText().fold(
                        onFailure = {
                            throw IllegalArgumentException("Cell type is string ref but failed to get tex withen the value")
                        },
                        onSuccess = { text ->
                            if (text.isEmpty()) {
                                throw IllegalArgumentException("Cell type is string ref but text within the value is empty must be valid number")
                            } else {
                                val ref = text.toIntOrNull()
                                    ?: throw IllegalArgumentException("Cell type is string ref but text ($text) can not be parsed to an int")
                                SheetCell.Value.Ref(ref)
                            }
                        }
                    )
                } else {
                    throw IllegalArgumentException("Cell type is string ref we couldn't find the value tag")
                }
            }
        )
    }

    private const val TAG_WORKSHEET = "worksheet"
    private const val TAG_SHEET_DATA = "sheetData"
    private const val TAG_SHEET_DATA_ROW = "row"
    private const val TAG_SHEET_DATA_CELL = "c"
    private const val TAG_SHEET_DATA_CELL_VALUE = "v"
    private const val TAG_SHEET_DATA_CELL_FUNCTION = "f"

    /** the tag within [TAG_SHEET_DATA_CELL] if the [ATTR_CELL_TYPE] is [VALUE_CELL_TYPE_INLINE_STRING] */
    private const val TAG_SHEET_DATA_CELL_INLINE_STRING = "is"

    /** the tag within [TAG_SHEET_DATA_CELL_INLINE_STRING] */
    private const val TAG_SHEET_DATA_CELL_INLINE_STRING_TEXT = "t"

    private const val ATTR_CELL_ID = "r"
    private const val ATTR_CELL_TYPE = "t"
    private const val ATTR_CELL_STYLE = "s"

    /** this means the type of the cell is a string in the shared strings */
    private const val VALUE_CELL_TYPE_STRING_REF = "s"

    /** this means the type of the cell is a string inlined directly in the cell */
    private const val VALUE_CELL_TYPE_INLINE_STRING = "inlineStr"

    /** boolean type of cell */
    private const val VALUE_CELL_TYPE_BOOL = "b"

    /** error type of cell */
    private const val VALUE_CELL_TYPE_ERROR = "e"
    private const val VALUE_CELL_BOOLEAN_TRUE = "TRUE"
    private const val VALUE_CELL_BOOLEAN_FALSE = "FALSE"
}

//private val ns: String? = null
//
//@Throws(XmlPullParserException::class, IOException::class)
//fun parse(inputStream: InputStream): List<*> {
//    inputStream.use { inputStream ->
//        val parser: XmlPullParser = Xml.newPullParser()
//        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
//        parser.setInput(inputStream, null)
//        parser.nextTag()
//        return readFeed(parser)
//    }
//}
//
//private fun readFeed(parser: XmlPullParser): List<Entry> {
//    val entries = mutableListOf<Entry>()
//
//    parser.require(XmlPullParser.START_TAG, ns, "feed")
//    while (parser.next() != XmlPullParser.END_TAG) {
//        if (parser.eventType != XmlPullParser.START_TAG) {
//            continue
//        }
//        // Starts by looking for the entry tag.
//        if (parser.name == "entry") {
//            entries.add(readEntry(parser))
//        } else {
//            skip(parser)
//        }
//    }
//    return entries
//}
//
//data class Entry(val title: String?, val summary: String?, val link: String?)
//
//// Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
//// to their respective "read" methods for processing. Otherwise, skips the tag.
//@Throws(XmlPullParserException::class, IOException::class)
//private fun readEntry(parser: XmlPullParser): Entry {
//    parser.require(XmlPullParser.START_TAG, ns, "entry")
//    var title: String? = null
//    var summary: String? = null
//    var link: String? = null
//    while (parser.next() != XmlPullParser.END_TAG) {
//        if (parser.eventType != XmlPullParser.START_TAG) {
//            continue
//        }
//        when (parser.name) {
//            "title" -> title = readTitle(parser)
//            "summary" -> summary = readSummary(parser)
//            "link" -> link = readLink(parser)
//            else -> skip(parser)
//        }
//    }
//    return Entry(title, summary, link)
//}
//
//// Processes title tags in the feed.
//@Throws(IOException::class, XmlPullParserException::class)
//private fun readTitle(parser: XmlPullParser): String {
//    parser.require(XmlPullParser.START_TAG, ns, "title")
//    val title = readText(parser)
//    parser.require(XmlPullParser.END_TAG, ns, "title")
//    return title
//}
//
//// Processes link tags in the feed.
//@Throws(IOException::class, XmlPullParserException::class)
//private fun readLink(parser: XmlPullParser): String {
//    var link = ""
//    parser.require(XmlPullParser.START_TAG, ns, "link")
//    val tag = parser.name
//    val relType = parser.getAttributeValue(null, "rel")
//    if (tag == "link") {
//        if (relType == "alternate") {
//            link = parser.getAttributeValue(null, "href")
//            parser.nextTag()
//        }
//    }
//    parser.require(XmlPullParser.END_TAG, ns, "link")
//    return link
//}
//
//// Processes summary tags in the feed.
//@Throws(IOException::class, XmlPullParserException::class)
//private fun readSummary(parser: XmlPullParser): String {
//    parser.require(XmlPullParser.START_TAG, ns, "summary")
//    val summary = readText(parser)
//    parser.require(XmlPullParser.END_TAG, ns, "summary")
//    return summary
//}
//
//// For the tags title and summary, extracts their text values.
//@Throws(IOException::class, XmlPullParserException::class)
//private fun readText(parser: XmlPullParser): String {
//    var result = ""
//    if (parser.next() == XmlPullParser.TEXT) {
//        result = parser.text
//        parser.nextTag()
//    }
//    return result
//}
//
//@Throws(XmlPullParserException::class, IOException::class)
//private fun skip(parser: XmlPullParser) {
//    if (parser.eventType != XmlPullParser.START_TAG) {
//        throw IllegalStateException()
//    }
//    var depth = 1
//    while (depth != 0) {
//        when (parser.next()) {
//            XmlPullParser.END_TAG -> depth--
//            XmlPullParser.START_TAG -> depth++
//        }
//    }
//}