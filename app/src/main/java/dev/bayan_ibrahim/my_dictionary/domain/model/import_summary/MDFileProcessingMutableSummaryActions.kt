package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum


interface MDFileProcessingSummaryActions {
    fun onStep(step: MDFileProcessingSummaryActionsStep)
    fun onException(stepException: MDFileProcessingSummaryStepException)
    fun onWarning(stepWarning: MDFileProcessingSummaryStepWarning)
    fun recognizeLanguage(code: LanguageCode, new: Boolean)
    fun recognizeWordClass(languageCode: LanguageCode, name: String, new: Boolean)
    fun recognizeWordClassRelation(languageCode: LanguageCode, wordClassName: String, relationLabel: String, new: Boolean)
    fun recognizeTag(value: String, new: Boolean)
    fun recognizeWord(languageCode: LanguageCode, meaning: String, translation: String, new: Boolean)
    fun recognizeCorruptedWord(languageCode: LanguageCode, meaning: String, translation: String, new: Boolean)
    fun reset()
}

enum class MDFileProcessingSummaryActionsStep(@StringRes val labelRes: Int, override val icon: MDIconsSet) : LabeledEnum, IconedEnum {
    Start(R.string.start, MDIconsSet.Add),
    RecognizingFileType(R.string.read_file_type, MDIconsSet.SearchFile),
    RecognizingFileReader(R.string.get_suitable_file_reader, MDIconsSet.ImportFromFile),
    GetAvailableParts(R.string.get_available_parts, MDIconsSet.ExportToFile),
    ParseSaveLanguages(R.string.process_languages, MDIconsSet.LanguageWordSpace),
    ParseAndSaveWordsClasses(R.string.process_words_classes, MDIconsSet.WordClass),
    ParseAndSaveTags(R.string.process_tags, MDIconsSet.WordTag),
    ParseAndSaveWords(R.string.process_words, MDIconsSet.WordMeaning),
    End(R.string.end, MDIconsSet.Check);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(labelRes)
}

sealed class MDFileProcessingSummaryStepException : MDFileProcessingSummaryLog {
    override val label: String
        @Composable
        get() = when (this) {
            is UnRecognizedFileType -> stringResource(R.string.error_unrecognized_file_type)
            is UnRecognizedFileReader -> stringResource(R.string.error_unrecognized_file_version)
            UnableToGetFileParts -> stringResource(R.string.error_corrupted_file_parts)
            ExistedWordTransactionAbort -> stringResource(R.string.error_existed_word)
            CorruptedWordTransactionAbort -> stringResource(R.string.error_corrupted_word)
        }

    override val cause: String
        @Composable
        get() = when (this) {
            is UnRecognizedFileType -> stringResource(R.string.error_unrecognized_file_type_cause)
            is UnRecognizedFileReader -> stringResource(R.string.error_unrecognized_file_reader_cause)
            UnableToGetFileParts -> stringResource(R.string.error_corrupted_file_parts_cause)
            ExistedWordTransactionAbort -> stringResource(R.string.error_existed_word_cause)
            CorruptedWordTransactionAbort -> stringResource(R.string.error_corrupted_word_cause)
        }

    override val suggestions: List<String>
        @Composable
        get() = when (this) {
            is UnRecognizedFileType -> listOf(
                stringResource(R.string.error_unrecognized_file_type_suggestion_1, this.appVersion),
                stringResource(R.string.error_unrecognized_file_type_suggestion_2),
                stringResource(R.string.error_unrecognized_file_type_suggestion_3)
            )

            is UnRecognizedFileReader -> listOfNotNull(
                if (fileVersion != null && fileVersion > availableFilesVersions.max())
                    stringResource(
                        R.string.error_unrecognized_file_reader_suggestion_1,
                        fileVersion,
                        availableFilesVersions.joinToString(", ", "[", "]"),
                        appVersion
                    )
                else null,
                if (fileVersion != null && fileVersion < availableFilesVersions.min())
                    stringResource(
                        R.string.error_unrecognized_file_reader_suggestion_2,
                        fileVersion,
                        availableFilesVersions.joinToString(", ", "[", "]")
                    )
                else null,
                stringResource(R.string.error_unrecognized_file_reader_suggestion_3)
            )

            UnableToGetFileParts -> listOf(
                stringResource(R.string.error_corrupted_file_parts_suggestion_1)
            )

            ExistedWordTransactionAbort -> listOf(
                stringResource(R.string.error_existed_word_suggestion_1)
            )

            CorruptedWordTransactionAbort -> listOf(
                stringResource(R.string.error_corrupted_word_suggestion_1)
            )
        }

    data class UnRecognizedFileType(val appVersion: String) :
        MDFileProcessingSummaryStepException() // no valid factory for file, unable to open file or unable to file valid reader for this file

    data class UnRecognizedFileReader(
        val appVersion: String,
        val fileVersion: Int?,
        val availableFilesVersions: List<Int>,
    ) : MDFileProcessingSummaryStepException() // valid factory (valid version) but no reader can handle with this version, version value is not a valid value

    data object UnableToGetFileParts :
        MDFileProcessingSummaryStepException() // version of file is valid and type is valid but there is a problem in getting file parts (not an empty parts but parts with exception)

    data object ExistedWordTransactionAbort :
        MDFileProcessingSummaryStepException() // facing a corrupted word and abort the transaction because of it

    data object CorruptedWordTransactionAbort :
        MDFileProcessingSummaryStepException() // facing a corrupted word and abort the transaction because of it
}

enum class MDFileProcessingSummaryStepWarning : MDFileProcessingSummaryLog {
    BlankValidParts, // no exception in getting file parts but parts are invalid, in other words this file is blank
    BlankLanguages, // languages file part exists but has no data,
    BlankTags,// tags file part exists but has no data,
    ExistedWordAbort, // when facing a corrupted word and ignore it,
    CorruptedWordAbort; // when facing a corrupted word and ignore it

    override val label: String
        @Composable
        get() = when (this) {
            BlankValidParts -> stringResource(R.string.warning_no_valid_parts)
            BlankLanguages -> stringResource(R.string.warning_no_languages_data)
            BlankTags -> stringResource(R.string.warning_no_tags_data)
            ExistedWordAbort -> stringResource(R.string.warning_word_conflict_detected)
            CorruptedWordAbort -> stringResource(R.string.warning_corrupted_word_detected)
        }

    override val cause: String
        @Composable
        get() = when (this) {
            BlankValidParts -> stringResource(R.string.warning_no_valid_parts_cause)
            BlankLanguages -> stringResource(R.string.warning_no_languages_data_cause)
            BlankTags -> stringResource(R.string.warning_no_tags_data_cause)
            ExistedWordAbort -> stringResource(R.string.warning_word_conflict_cause)
            CorruptedWordAbort -> stringResource(R.string.warning_corrupted_word_detected_cause)
        }

    override val suggestions: List<String>
        @Composable
        get() = when (this) {
            BlankValidParts -> listOf(
                stringResource(R.string.warning_no_valid_parts_suggestion_1)
            )

            BlankLanguages -> listOf(
                stringResource(R.string.warning_no_language_data_suggestion_1),
                stringResource(R.string.warning_no_language_data_suggestion_2)
            )

            BlankTags -> listOf(
                stringResource(R.string.warning_no_tags_data_suggestion_1),
                stringResource(R.string.warning_no_tags_data_suggestion_2)
            )

            ExistedWordAbort -> listOf(
                stringResource(R.string.warning_word_conflict_suggestion_1),
                stringResource(R.string.warning_word_conflict_suggestion_2),
                stringResource(R.string.warning_word_conflict_suggestion_3)
            )

            CorruptedWordAbort -> listOf(
                stringResource(R.string.warning_corrupted_word_detected_suggestion_1),
                stringResource(R.string.warning_corrupted_word_detected_suggestion_2)
            )
        }
}

interface MDFileProcessingSummaryLog {
    @get:Composable
    val label: String

    @get:Composable
    val cause: String

    @get:Composable
    val suggestions: List<String>
}