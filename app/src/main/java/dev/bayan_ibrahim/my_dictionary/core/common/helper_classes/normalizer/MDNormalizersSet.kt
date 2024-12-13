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

/**
 * - trim white spaces [MDTrimNormalizer]
 * - convert to lowercase [MDLowercaseNormalizer]
 * - add leading # [MDCustomNormalizer]
 */
val tagViewNormalizer = MDNormalizerWrapper(
    MDTrimNormalizer(),
    MDLowercaseNormalizer,
    MDCustomNormalizer { "#$it" }
)

/**
 * - `Some Tag  ` ->`#some tag`
 * @see [tagViewNormalizer]
 */
val String.tagViewNormalize: String get() = tagViewNormalizer.normalize(this)

/**
 * - convert to lowercase [MDLowercaseNormalizer]
 * - filter not white spaces or # [MDFilterNotNormalizer]
 */
val tagMatchNormalizer = MDNormalizerWrapper(
    MDTrimNormalizer(),
    MDLowercaseNormalizer,
    MDFilterNotNormalizer { it.isWhitespace() || it == '#' },
)

/**
 * - `#some Tag` ->`sometag`
 * @see [tagMatchNormalizer]
 */
val String.tagMatchNormalize: String get() = tagMatchNormalizer.normalize(this)

/**
 * - convert to lowercase [MDLowercaseNormalizer]
 * - filter not white spaces or # [MDFilterNotNormalizer]
 * - split and rejoin spaced by .*? with leading and training .* [MDCharsSeparatorNormalizer]
 */
val tagRegexNormalizer = MDNormalizerWrapper(
    MDLowercaseNormalizer,
    MDFilterNotNormalizer { it.isWhitespace() || it == '#' },
    MDCharsSeparatorNormalizer(joinSeparator = ".*", prefix = ".*", suffix = ".*")
)

/**
 * - `#some Tag` ->`.*?s.*?o.*?m.*?e.*?t.*?a.*?g.*?`
 * @see tagRegexNormalizer
 */
val String.tagRegexNormalize: String get() = tagRegexNormalizer.normalize(this)

