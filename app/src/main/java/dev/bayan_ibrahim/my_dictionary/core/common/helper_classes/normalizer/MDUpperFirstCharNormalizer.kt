package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer

object MDUpperFirstCharNormalizer : MDNormalizer() {
    override fun applyCurrentNormalization(sourceString: String): String {
        return sourceString.replaceFirstChar(Char::uppercaseChar)
    }
}