package dev.bayan_ibrahim.my_dictionary.domain.model

enum class MDFileStrategy {
    Ignore,
    OverrideAll,

    /**
     * override just valid values from the new word
     */
    OverrideValid,
    Abort,
}
