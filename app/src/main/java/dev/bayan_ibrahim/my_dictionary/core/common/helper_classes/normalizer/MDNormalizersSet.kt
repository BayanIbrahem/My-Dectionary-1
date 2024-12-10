package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer

val meaningViewNormalizer = MDNormalizerWrapper(MDTrimNormalizer(), MDUpperFirstCharNormalizer)
val String.meaningViewNormalize: String get() = meaningViewNormalizer.normalize(this)

val meaningSearchNormalizer = MDNormalizerWrapper(MDFilterNotNormalizer(), MDLowercaseNormalizer)
val String.meaningSearchNormalize: String get() = meaningSearchNormalizer.normalize(this)


val searchQueryDbNormalizer = MDNormalizerWrapper(
    MDFilterNotNormalizer(),
    MDCharsSeparatorNormalizer(joinSeparator = "%", prefix = "%", suffix = "%"),
    MDLowercaseNormalizer,
)
val String.searchQueryDbNormalize: String get() = searchQueryDbNormalizer.normalize(this)

val searchQueryRegexNormalizer = MDNormalizerWrapper(
    MDFilterNotNormalizer(),
    MDCharsSeparatorNormalizer(joinSeparator = ".*?", prefix = ".*?", suffix = ".*?"),
    MDLowercaseNormalizer,
)
val String.searchQueryRegexNormalize: String get() = searchQueryRegexNormalizer.normalize(this)


val tagViewNormalizer = MDNormalizerWrapper(MDTrimNormalizer(), MDLowercaseNormalizer, MDCustomNormalizer { "#$it" })
val String.tagViewNormalize: String get() = tagViewNormalizer.normalize(this)

val tagMatchNormalizer = MDNormalizerWrapper(
    MDLowercaseNormalizer,
    MDFilterNotNormalizer { it.isWhitespace() || it == '#' },
)

val tagRegexNormalizer = MDNormalizerWrapper(
    MDLowercaseNormalizer,
    MDFilterNotNormalizer { it.isWhitespace() || it == '#' },
    MDCharsSeparatorNormalizer(joinSeparator = ".*", prefix = ".*", suffix = ".*")
)
val String.tagMatchNormalize: String get() = tagMatchNormalizer.normalize(this)
val String.tagRegexNormalize: String get() = tagRegexNormalizer.normalize(this)

