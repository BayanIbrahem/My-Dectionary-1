package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.core.ui.MDWordFieldTextField
import dev.bayan_ibrahim.my_dictionary.core.ui.UnavailableComponentHint
import dev.bayan_ibrahim.my_dictionary.core.ui.scrollbar
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component.WordDetailsTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component.wordDetailsRelatedWordsTextFieldsList
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component.wordDetailsTextFieldList
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordDetailsScreen(
    uiState: WordDetailsUiState,
    uiActions: WordDetailsUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier.fillMaxSize(),
    ) {
        val state = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollbar(state, stickHeadersContentType = 1),
            state = state,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                start = 8.dp,
                top = 0.dp,
                end = 8.dp,
                bottom = 8.dp,
            )
        ) {
            stickyHeader(
                contentType = 1
            ) {
                WordDetailsTopAppBar(
                    isEditModeOn = uiState.isEditModeOn,
                    validWord = uiState.valid,
                    language = uiState.language,
                    isNewWord = uiState.id == INVALID_ID,
                    memorizingProbability = uiState.memorizingProbability,
                    onEdit = uiActions::onEnableEditMode,
                    onSave = uiActions::onSaveChanges,
                    onCancel = uiActions::onCancelChanges,
                    onShare = {
                        // TODO, on share
                    }
                )
            }
            item {
                MDWordFieldTextField(
                    value = uiState.meaning,
                    onValueChange = uiActions::onMeaningChange,
                    leadingIcon = MDIconsSet.WordMeaning, // checked
                    modifier = Modifier.fillMaxWidth(),
                    label = "Meaning", // TODO, string res
                    readOnly = !uiState.isEditModeOn,
                )
            }
            item {
                MDWordFieldTextField(
                    value = uiState.translation,
                    onValueChange = uiActions::onTranslationChange,
                    leadingIcon = MDIconsSet.WordTranslation, // checked
                    modifier = Modifier.fillMaxWidth(),
                    label = "Translation", // TODO, string res
                    readOnly = !uiState.isEditModeOn,
                )
            }
            item {
                MDWordFieldTextField(
                    value = uiState.transcription,
                    onValueChange = uiActions::onTranscriptionChange,
                    leadingIcon = MDIconsSet.WordTranscription, // checked
                    modifier = Modifier.fillMaxWidth(),
                    label = "Transcription", // TODO, string res
                    readOnly = !uiState.isEditModeOn,
                    hasBottomHorizontalDivider = true
                )
            }
            wordDetailsTextFieldList(
                items = uiState.additionalTranslations,
                onItemValueChange = uiActions::onEditAdditionalTranslation,
                leadingIcon = MDIconsSet.WordAdditionalTranslation,// checked
                groupLabel = "Additional Translations", // TODO, string res
                onGroupFocusChanged = uiActions::onValidateAdditionalTranslations
            )
            wordDetailsTextFieldList(
                items = uiState.tags,
                onItemValueChange = uiActions::onEditTag,
                leadingIcon = MDIconsSet.WordTag,// checked
                groupLabel = "Tags", // TODO, string res
                onGroupFocusChanged = uiActions::onValidateTags
            )
            wordDetailsTextFieldList(
                items = uiState.examples,
                onItemValueChange = uiActions::onEditExample,
                leadingIcon = MDIconsSet.WordExample,// checked
                groupLabel = "Examples", // TODO, string res
                onGroupFocusChanged = uiActions::onValidateExamples
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
                        leadingIcon = MDIconsSet.WordTypeTag,// checked
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
                onItemValueChange = uiActions::onEditRelatedWordValue,
                typeRelations = uiState.selectedTypeTag?.relations,
                onSelectRelation = { id, relation ->
                    uiActions.onEditRelatedWordRelation(id, relation)
                },
                leadingIcon = MDIconsSet.WordRelatedWords,// checked
                groupLabel = "Word Relations", // TODO, string res
                onGroupFocusChanged = uiActions::onValidateRelatedWords
            )
        }
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
                    uiActions = getUiActions()
                )
            }
        }
    }
}

private fun getUiActions() = WordDetailsUiActions(
    object : WordDetailsNavigationUiActions {
        override fun pop() {}
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
        override fun onEditTag(id: Long, newTag: String) {}
        override fun onValidateTags(focusedTextFieldId: Long?) {}
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