package dev.bayan_ibrahim.my_dictionary.domain.model.import_summary

import androidx.compose.runtime.Composable
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
    fun recognizeContextTag(value: String, new: Boolean)
    fun recognizeWord(languageCode: LanguageCode, meaning: String, translation: String, new: Boolean)
    fun recognizeCorruptedWord(languageCode: LanguageCode, meaning: String, translation: String, new: Boolean)
    fun reset()
}

enum class MDFileProcessingSummaryActionsStep(override val strLabel: String, override val icon: MDIconsSet) : LabeledEnum, IconedEnum {
    Start("Start", MDIconsSet.Add), // TODO, string res, icon res
    RecognizingFileType("Read File Type", MDIconsSet.SearchFile), // TODO, string res, icon res
    RecognizingFileReader("Get Suitable File Reader", MDIconsSet.ImportFromFile), // TODO, string res, icon res
    GetAvailableParts("Get available parts in file", MDIconsSet.ExportToFile), // TODO, string res, icon res
    ParseSaveLanguages("Process Languages", MDIconsSet.LanguageWordSpace),// TODO, string res, icon res
    ParseAndSaveWordsClasses("Process Words Classes", MDIconsSet.WordClass),// TODO, string res, icon res
    ParseAndSaveContextTags("Process Context Tags", MDIconsSet.WordTag),// TODO, string res, icon res
    ParseAndSaveWords("Process Words", MDIconsSet.WordMeaning),// TODO, string res, icon res
    End("End", MDIconsSet.Check),// TODO, string res, icon res
}

sealed class MDFileProcessingSummaryStepException : MDFileProcessingSummaryLog {
    override val label: String
        @Composable
        get() = when (this) { // TODO, string res
            is UnRecognizedFileType -> "Unrecognized File Type"
            is UnRecognizedFileReader -> "Unrecognized File Version"
            UnableToGetFileParts -> "Corrupted File Parts"
            ExistedWordTransactionAbort -> "Existed Word"
            CorruptedWordTransactionAbort -> "Corrupted Word"
        }

    override val cause: String
        @Composable
        get() = when (this) { // TODO, string res
            is UnRecognizedFileType -> "Cannot open the file. It may be corrupted or of an unsupported type."
            is UnRecognizedFileReader -> "Unable to find a suitable reader for the file. The file version might be too new for this app, too old, or invalid."
            UnableToGetFileParts -> "The file content is problematic, making it impossible to retrieve its parts."
            ExistedWordTransactionAbort -> "Import aborted due to a conflict with an existing word. An identical word with the same language, meaning, and translation exists."
            CorruptedWordTransactionAbort -> "Import aborted because a corrupted word was detected. The strategy is set to abort on corrupted words."
        }

    override val suggestions: List<String>
        @Composable
        get() = when (this) { // TODO, string res
            is UnRecognizedFileType -> listOf(
                "Ensure the app version (${this.appVersion}) is not older than the version used to export this file.",
                "If this app version is newer than the exporting app's version, contact us for support.",
                "If this file was created by a third-party tool, ensure its format is supported. Check available formats in the export screen."
            )

            is UnRecognizedFileReader -> listOfNotNull(
                if (fileVersion != null && fileVersion > availableFilesVersions.max())
                    "This file version ($fileVersion) is not supported. Supported versions are ${
                        availableFilesVersions.joinToString(", ", "[", "]")
                    }. The current app version is $appVersion. Please verify the file's compatibility."
                else null,
                if (fileVersion != null && fileVersion < availableFilesVersions.min())
                    "This file version ($fileVersion) is too old. Supported versions are ${
                        availableFilesVersions.joinToString(", ", "[", "]")
                    }. Consider updating or verifying the file."
                else null,
                "Contact us with the necessary details for assistance."
            )

            UnableToGetFileParts -> listOf(
                "The file may be corrupted. Try re-exporting it or contact us with the necessary details."
            )

            ExistedWordTransactionAbort -> listOf(
                "Change the conflict resolution strategy or remove conflicting values from your file. This error occurs when data in the imported file conflicts with your existing dataset."
            )

            CorruptedWordTransactionAbort -> listOf(
                "Change the corrupted word handling strategy or fix the invalid words in your file. This error occurs when the file contains words with invalid data, such as a blank meaning."
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
    BlankContextTags,// context tags file part exists but has no data,
    ExistedWordAbort, // when facing a corrupted word and ignore it,
    CorruptedWordAbort; // when facing a corrupted word and ignore it

    override val label: String
        @Composable
        get() = when (this) {
            BlankValidParts -> "No Valid Parts"
            BlankLanguages -> "No Languages Data"
            BlankContextTags -> "No Context Tags Data"
            ExistedWordAbort -> "Word Conflict Detected"
            CorruptedWordAbort -> "Corrupted Word Detected"
        }

    override val cause: String
        @Composable
        get() = when (this) {
            BlankValidParts -> "The file is not empty but it doesn't contain any file parts"
            BlankLanguages -> "The file contains a languages section, but it has no data."
            BlankContextTags -> "The file contains a context tags section, but it has no data."
            ExistedWordAbort -> "A new word was ignored because it conflicts with an existing word in your local dataset."
            CorruptedWordAbort -> "A new word was ignored because it contains corrupted or invalid data."
        }

    override val suggestions: List<String>
        @Composable
        get() = when (this) {
            BlankValidParts -> listOf(
                "Ensure the file contains valid data. While the file isn't empty, it doesn't have any valid file parts."
            )

            BlankLanguages -> listOf(
                "If languages are unnecessary, remove the languages part from the file.",
                "Add valid languages data to the file if required."
            )

            BlankContextTags -> listOf(
                "If context tags are unnecessary, remove the context tags part from the file.",
                "Add valid context tags data to the file if required."
            )

            ExistedWordAbort -> listOf(
                "Change the existing word conflict resolution strategy.",
                "Edit the new word to avoid conflict with existing data.",
                "Edit or delete the conflicting word in your local dataset."
            )

            CorruptedWordAbort -> listOf(
                "Delete or fix the corrupted word in the import file.",
                "Remove the corrupted word from the file before importing."
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