package dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet

import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode

sealed interface MDSheetImportProgressStatus {
    data object Queue : MDSheetImportProgressStatus
    data object ParsingRows : MDSheetImportProgressStatus
    data object ProcessingRows : MDSheetImportProgressStatus
    data object Finished : MDSheetImportProgressStatus
    sealed interface Failed : MDSheetImportProgressStatus {
        sealed interface Tag : Failed {
            /**
             * tag label is null or blank and not valid
             */
            data object InvalidTagLabel : Tag

            /**
             * tag label is existed in the database in conditions where conflict abort the transaction
             */
            data class ConflictTagLabel(val label: String) : Tag
        }
        sealed interface Word: Failed {
            /**
             * when the word is invalid in at least one of [code], [meaning], [translation]
             */
            data class InvalidWord(
                val code: String?,
                val meaning: String?,
                val translation: String?,
            ): Word

            data class ConflictWord(
                val code: LanguageCode,
                val meaning: String,
                val translation: String,
            ): Word
        }
    }
}