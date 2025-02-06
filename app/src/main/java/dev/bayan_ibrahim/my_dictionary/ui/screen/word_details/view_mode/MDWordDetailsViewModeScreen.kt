package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.view_mode


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.MDDateTimeFormat
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.format
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.toDefaultLocalDateTime
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTitleWithHint
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardScope
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.item
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import dev.bayan_ibrahim.my_dictionary.domain.model.word.invalid
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.component.MDContextTagColorIcon
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.view_mode.component.MDWordDetailsViewModeTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDWordDetailsViewModeScreen(
    uiState: MDWordDetailsViewModeUiState,
    uiActions: MDWordDetailsViewModeUiActions,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    CompositionLocalProvider(
        LocalLayoutDirection provides uiState.word.language.direction,
    ) {
        MDScreen(
            uiState = uiState,
            modifier = modifier,
            topBar = {
                MDWordDetailsViewModeTopAppBar(
                    language = uiState.word.language,
                    onShare = uiActions::onShare,
                    onNavigateUp = uiActions::onPop,
                    onClickWordStatistics = uiActions::onClickWordStatistics,
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = lazyListState.lastScrolledBackward || !lazyListState.canScrollBackward,
                    enter = fadeIn() + expandIn(),
                    exit = fadeOut() + shrinkOut(),

                ) {
                    FloatingActionButton(uiActions::onEdit) {
                        MDIcon(MDIconsSet.Edit)
                    }
                }
            }
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = lazyListState
            ) {
                item {
                    WordInfoGroup(
                        title = "Basic", // TODO, string res
                        icon = MDIconsSet.WordMeaning, // TODO, icon res

                    ) {
                        wordPropertyItem(
                            label = "Meaning",/* TODO, string res */
                            value = uiState.word.meaning,
                        )
                        wordPropertyItem(
                            label = "Translation",/* TODO, string res */
                            value = uiState.word.translation,
                        )
                        wordPropertyItem(
                            label = "Language",/* TODO, string res */
                            value = uiState.word.language.fullDisplayName,
                        )
                    }
                }
                item {
                    WordInfoGroup(
                        title = "Phonetic", // TODO, string res
                        icon = MDIconsSet.WordTranscription,
                    ) {
                        wordPropertyItem(
                            label = "Transcription",/* TODO, string res */
                            value = uiState.word.transcription.ifBlank { "-" },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        // TODO, on pronounce word
                                    }
                                ) {
                                    MDIcon(MDIconsSet.WordTranscription/* TODO, icon res */)
                                }
                            }
                        )
                    }
                }
                item {
                    if (uiState.word.tags.isNotEmpty()) {
                        WordInfoGroup(
                            title = "Context tags", // TODO, string res
                            icon = MDIconsSet.WordTag,
                        ) {
                            uiState.word.tags.forEach { tag ->
                                wordPropertyItem(
                                    value = tag.value,
                                    trailingIcon = tag.color?.let { color ->
                                        {
                                            MDContextTagColorIcon(
                                                color = color,
                                                isPassed = tag.currentColorIsPassed,
                                                canPassable = tag.passColorToChildren
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    if (uiState.word.additionalTranslations.isNotEmpty()) {
                        WordInfoGroup(
                            title = "Additional Translations", // TODO, string res
                            icon = MDIconsSet.WordAdditionalTranslation
                        ) {
                            uiState.word.additionalTranslations.forEach { example ->
                                wordPropertyItem(value = example)
                            }
                        }
                    }
                }
                item {
                    if (uiState.word.examples.isNotEmpty()) {
                        WordInfoGroup(
                            title = "Examples", // TODO, string res
                            icon = MDIconsSet.WordExample,
                        ) {
                            uiState.word.examples.forEach { example ->
                                wordPropertyItem(value = example)
                            }
                        }
                    }
                }

                item {
                    uiState.word.wordClass?.let { wordClass ->
                        WordInfoGroup(
                            title = "Word Class ${wordClass.name}", // TODO, string res
                            icon = MDIconsSet.WordRelatedWords,
                        ) {
                            if (uiState.word.relatedWords.isEmpty()) {
                                // TODO, string res
                                wordPropertyItem(label = "No Relations", value = "")
                            } else {
                                uiState.word.relatedWords.forEach { relation ->
                                    wordPropertyItem(
                                        label = relation.relationLabel,
                                        value = relation.value,
                                    )
                                }
                            }
                        }
                    }
                }
                uiState.word.lexicalRelations.forEach { (type, relations) ->
                    if (relations.isNotEmpty()) {
                        item {
                            WordInfoGroup(
                                title = type.relationName,
                                titleHint = type.strLabel,
                                icon = MDIconsSet.WordRelatedWords
                            ) {
                                relations.forEach { relation ->
                                    wordPropertyItem(
                                        value = relation.relatedWord,
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    WordInfoGroup(
                        title = "Creation",
                        icon = MDIconsSet.CreateTime
                    ) {
                        wordPropertyItem(
                            label = "Created at",
                            value = uiState.word.createdAt.toDefaultLocalDateTime().format(MDDateTimeFormat.EuropeanDateTime)
                        )
                        wordPropertyItem(
                            label = "Updated at",
                            value = uiState.word.updatedAt.toDefaultLocalDateTime().format(MDDateTimeFormat.EuropeanDateTime)
                        )
                    }
                }
            }
        }
    }
}

private fun MDHorizontalCardScope.wordPropertyItem(
    value: String = "",
    leadingIcon: MDIconsSet? = null,
    label: String? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    item(
        leadingIcon = leadingIcon?.let {
            {
                MDIcon(leadingIcon)
            }
        },
        trailingIcon = trailingIcon
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            label?.let {
                Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
            Text(text = value, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.basicMarquee(Int.MAX_VALUE))
        }
    }
}

@Composable
private fun WordInfoGroup(
    title: String,
    modifier: Modifier = Modifier,
    titleHint: String? = null,
    icon: MDIconsSet? = null,
    content: MDHorizontalCardScope.() -> Unit,
) {
    MDHorizontalCardGroup(
        title = {
            MDTitleWithHint(
                title = title,
                icon = icon,
                titleHint = titleHint,
            )
        },
        modifier = modifier,
        content = content,
    )
}


@Preview(device = "id:pixel_9_pro")
@Composable
private fun WordDetailsViewModeScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDWordDetailsViewModeScreen(
                    uiState = MDWordDetailsViewModeMutableUiState().apply {
                        onExecute {
                            word = Word.invalid().copy(
                                meaning = "Meaning",
                                translation = "Translation",
                                transcription = "Transcription",
                                tags = setOf(
                                    ContextTag(value = "object/word/tag1", color = Color.Red),
                                    ContextTag(value = "object/word/tag2", color = Color.Blue),
                                    ContextTag(value = "object/word/tag3"),
                                ),
                                examples = listOf(
                                    "example 1",
                                    "example 2",
                                    "example 3",
                                ),
                                additionalTranslations = listOf(
                                    "Additional translation 1",
                                    "Additional translation 2",
                                ),
                                wordClass = WordClass(0, "Type tag", "en".code.getLanguage(), listOf()),
                                relatedWords = listOf(
                                    RelatedWord(0, 0, 0, "Relation", "Word"),
                                    RelatedWord(0, 0, 0, "Relation", "Word"),
                                    RelatedWord(0, 0, 0, "Relation", "Word"),
                                ),
                                lexicalRelations = WordLexicalRelationType.entries.associateWith {
                                    val words = listOf("word")
                                    words.map {
                                        WordLexicalRelation.Synonym(it)
                                    }
                                }
                            )
                            true
                        }
                    },
                    uiActions = MDWordDetailsViewModeUiActions(
                        object : MDWordDetailsViewModeNavigationUiActions {
                            override fun onClickWordStatistics() {}
                            override fun onEdit() {}
                            override fun onShare() {}
                            override fun onOpenNavDrawer() {}
                        },
                        object : MDWordDetailsViewModeBusinessUiActions {},
                    )
                )
            }
        }
    }
}
