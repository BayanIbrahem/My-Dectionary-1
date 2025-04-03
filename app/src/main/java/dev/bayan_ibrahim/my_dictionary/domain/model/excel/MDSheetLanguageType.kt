package dev.bayan_ibrahim.my_dictionary.domain.model.excel

sealed interface MDSheetLanguageType {
    data object Global : MDSheetLanguageType
    data class LanguageSpecific(val languageCode: String) : MDSheetLanguageType
    companion object {
        /**
         * if the sheet has language code then a certain separator and then it is a [LanguageSpecific] otherwise
         * it is [Global]
         * examples of language specific:
         * en_some_other_name
         * DU-some other name
         * du some other name
         * examples of global sheet
         * du
         * some name
         * `blank`
         */
        operator fun invoke(rawName: String): MDSheetLanguageType = TODO()
    }
}