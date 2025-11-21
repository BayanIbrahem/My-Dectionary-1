package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDropDownMenu
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.overline.MDCard2Overline
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.core.ui.MDWordFieldTextField
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode.component.MDWordDetailsEditModeTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun MDWordDetailsEditModeScreen(
    uiState: MDWordDetailsEditModeUiState,
    uiActions: MDWordDetailsEditModeUiActions,
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
                overline = {
                    MDCard2Overline(
                        title = firstCapStringResource(R.string.basic),
                        leading = {
                            MDIcon(MDIconsSet.WordMeaning)

                        },
                        modifier = Modifier.headerGradientBackground(),
                    )
                },
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
                overline = {
                    MDCard2Overline(
                        title = firstCapStringResource(R.string.phonetic),
                        leading = {
                            MDIcon(MDIconsSet.WordTranscription)
                        },
                        modifier = Modifier.headerGradientBackground(),
                    )
                },
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
                overline = {
                    MDCard2Overline(
                        title = firstCapStringResource(R.string.tags),
                        leading = {
                            MDIcon(MDIconsSet.WordTag)

                        },
                        modifier = Modifier.headerGradientBackground(),
                    )
                },
            ) {
                items(uiState.tags) { tag ->
                    MDCard2ListItem(
                        title = tag.label,
                        trailingIcon = { MDIcon(MDIconsSet.Close) },
                        onTrailingClick = {
                            uiActions.onRemoveTag(tag)
                        }
                    )
                }
                item {
                    var showDialog by remember {
                        mutableStateOf(false)
                    }
                    MDCard2ListItem(
                        title = firstCapStringResource(R.string.add_x, stringResource(R.string.tag)),
                        onClick = {
                            showDialog = true
                        }
                    )
                    if (showDialog) {
                        MDTagsSelectorRoute(
                            isDialog = false,
                            isSelectEnabled = true,
                            selectedTagsMaxSize = 1,
                            isAddEnabled = false,
                            isEditEnabled = false,
                            isDeleteEnabled = false,
                            isDeleteSubtreeEnabled = false,
                            onConfirmSelectedTags = {
                                it.firstOrNull()?.let { tag ->
                                    uiActions.onAddTag(tag)
                                }
                            },
                            onPopOrDismissDialog = {
                                showDialog = false
                            }
                        )
                    }
                }
            }

            editableGroup(
                overline = {
                    MDCard2Overline(
                        title = firstCapStringResource(R.string.additional_translations),
                        leading = {
                            MDIcon(MDIconsSet.WordAdditionalTranslation)

                        },
                        modifier = Modifier.headerGradientBackground(),
                    )
                },
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
                overline = {
                    MDCard2Overline(
                        title = firstCapStringResource(R.string.examples),
                        leading = {
                            MDIcon(MDIconsSet.WordExample)
                        },
                        modifier = Modifier.headerGradientBackground(),
                    )
                },
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
                overline = {
                    MDCard2Overline(
                        title = firstCapStringResource(R.string.word_class),
                        leading = {
                            MDIcon(MDIconsSet.WordRelatedWords)
                        },
                        modifier = Modifier.headerGradientBackground(),
                    )
                },
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
                    overline = {
                        MDCard2Overline(
                            title = type.label,
                            subtitle = type.hintLikeExample,
                            leading = {
                                MDIcon(MDIconsSet.WordRelatedWords)
                            },
                            modifier = Modifier.headerGradientBackground(),
                        )
                    },
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

@Composable
private fun Modifier.headerGradientBackground(): Modifier = background(
    Brush.verticalGradient(
        Pair(0.5f, MaterialTheme.colorScheme.surface),
        Pair(1f, Color.Transparent),
    )
)

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.editableGroup(
    overline: @Composable () -> Unit,
    items: LazyListScope.() -> Unit,
) {
    stickyHeader {
        overline()
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
                            override fun onAddTag(tag: Tag) {}
                            override fun onRemoveTag(tag: Tag) {}
                        },
                    ),
                )
            }
        }
    }
}
