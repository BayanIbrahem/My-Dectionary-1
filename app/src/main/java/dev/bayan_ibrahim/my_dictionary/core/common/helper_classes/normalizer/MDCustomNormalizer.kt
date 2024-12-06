package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer

data class MDCustomNormalizer(
    private val apply: (sourceString: String) -> String,
) : MDNormalizer() {
    override fun applyCurrentNormalization(sourceString: String): String = apply(sourceString)
}
