package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser

import org.xmlpull.v1.XmlPullParser

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

fun XmlPullParser.getAttributesMap(): Map<String, String> = (0..<this.attributeCount).mapNotNull {
    Pair(
        first = getAttributeName(it) ?: return@mapNotNull null,
        second = getAttributeValue(it) ?: return@mapNotNull null
    )
}.toMap()
