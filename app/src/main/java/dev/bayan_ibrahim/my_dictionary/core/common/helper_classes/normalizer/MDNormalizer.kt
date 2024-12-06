package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer

abstract class MDNormalizer(
    /**
     * pass a list of normalizers to normalize strings in order,
     * pass null if you want to use this normalizer content
     */
    subNormalizers: List<MDNormalizer?> = emptyList(),
    /**
     * if true this allows executing more than one instance of current normalizer else it would ignore them starting
     * from second one
     */
    private val allowApplyCurrentMultipleTimes: Boolean = true,
) {
    constructor(
        vararg subNormalizers: MDNormalizer?,
        allowApplyCurrentMultipleTimes: Boolean = true,
    ) : this(
        subNormalizers = subNormalizers.toList(),
        allowApplyCurrentMultipleTimes = allowApplyCurrentMultipleTimes
    )

    constructor(
        prefixSubNormalizer: List<MDNormalizer>,
        suffixSubNormalizer: List<MDNormalizer>,
    ) : this(
        listOf(
            *prefixSubNormalizer.toTypedArray(),
            null,
            *suffixSubNormalizer.toTypedArray(),
        )
    )

    protected abstract fun applyCurrentNormalization(sourceString: String): String
    private var currentNormalizer = listOf(::applyCurrentNormalization)
        get() {
            return field.also {
                if (!allowApplyCurrentMultipleTimes) {
                    currentNormalizer = emptyList()
                }
            }
        }
    private val applyNormalizationsCallbacks: List<(String) -> String> = if (
        subNormalizers.isEmpty()
    ) {
        currentNormalizer
    } else {
        subNormalizers.flatMap { normalizer ->
            normalizer?.applyNormalizationsCallbacks ?: currentNormalizer
        }
    }

    fun normalize(sourceString: String): String {
        var result = sourceString
        applyNormalizationsCallbacks.forEach { callback ->
            result = callback(result)
        }
        return result
    }

    fun normalize(char: Char): Char? {
        var result = char.toString()
        applyNormalizationsCallbacks.forEach { callback ->
            result = callback(result)
        }
        return result.firstOrNull()
    }

    fun valid(char: Char): Boolean = normalize(char) != null
}

/*
    fun normalize(sourceString: String): String {
        return mapCharsCase(
            sourceString = sourceString,
            separators = separators,
            normalizedCase = normalizedCase
        ).mapNotNull { char ->
            if (char in extraChars) null
            else if (normalizedSeparator != null && char in separators) normalizedSeparator
            else char
        }.joinToString("", normalizedPrefix, normalizedSuffix)
    }

    enum class CaseShape {
        NoEffect,
        CapitalizedFirstChar,
        CapitalizedFirstCharEachWord,
        Invert,
        Uppercase,
        Lowercase,
    }

    private fun mapCharsCase(
        sourceString: String,
        separators: Set<Char>,
        normalizedCase: CaseShape,
    ): String {
        return when (normalizedCase) {
            CaseShape.NoEffect -> sourceString

            CaseShape.CapitalizedFirstChar -> sourceString.replaceFirstChar { it.uppercaseChar() }

            CaseShape.CapitalizedFirstCharEachWord -> sourceString
                .split(
                    *separators.toCharArray()
                ).joinToString("") { word ->
                    word.replaceFirstChar { char ->
                        char.uppercaseChar()
                    }
                }

            CaseShape.Invert -> sourceString.map {
                if (it.isLowerCase()) {
                    it.uppercaseChar()
                } else {
                    it.lowercaseChar()
                }
            }.joinToString("")

            CaseShape.Uppercase -> sourceString.uppercase()

            CaseShape.Lowercase -> sourceString.lowercase()
        }

    }
 */