package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser

import org.xmlpull.v1.XmlPullParser

data class XmlElement(
    val name: String,
    val attributes: Map<String, String>,
    val children: MutableList<XmlElement> = mutableListOf(),
    var text: String? = null,
) {
    override fun toString(): String = buildString()

    fun buildString(level: Int = 0): String {
        var lv = level
        fun getLeadingBlanks(lv: Int = level): String = "  ".repeat(lv)
        fun StringBuilder.getLineWithTab(content: String, lv: Int = level) = appendLine(getLeadingBlanks(lv) + content)
        return buildString {
            getLineWithTab("name: $name", lv)
            getLineWithTab("attributes", lv)
            lv++
            attributes.forEach { (key, value) ->
                getLineWithTab("`$key` -> $value", lv)
            }
            getLineWithTab("children")
            children.forEach {
                append(it.buildString(lv))
            }
            lv--
            getLineWithTab("text: $text", lv)
        }
    }
}

/**
 * return the next next text as result
 */
fun XmlPullParser.getNextText(): Result<String> = runCatching {
    nextText()
}
/**
 * @return true if navigated to tag or false if tag is not found
 */
fun XmlPullParser.navigateToTag(tag: String): Result<Boolean> {
    return runCatching {
        while (next() != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && this.name == tag) {
                return@runCatching true
            }
        }
        return@runCatching false
    }
}
