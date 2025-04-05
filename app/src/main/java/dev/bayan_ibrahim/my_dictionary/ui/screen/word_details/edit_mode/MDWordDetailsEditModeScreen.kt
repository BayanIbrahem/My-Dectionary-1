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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDropDownMenu
import dev.bayan_ibrahim.my_dictionary.core.design_system.ContentWithHint
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.core.ui.MDWordFieldTextField
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorBusinessUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorMutableUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.tagsSelector
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode.component.MDWordDetailsEditModeTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun MDWordDetailsEditModeScreen(
    uiState: MDWordDetailsEditModeUiState,
    uiActions: MDWordDetailsEditModeUiActions,
    tagsState: MDTagsSelectorUiState,
    tagsActions: MDTagsSelectorUiActions,
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
        val availableWordsClasses by uiState.availableWordsClasses.collectAsStateWithLifecycle()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(spacedBy)
        ) {
            editableGroup(
                title = { firstCapStringResource(R.string.basic) },
                icon = MDIconsSet.WordMeaning, // TODO, icon res
            ) {
                item {
                    MDWordFieldTextField(
                        value = uiState.meaning,
                        onValueChange = uiActions::onEditMeaning,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = firstCapStringResource(R.string.meaning_hint),
                        label = firstCapStringResource(R.string.meaning),
                        leadingIcon = MDIconsSet.WordMeaning,
                    )
                }
                item {
                    MDWordFieldTextField(
                        value = uiState.translation,
                        onValueChange = uiActions::onEditTranslation,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = firstCapStringResource(R.string.translation_hint),
                        label = firstCapStringResource(R.string.translation),
                        leadingIcon = MDIconsSet.WordTranslation,
                    )
                }

                item {
                    MDWordFieldTextField(
                        value = uiState.note,
                        onValueChange = uiActions::onEditNote,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = firstCapStringResource(R.string.note_hint),
                        label = firstCapStringResource(R.string.note),
                        leadingIcon = MDIconsSet.WordExample,  // TODO, icon res
                        maxLines = 3,
                    )
                }
            }

            editableGroup(
                title = { firstCapStringResource(R.string.phonetic) },
                icon = MDIconsSet.WordTranscription,
            ) {
                item {
                    MDWordFieldTextField(
                        value = uiState.transcription,
                        onValueChange = uiActions::onEditTranscription,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = firstCapStringResource(R.string.transcription_hint),
                        label = firstCapStringResource(R.string.transcription),
                        leadingIcon = MDIconsSet.WordTranscription,
                    )
                }
            }

            editableGroup(
                title = { firstCapStringResource(R.string.tags) },
                icon = MDIconsSet.WordTag,
            ) {
                tagsSelector(
                    state = tagsState,
                    actions = tagsActions,
                    spacedBy = spacedBy,
                    showTitle = false,
                    showHorizontalDivider = false,
                )
            }

            editableGroup(
                title = { firstCapStringResource(R.string.additional_translations) },
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
                        placeholder = if (isLast) {
                            firstCapStringResource(R.string.add_x, firstCapStringResource(R.string.additional_translation))
                        } else {
                            "${firstCapStringResource(R.string.additional_translation)} (${stringResource(R.string.leave_blank_to_delete)})"
                        },
                    )
                }
            }

            editableGroup(
                title = { firstCapStringResource(R.string.examples) },
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
                        placeholder = if (isLast) {
                            firstCapStringResource(R.string.add_x, firstCapStringResource(R.string.example))
                        } else {
                            "${firstCapStringResource(R.string.example)} (${stringResource(R.string.leave_blank_to_delete)})"
                        },
                    )
                }
            }

            editableGroup(
                title = { firstCapStringResource(R.string.word_class) },
                icon = MDIconsSet.WordRelatedWords,
            ) {
                item {
                    MDBasicDropDownMenu(
                        value = uiState.selectedWordClass,
                        suggestions = availableWordsClasses,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = firstCapStringResource(R.string.select_x, firstCapStringResource(R.string.word_class)),
                        fieldReadOnly = true,
                        onValueChange = {},
                        suggestionTitle = {
                            this.name
                        },
                        onSelectSuggestion = { i, it ->
                            uiActions.onEditWordClass(it)
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
                            suggestions = uiState.selectedWordClass?.relations ?: emptyList(),
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
                            placeholder = if (isLast) {
                                firstCapStringResource(R.string.add_x, firstCapStringResource(R.string.word_class_relation))
                            } else {
                                "${firstCapStringResource(R.string.word_class_relation)} (${stringResource(R.string.leave_blank_to_delete)})"
                            },
                        )
                    }
                }
            }
            uiState.lexicalRelations.entries.sortedBy {
                it.key
            }.forEach { (type, relations) ->
                editableGroup(
                    title = { type.label },
                    titleHint = {
                        type.hintLikeExample
                    },
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
                            placeholder = if (isLast) {
                                firstCapStringResource(R.string.add_x, firstCapStringResource(R.string.lexical_relation))
                            } else {
                                "${firstCapStringResource(R.string.lexical_relation)} (${stringResource(R.string.leave_blank_to_delete)})"
                            },
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
    titleHint = { titleHint },
    icon = icon,
    items = items
)

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.editableGroup(
    title: @Composable () -> String,
    titleHint: @Composable () -> String? = { null },
    icon: MDIconsSet? = null,
    items: LazyListScope.() -> Unit,
) {
    stickyHeader {
        ContentWithHint(title = title(), icon = icon, titleHint = titleHint())
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
                        availableWordsClasses = MutableStateFlow(emptyList())
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
                            override fun onEditNote(newNote: String) {}
                            override fun onEditTranscription(newTranscription: String) {}
                            override fun onEditAdditionalTranslations(id: Long, newValue: String) {}
                            override fun onEditExamples(id: Long, newValue: String) {}
                            override fun onEditWordClass(newWordClass: WordClass?) {}
                            override fun onEditTypeRelationLabel(id: Long, relation: WordClassRelation) {}
                            override fun onEditTypeRelationValue(id: Long, newValue: String) {}
                            override fun onEditLexicalRelation(type: WordLexicalRelationType, id: Long, newValue: String) {}
                            override fun onAdditionalTranslationsFocusChange(newFocused: Long) {}
                            override fun onExamplesFocusChange(newFocused: Long) {}
                            override fun onTypeRelationFocusChange(newFocused: Long) {}
                            override fun onLexicalRelationFocusChange(newFocused: Long) {}
                            override fun onFocusChange(newFocused: Long) {}
                            override fun onUpdateSelectedTags(selectedTags: List<Tag>) {}
                        },
                    ),
                    tagsState = MDTagsSelectorMutableUiState(),
                    tagsActions = MDTagsSelectorUiActions(navigationActions = object : MDTagsSelectorNavigationUiActions {},
                        businessActions = object : MDTagsSelectorBusinessUiActions {
                            override fun onClickTag(tag: Tag) {}

                            override fun onSelectTag(tag: Tag) {}

                            override fun onSelectCurrentTag() {}

                            override fun onUnSelectTag(tag: Tag) {}

                            override fun onSetInitialSelectedTags(tags: Collection<Tag>) {}

                            override fun clearSelectedTags() {}

                            override fun onAddNewTag(tag: Tag) {}

                            override fun onAddNewTag(segment: String) {}

                            override fun onDeleteTag(tag: Tag) {}

                            override fun onNavigateUp() {}

                            override fun onResetToRoot() {}

                            override fun onSetAllowedTagsFilter(filter: (Tag) -> Boolean) {}

                            override fun onResetAllowedTagsFilter() {}
                            override fun onSetForbiddenTagsFilter(filter: (Tag) -> Boolean) {}
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
