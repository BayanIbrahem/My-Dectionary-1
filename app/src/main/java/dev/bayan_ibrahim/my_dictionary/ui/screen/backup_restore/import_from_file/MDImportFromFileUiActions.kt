package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file

import android.net.Uri
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDExtraTagsStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions

interface MDImportFromFileBusinessUiActions {
    fun onPickFile(uri: Uri)
    fun onRemoveFile()

    fun onToggleSelectAvailablePart(type: MDFilePartType, selected: Boolean)

    fun onChangeCorruptedWordStrategy(strategy: MDPropertyCorruptionStrategy)
    fun onChangeExistedWordStrategy(strategy: MDPropertyConflictStrategy)
    fun onChangeExtraTagsStrategy(strategy: MDExtraTagsStrategy)
    fun onStartImportProcess()
    fun onCancelImportProcess()
    fun onUnselectTag(tag: Tag)
    fun onSetSelectedTags(tags: kotlin.collections.List<dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag>)
}

interface MDImportFromFileNavigationUiActions : MDAppNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDImportFromFileUiActions(
    navigationActions: MDImportFromFileNavigationUiActions,
    businessActions: MDImportFromFileBusinessUiActions,
) : MDImportFromFileBusinessUiActions by businessActions, MDImportFromFileNavigationUiActions by navigationActions