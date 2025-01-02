package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDropDownMenu
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTitleWithHint
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.core.ui.MDWordFieldTextField
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorBusinessUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorMutableUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.contextTagsSelector
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode.component.MDWordDetailsEditModeTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun MDWordDetailsEditModeScreen(
    uiState: MDWordDetailsEditModeUiState,
    uiActions: MDWordDetailsEditModeUiActions,
    contextTagsState: MDContextTagsSelectorUiState,
    contextTagsActions: MDContextTagsSelectorUiActions,
    modifier: Modifier = Modifier,
    spacedBy: Dp = 8.dp,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        topBar = {
            MDWordDetailsEditModeTopAppBar(
                language = uiState.language,
                validWord = uiState.valid,
                onCancel = uiActions::onPop,
                onSave = uiActions::onSave
            )
        },
    ) {
        val availableTypeTags by uiState.availableTypeTags.collectAsStateWithLifecycle()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(spacedBy)
        ) {
            editableGroup(
                title = "Basic", // TODO, string res
                icon = MDIconsSet.WordMeaning, // TODO, icon res
            ) {
                item {
                    MDWordFieldTextField(
                        value = uiState.meaning,
                        onValueChange = uiActions::onEditMeaning,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = "Meaning e.g Car",
                        label = "Meaning",
                        // TODO, string res
                        leadingIcon = MDIconsSet.WordMeaning,
                    )
                }
                item {
                    MDWordFieldTextField(
                        value = uiState.translation,
                        onValueChange = uiActions::onEditTranslation,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = "Translation e.g Koruma",
                        label = "Translation", // TODO, string res
                        leadingIcon = MDIconsSet.WordTranslation,
                    )
                }
            }

            editableGroup(
                title = "Phonetic", // TODO, string res
                icon = MDIconsSet.WordTranscription,
            ) {
                item {
                    MDWordFieldTextField(
                        value = uiState.transcription,
                        onValueChange = uiActions::onEditTranscription,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = "Transcription (phonetic symbol)",
                        label = "Transcription", // TODO, string res
                        leadingIcon = MDIconsSet.WordTranscription,
                    )
                }
            }

            editableGroup(
                title = "Context tags", // TODO, string res
                icon = MDIconsSet.WordTag,
            ) {
                contextTagsSelector(
                    state = contextTagsState,
                    actions = contextTagsActions,
                    spacedBy = spacedBy,
                    showTitle = false,
                    showHorizontalDivider = false,
                )
            }

            editableGroup(
                title = "Additional Translations", // TODO string res
                icon = MDIconsSet.WordAdditionalTranslation,
            ) {
                itemsIndexed(
                    items = uiState.additionalTranslations.toList().sortedBy { it.first },
                    key = { _, (id, _) -> id },
                ) { i, (id, value) ->
                    val isLast = i == uiState.additionalTranslations.count().dec()
                    MDWordFieldTextField(
                        value = value,
                        onValueChange = { newValue ->
                            uiActions.onEditAdditionalTranslations(id, newValue)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                        onFocusEvent = {
                            if (it.isFocused) {
                                uiActions.onAdditionalTranslationsFocusChange(newFocused = id)
                            }
                        },
                        placeholder = if (isLast) "Add additional translation" else "Additional Translation (leave blank to delete)",
                        // TODO, string res
                    )
                }
            }

            editableGroup(
                title = "Examples", // TODO string res
                icon = MDIconsSet.WordExample,
            ) {
                itemsIndexed(
                    items = uiState.examples.toList().sortedBy { it.first },
                    key = { _, (id, _) -> id },
                ) { i, (id, value) ->
                    val isLast = i == uiState.additionalTranslations.count().dec()
                    MDWordFieldTextField(
                        value = value,
                        onValueChange = { newValue ->
                            uiActions.onEditExamples(id, newValue)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                        onFocusEvent = {
                            if (it.isFocused) {
                                uiActions.onExamplesFocusChange(newFocused = id)
                            }
                        },
                        placeholder = if (isLast) "Add Example" else "Example (leave blank to delete)",
                        // TODO, string rse
                    )
                }
            }

            editableGroup(
                title = "Word Class", // TODO, string res
                icon = MDIconsSet.WordRelatedWords,
            ) {
                item {
                    MDBasicDropDownMenu(
                        value = uiState.selectedTypeTag,
                        suggestions = availableTypeTags,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = "Select type tag", // TODO, string res
                        fieldReadOnly = true,
                        onValueChange = {},
                        suggestionTitle = {
                            this.name
                        },
                        onSelectSuggestion = { i, it ->
                            uiActions.onEditTypeTag(it)
                        },
                    )
                }
                itemsIndexed(
                    items = uiState.relatedWords.toList().sortedBy { it.first },
                    key = { _, (id, _) -> id },
                ) { i, (id, value) ->
                    val isLast = i == uiState.additionalTranslations.count().dec()
                    val (label, word) = value
                    Row(
                        modifier = modifier.animateItem(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MDBasicDropDownMenu(
                            modifier = Modifier.weight(1f),
                            value = label,
                            suggestions = uiState.selectedTypeTag?.relations ?: emptyList(),
                            fieldReadOnly = true,
                            onValueChange = {},
                            allowCancelSelection = false,
                            suggestionTitle = {
                                this.label
                            },
                            onSelectSuggestion = { i, it ->
                                it?.let { it1 ->
                                    uiActions.onEditTypeRelationLabel(id, it1)
                                }
                            },
                        )
                        MDWordFieldTextField(
                            modifier = Modifier
                                .weight(1f),
                            onFocusEvent = {
                                if (it.isFocused) {
                                    uiActions.onTypeRelationFocusChange(newFocused = id)
                                }
                            },
                            value = word,
                            onValueChange = { newValue ->
                                uiActions.onEditTypeRelationValue(id, newValue)
                            },
                            placeholder = if (isLast) "Add Type relation" else "Type relation (leave blank to delete)", // TODO, string rse
                        )
                    }
                }
            }
            uiState.lexicalRelations.entries.sortedBy {
                it.key
            }.forEach { (type, relations) ->
                editableGroup(
                    title = { type.relationName },
                    titleHint = type.strLabel,
                    icon = MDIconsSet.WordRelatedWords // TODO, icon res
                ) {
                    itemsIndexed(
                        items = relations.toList().sortedBy { it.first },
                        key = { _, (id, _) -> id }
                    ) { i, (id, relation) ->
                        val isLast = i == uiState.additionalTranslations.count().dec()
                        MDWordFieldTextField(
                            value = relation,
                            onValueChange = { newValue ->
                                uiActions.onEditLexicalRelation(type, id, newValue)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            onFocusEvent = {
                                if (it.isFocused) {
                                    uiActions.onLexicalRelationFocusChange(newFocused = id)
                                }
                            },
                            placeholder = if (isLast) "Add Lexical relation" else "Lexical relation (leave blank to delete)", // TODO, string rse
                        )
                    }
                }
            }
        }
    }
}

private fun LazyListScope.editableGroup(
    title: String,
    titleHint: String? = null,
    icon: MDIconsSet? = null,
    items: LazyListScope.() -> Unit,
) = editableGroup(
    title = { title },
    titleHint = titleHint,
    icon = icon,
    items = items
)

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.editableGroup(
    title: @Composable () -> String,
    titleHint: String? = null,
    icon: MDIconsSet? = null,
    items: LazyListScope.() -> Unit,
) {
    stickyHeader {
        MDTitleWithHint(title = title(), icon = icon, titleHint = titleHint)
    }
    items()
    item {
        HorizontalDivider(modifier = Modifier.padding(end = 24.dp))
    }
}

@Preview
@Composable
private fun MDWordDetailsEditModeScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDWordDetailsEditModeScreen(
                    uiState = MDWordDetailsEditModeMutableUiState(
                        tags = remember {
                            mutableStateListOf()
                        },
                        availableTypeTags = MutableStateFlow(emptyList())
                    ).apply {
                        onExecute { true }
                    },
                    uiActions = MDWordDetailsEditModeUiActions(
                        object : MDWordDetailsEditModeNavigationUiActions {
                            override fun onNavigateToViewMode(newWordId: Long, language: LanguageCode) {}
                            override fun onOpenNavDrawer() {}
                        },
                        object : MDWordDetailsEditModeBusinessUiActions {
                            override fun onSave() {}
                            override fun onEditMeaning(newMeaning: String) {}
                            override fun onEditTranslation(newTranslation: String) {}
                            override fun onEditTranscription(newTranscription: String) {}
                            override fun onEditAdditionalTranslations(id: Long, newValue: String) {}
                            override fun onEditExamples(id: Long, newValue: String) {}
                            override fun onEditTypeTag(newTypeTag: WordTypeTag?) {}
                            override fun onEditTypeRelationLabel(id: Long, relation: WordTypeTagRelation) {}
                            override fun onEditTypeRelationValue(id: Long, newValue: String) {}
                            override fun onEditLexicalRelation(type: WordLexicalRelationType, id: Long, newValue: String) {}
                            override fun onAdditionalTranslationsFocusChange(newFocused: Long) {}
                            override fun onExamplesFocusChange(newFocused: Long) {}
                            override fun onTypeRelationFocusChange(newFocused: Long) {}
                            override fun onLexicalRelationFocusChange(newFocused: Long) {}
                            override fun onFocusChange(newFocused: Long) {}
                        },
                    ),
                    contextTagsState = MDContextTagsSelectorMutableUiState(),
                    contextTagsActions = MDContextTagsSelectorUiActions(navigationActions = object : MDContextTagsSelectorNavigationUiActions {},
                        businessActions = object : MDContextTagsSelectorBusinessUiActions {
                            override fun onClickTag(tag: ContextTag) {}

                            override fun onSelectTag(tag: ContextTag) {}

                            override fun onSelectCurrentTag() {}

                            override fun onUnSelectTag(tag: ContextTag) {}

                            override fun onSetInitialSelectedTags(tags: Collection<ContextTag>) {}

                            override fun clearSelectedTags() {}

                            override fun onAddNewContextTag(tag: ContextTag) {}

                            override fun onAddNewContextTag(segment: String) {}

                            override fun onDeleteContextTag(tag: ContextTag) {}

                            override fun onNavigateUp() {}

                            override fun onResetToRoot() {}

                            override fun onSetAllowedTagsFilter(filter: (ContextTag) -> Boolean) {}

                            override fun onResetAllowedTagsFilter() {}
                            override fun onSetForbiddenTagsFilter(filter: (ContextTag) -> Boolean) {}
                            override fun onResetForbiddenTagsFilter() {}
                            override fun onResetTagsFilter() {}
                            override fun onSearchQueryChange(query: String) {}
                            override fun refreshCurrentTree() {}
                        }),
                )
            }
        }
    }
}
