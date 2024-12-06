package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer

class MDFilterNotNormalizer(
    private val disallowedChars: (Char) -> Boolean = ::whiteSpacesPredicate,
) : MDNormalizer() {
    override fun applyCurrentNormalization(sourceString: String): String {
        return sourceString.filterNot(disallowedChars)
    }

    companion object {
        fun whiteSpacesPredicate(char: Char): Boolean = char.isWhitespace()

        private val smiWhiteSpaces: Set<Char> = setOf('-', '_', '.', ',', ':', ';', '\\', '/', '|')
        fun smiWhiteSpacePredicate(char: Char): Boolean = char in smiWhiteSpaces
    }
}