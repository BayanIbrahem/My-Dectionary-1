package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer

class MDCharsSeparatorNormalizer(
    private val joinSeparator: String = "",
    private val prefix: String = "",
    private val suffix: String = "",
) : MDNormalizer() {
    override fun applyCurrentNormalization(sourceString: String): String {
        return sourceString
            .toList()
            .joinToString(joinSeparator, prefix, suffix)
    }

    companion object {
        val whiteSpaces: Array<String> = arrayOf(" ", "\r", "\n", "\t")
    }
}