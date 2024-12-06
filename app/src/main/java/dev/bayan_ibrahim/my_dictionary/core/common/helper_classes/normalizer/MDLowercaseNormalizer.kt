package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer

object MDLowercaseNormalizer : MDNormalizer() {
    override fun applyCurrentNormalization(sourceString: String): String {
        return sourceString.lowercase()
    }
}