package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

object SharedStringsParser {
    fun parseSharedStrings(inputStream: InputStream): Result<List<String>> {
        return inputStream.use { inputStream ->
            runCatching {
                val parser: XmlPullParser = Xml.newPullParser().apply {
                    setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                    setInput(inputStream, null)
                }
                parser.parseSharedStrings()
            }
        }
    }

    private fun XmlPullParser.parseSharedStrings(): List<String> {
        return navigateToTag(TAG_SHARED_STRINGS).fold(
            onFailure = {
                throw IllegalArgumentException("error navigating $TAG_SHARED_STRINGS tag on top level cause: ($it)")
            },
            onSuccess = { exist ->
                if (!exist) {
                    throw IllegalArgumentException("can not find, $TAG_SHARED_STRINGS tag on top level, invalid shared strings")
                }
                val sharedStrings = mutableListOf<String>()
                while (true) {
                    val nextSharedString = parseSharedString().getOrNull() ?: break
                    sharedStrings.add(nextSharedString)
                }
                sharedStrings
            }
        )
    }

    private fun XmlPullParser.parseSharedString(): Result<String> {
        return runCatching {
            return@runCatching navigateToTag(TAG_SHARED_STRING).fold(
                onFailure = {
                    throw IllegalArgumentException("error navigating to string tag $TAG_SHARED_STRING, cause ($it)")
                },
                onSuccess = { exist ->
                    if (!exist) {
                        throw IllegalArgumentException("can not find shared string tag $TAG_SHARED_STRING")
                    }
                    return@fold parseSharedStringText()
                }
            )
        }

    }

    private fun XmlPullParser.parseSharedStringText(): String {
        return navigateToTag(TAG_SHARED_STRING_TEXT).fold(
            onFailure = {
                throw IllegalArgumentException("error navigating to string text tag $TAG_SHARED_STRING_TEXT, cause ($it)")
            },
            onSuccess = { exist ->
                if (!exist) {
                    throw IllegalArgumentException("can not find shared string text tag $TAG_SHARED_STRING_TEXT")
                }
                getNextText().fold(
                    onFailure = {
                        throw IllegalArgumentException("error navigating to string text tag $TAG_SHARED_STRING_TEXT, cause ($it)")
                    },
                    onSuccess = {
                        it
                    }
                )
            }
        )
    }

    private const val TAG_SHARED_STRINGS = "sst"
    private const val TAG_SHARED_STRING = "si"
    private const val TAG_SHARED_STRING_TEXT = "t"
}