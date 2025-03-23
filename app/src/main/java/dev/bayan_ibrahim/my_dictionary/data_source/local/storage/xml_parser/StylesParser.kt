package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

object StylesParser {
    fun parseStyles(inputStream: InputStream): Result<SheetStyles> {
        return inputStream.use { inputStream ->
            runCatching {
                val parser: XmlPullParser = Xml.newPullParser().apply {
                    setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                    setInput(inputStream, null)
                }
                parser.parseStyles()
            }
        }
    }

    private fun XmlPullParser.parseStyles(): SheetStyles {
        return navigateToTag(TAG_SHEET).fold(
            onFailure = {
                throw IllegalArgumentException("error finding styles sheet tag, cause: $it")
            },
            onSuccess = { exist ->
                if (!exist) {
                    throw IllegalArgumentException("can not find styles sheet tag")
                }

                val styles = parseCellStyles()
                SheetStyles(styles)
            }
        )
    }

    private fun XmlPullParser.parseCellStyles(): List<SheetCellStyle> {
        return navigateToTag(TAG_CELL_STYLES).fold(
            onFailure = {
                throw IllegalArgumentException("error finding cell styles $TAG_CELL_STYLE tag, cause: $it")
            },
            onSuccess = { exist ->
                if (!exist) {
                    throw IllegalArgumentException("can not find cell styles tag")
                }
                val styles = mutableListOf<SheetCellStyle>()
                while (true) {
                    val style = parseCellStyle().getOrNull() ?: break
                    styles.add(style)
                }
                styles
            }
        )
    }

    private fun XmlPullParser.parseCellStyle(): Result<SheetCellStyle> {
        return runCatching {
            navigateToTag(TAG_CELL_STYLE).fold(
                onFailure = {
                    throw IllegalArgumentException("error finding cell style tag cause: $it")
                },
                onSuccess = { exist ->
                    if (!exist) {
                        throw IllegalArgumentException("can not find cell style tag")
                    }
                    val attr = getAttributesMap()
                    val numIdValue =
                        attr[ATTR_CELL_STYLE_NUM_ID] ?: throw IllegalArgumentException(" can not find number style id attribute in a cell style tag")
                    val value = numIdValue.toIntOrNull()
                        ?: throw IllegalArgumentException("invalid number style id attribute value ($numIdValue) require a valid int in a cell style tag")
                    return@fold SheetCellStyle(value)
                }
            )

        }
    }

    private const val TAG_SHEET = "styleSheet"
    private const val TAG_CELL_STYLES = "cellXfs"
    private const val TAG_CELL_STYLE = "xf"
    private const val ATTR_CELL_STYLE_NUM_ID = "numFmtId"

}