package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.core.ui.MDWordFieldTextField
import dev.bayan_ibrahim.my_dictionary.core.ui.UnavailableComponentHint
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagExplorerDialog
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionActions
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionActionsImpl
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionUiState
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.contextTagsSelectionItem
import dev.bayan_ibrahim.my_dictionary.core.ui.scrollbar
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component.WordDetailsTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component.wordDetailsRelatedWordsTextFieldsList
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component.wordDetailsTextFieldList
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun WordDetailsScreen(
    uiState: WordDetailsUiState,
    uiActions: WordDetailsUiActions,
    contextTagsState: MDContextTagsSelectionUiState,
    contextTagsActions: MDContextTagsSelectionActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier.fillMaxSize(),
        topBar = {
            WordDetailsTopAppBar(
                isEditModeOn = uiState.isEditModeOn,
                validWord = uiState.valid,
                language = uiState.language,
                isNewWord = uiState.id == INVALID_ID,
                onEdit = uiActions::onEnableEditMode,
                onSave = uiActions::onSaveChanges,
                onCancel = uiActions::onCancelChanges,
                onClickWordStatistics = {
                    uiState.id.nullIfInvalid()?.let {
                        uiActions.navigateToWordStatistics(wordId = it)
                    }
                },
                onShare = {
                    // TODO, on share
                }
            )
        }
    ) {
        var showTagsExplorerDialog by remember {
            mutableStateOf(false)
        }
        MDContextTagExplorerDialog(
            showDialog = showTagsExplorerDialog,
            onDismissRequest = { showTagsExplorerDialog = false },
            state = contextTagsState,
            actions = contextTagsActions,
            allowAddTags = uiState.isEditModeOn,
            allowRemoveTags = uiState.isEditModeOn
        )
        val state = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollbar(state, stickHeadersContentType = 1),
            state = state,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                MDWordFieldTextField(
                    value = uiState.meaning,
                    onValueChange = uiActions::onMeaningChange,
                    leadingIcon = MDIconsSet.WordMeaning,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Meaning", // TODO, string res
                    readOnly = !uiState.isEditModeOn,
                )
            }
            item {
                MDWordFieldTextField(
                    value = uiState.translation,
                    onValueChange = uiActions::onTranslationChange,
                    leadingIcon = MDIconsSet.WordTranslation,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Translation", // TODO, string res
                    readOnly = !uiState.isEditModeOn,
                )
            }
            item {
                MDWordFieldTextField(
                    value = uiState.transcription,
                    onValueChange = uiActions::onTranscriptionChange,
                    leadingIcon = MDIconsSet.WordTranscription,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Transcription", // TODO, string res
                    readOnly = !uiState.isEditModeOn,
                    hasBottomHorizontalDivider = true
                )
            }
            wordDetailsTextFieldList(
                items = uiState.additionalTranslations,
                onItemValueChange = uiActions::onEditAdditionalTranslation,
                leadingIcon = MDIconsSet.WordAdditionalTranslation,
                groupLabel = "Additional Translations", // TODO, string res
                onGroupFocusChanged = uiActions::onValidateAdditionalTranslations,
                isEditModeOn = uiState.isEditModeOn,
            )
            contextTagsSelectionItem(
                state = contextTagsState,
                actions = contextTagsActions,
                allowEditTags = uiState.isEditModeOn,
                onAddNewTagClick = {
                    showTagsExplorerDialog = true
                }
            )
            wordDetailsTextFieldList(
                items = uiState.examples,
                onItemValueChange = uiActions::onEditExample,
                leadingIcon = MDIconsSet.WordExample,
                groupLabel = "Examples", // TODO, string res
                onGroupFocusChanged = uiActions::onValidateExamples,
                isEditModeOn = uiState.isEditModeOn
            )
            item {
                if (uiState.typeTags.isNotEmpty()) {
                    MDWordFieldTextField(
                        value = uiState.selectedTypeTag,
                        suggestions = uiState.typeTags,
                        onValueChange = {},
                        onSelectSuggestion = { i, type ->
                            uiActions.onChangeTypeTag(type)
                        },
                        suggestionTitle = { this.name },
                        leadingIcon = MDIconsSet.WordTypeTag,
                        fieldModifier = Modifier.fillMaxWidth(),
                        label = "Word Type", // TODO, string res
                        fieldReadOnly = true,
                        menuReadOnly = !uiState.isEditModeOn,
                        hasBottomHorizontalDivider = true
                    )
                } else if (uiState.isEditModeOn) {
                    UnavailableComponentHint(
                        text = "No Tag Types in ${uiState.language.localDisplayName}, add some before selecting word type tag",
                    ) // TODO, string res
                }
            }
            wordDetailsRelatedWordsTextFieldsList(
                items = uiState.relatedWords,
                isEditMode = uiState.isEditModeOn,
                onItemValueChange = uiActions::onEditRelatedWordValue,
                typeRelations = uiState.selectedTypeTag?.relations,
                onSelectRelation = { id, relation ->
                    uiActions.onEditRelatedWordRelation(id, relation)
                },
                leadingIcon = MDIconsSet.WordRelatedWords,
                groupLabel = "Word Relations", // TODO, string res
                onGroupFocusChanged = uiActions::onValidateRelatedWords
            )
        }
    }
}

@Composable
private fun ContextTagListItem(
    tag: ContextTag,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        tag.segments.forEach { segment ->
            Box(
                modifier = Modifier.height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(segment)
            }
        }
    }
}

@Composable
private fun NewContextTagListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("New Tag")
    }
}

@Preview
@Composable
private fun WordDetailsScreenPreview() {
    val uiState by remember {
        derivedStateOf {
            WordDetailsMutableUiState().apply {
                this.onFinishDataLoad(true)
                this.isEditModeOn = false
            }
        }
    }
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier) {
                WordDetailsScreen(
                    uiState = uiState,
                    uiActions = getUiActions(),
                    contextTagsState = MDContextTagsSelectionMutableUiState(),
                    contextTagsActions = MDContextTagsSelectionActionsImpl(
                        state = MDContextTagsSelectionMutableUiState(),
                        onAddNewTag = {},
                        onDeleteTag = {}
                    )
                )
            }
        }
    }
}

private fun getUiActions() = WordDetailsUiActions(
    object : WordDetailsNavigationUiActions {
        override fun pop() {}
        override fun navigateToWordStatistics(wordId: Long) {}
    },
    object : WordDetailsBusinessUiActions {
        override fun onEnableEditMode() {}
        override fun onCancelChanges() {}
        override fun onSaveChanges() {}
        override fun onMeaningChange(newMeaning: String) {}
        override fun onTranslationChange(newTranslation: String) {}
        override fun onTranscriptionChange(newTranscription: String) {}
        override fun onEditAdditionalTranslation(id: Long, newAdditionalTranslation: String) {}
        override fun onValidateAdditionalTranslations(focusedTextFieldId: Long?) {}
        override fun onChangeTypeTag(newTypeTag: WordTypeTag?) {}
        override fun onAddNewRelatedWord(relation: WordTypeTagRelation) {}
        override fun onEditRelatedWordRelation(id: Long, newRelation: WordTypeTagRelation) {}
        override fun onEditRelatedWordValue(id: Long, newValue: String) {}
        override fun onRemoveRelatedWord(id: Long, relation: String, value: String) {}
        override fun onValidateRelatedWords(focusedTextFieldId: Long?) {}
        override fun onEditExample(id: Long, newExample: String) {}
        override fun onValidateExamples(focusedTextFieldId: Long?) {}
    }
)