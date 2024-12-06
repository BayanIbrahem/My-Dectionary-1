package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer

class MDNormalizerWrapper(
    vararg normalizers: MDNormalizer,
) : MDNormalizer(*normalizers) {
    override fun applyCurrentNormalization(sourceString: String): String = sourceString
}
