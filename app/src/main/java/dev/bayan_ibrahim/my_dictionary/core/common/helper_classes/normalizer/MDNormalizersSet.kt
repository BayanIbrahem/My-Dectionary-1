package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer

/**
 * - trim white spaces [MDTrimNormalizer]
 * - convert first char to upper case [MDUpperFirstCharNormalizer]
 */
val meaningViewNormalizer = MDNormalizerWrapper(MDTrimNormalizer(), MDUpperFirstCharNormalizer)

/**
 * - `lowercase" -> "Lowercase`
 * - `   whitespace" -> "Whitespace`
 * @see [meaningViewNormalizer]
 */
val String.meaningViewNormalize: String get() = meaningViewNormalizer.normalize(this)

/**
 * - filter not white spaces chars [MDFilterNotNormalizer]
 * - convert to lowercase [MDLowercaseNormalizer]
 */
val meaningSearchNormalizer = MDNormalizerWrapper(MDFilterNotNormalizer(), MDLowercaseNormalizer)

/**
 * - `UPPERCASE` -> `uppercase`
 * - `   whitespace` -> `whitespace`
 * @see [meaningSearchNormalizer]
 */
val String.meaningSearchNormalize: String get() = meaningSearchNormalizer.normalize(this)


/**
 * - filter not white spaces chars [MDFilterNotNormalizer]
 * - split and rejoin spaced by % with leading and training % [MDCharsSeparatorNormalizer]
 * - convert to lowercase [MDLowercaseNormalizer]
 */
val searchQueryDbNormalizer = MDNormalizerWrapper(
    MDFilterNotNormalizer(),
    MDCharsSeparatorNormalizer(joinSeparator = "%", prefix = "%", suffix = "%"),
    MDLowercaseNormalizer,
)

/**
 * - `query` -> `%q%u%e%r%y%`
 * - ` L ` -> `%l%`
 * @see [searchQueryDbNormalizer]
 */
val String.searchQueryDbNormalize: String get() = searchQueryDbNormalizer.normalize(this)

/**
 * - filter not white spaces chars [MDFilterNotNormalizer]
 * - split and rejoin spaced by .*? with leading and training .*? [MDCharsSeparatorNormalizer]
 * - convert to lowercase [MDLowercaseNormalizer]
 */
val searchQueryRegexNormalizer = MDNormalizerWrapper(
    MDFilterNotNormalizer(),
    MDCharsSeparatorNormalizer(joinSeparator = ".*?", prefix = ".*?", suffix = ".*?"),
    MDLowercaseNormalizer,
)

/**
 * - `query` -> `.*?q.*?u.*?e.*?r.*?y.*?`
 * - ` L ` -> `.*?l.*?`
 * @see [searchQueryRegexNormalizer]
 */
val String.searchQueryRegexNormalize: String get() = searchQueryRegexNormalizer.normalize(this)


val tagMatchNormalizer = MDNormalizerWrapper(
    MDTrimNormalizer(),
    MDLowercaseNormalizer,
)

/**
 * - trim and lower case
 * - `some Tag` ->`some tag`
 * @see [tagMatchNormalizer]
 */
val String.tagMatchNormalize: String get() = tagMatchNormalizer.normalize(this)
