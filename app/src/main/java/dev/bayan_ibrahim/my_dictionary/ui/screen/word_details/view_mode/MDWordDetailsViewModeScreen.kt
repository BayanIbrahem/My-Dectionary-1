package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.view_mode


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningViewNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.MDDateTimeFormat
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.format
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.toDefaultLocalDateTime
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapPluralsResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.ContentWithHint
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.overline.MDCard2Overline
import dev.bayan_ibrahim.my_dictionary.core.ui.IconSegmentedButton
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordDetailsDirectionSource
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import dev.bayan_ibrahim.my_dictionary.domain.model.word.invalid
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.component.MDTagColorIcon
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.view_mode.component.MDWordDetailsViewModeTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDWordDetailsViewModeScreen(
    uiState: MDWordDetailsViewModeUiState,
    uiActions: MDWordDetailsViewModeUiActions,
    wordAlignmentSource: WordDetailsDirectionSource,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    val deviceDirection = LocalLayoutDirection.current

    val direction by remember(
        key1 = wordAlignmentSource,
        key2 = uiState.word.language.direction,
        key3 = deviceDirection
    ) {
        derivedStateOf {
            when (wordAlignmentSource) {
                WordDetailsDirectionSource.Ltr -> LayoutDirection.Ltr
                WordDetailsDirectionSource.Rtl -> LayoutDirection.Rtl
                WordDetailsDirectionSource.Device -> deviceDirection
                WordDetailsDirectionSource.WordLanguage -> uiState.word.language.direction
            }
        }
    }
    CompositionLocalProvider(
        LocalLayoutDirection provides direction,
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
                    IconSegmentedButton(
                        horizontalAlignment = Alignment.End,

                        selected = wordAlignmentSource,
                        allItems = WordDetailsDirectionSource.entries,
                        onSelectItem = uiActions::onToggleWordDetailsAlignmentSource
                    )
                }
                item {
                    WordInfoGroup(
                        title = firstCapStringResource(R.string.basic),
                        icon = MDIconsSet.WordMeaning, // TODO, icon res

                    ) {
                        WordPropertyItem(
                            label = firstCapStringResource(R.string.meaning),
                            value = uiState.word.meaning,
                        )
                        WordPropertyItem(
                            label = firstCapStringResource(R.string.translation),
                            value = uiState.word.translation,
                        )
                        WordPropertyItem(
                            label = firstCapStringResource(R.string.language),
                            value = uiState.word.language.fullDisplayName,
                        )
                        if (uiState.word.note.isNotEmpty())
                            WordPropertyItem(
                                label = firstCapStringResource(R.string.note),
                                value = uiState.word.note,
                            )
                    }
                }
                item {
                    WordInfoGroup(
                        title = firstCapStringResource(R.string.phonetic),
                        icon = MDIconsSet.WordTranscription,
                    ) {
                        WordPropertyItem(
                            label = firstCapStringResource(R.string.transcription),
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
                            title = firstCapStringResource(R.string.tags),
                            icon = MDIconsSet.WordTag,
                        ) {
                            uiState.word.tags.forEach { tag ->
                                WordPropertyItem(
                                    value = tag.value,
                                    trailingIcon = tag.color?.let { color ->
                                        {
                                            MDTagColorIcon(
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
                            title = firstCapStringResource(R.string.additional_translations),
                            icon = MDIconsSet.WordAdditionalTranslation
                        ) {
                            uiState.word.additionalTranslations.forEach { translation ->
                                WordPropertyItem(value = translation)
                            }
                        }
                    }
                }
                item {
                    if (uiState.word.examples.isNotEmpty()) {
                        WordInfoGroup(
                            title = firstCapStringResource(R.string.examples),
                            icon = MDIconsSet.WordExample,
                        ) {
                            uiState.word.examples.forEach { example ->
                                WordPropertyItem(value = example)
                            }
                        }
                    }
                }

                item {
                    uiState.word.wordClass?.let { wordClass ->
                        WordInfoGroup(
                            title = "${firstCapStringResource(R.string.word_class)} ${wordClass.name.meaningViewNormalize}",
                            icon = MDIconsSet.WordRelatedWords,
                        ) {
                            if (uiState.word.relatedWords.isEmpty()) {
                                WordPropertyItem(
                                    label = firstCapPluralsResource(R.plurals.relation, 0),
                                    value = ""
                                )
                            } else {
                                uiState.word.relatedWords.forEach { relation ->
                                    WordPropertyItem(
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
                                title = type.label,
                                titleHint = type.hintLikeExample,
                                icon = MDIconsSet.WordRelatedWords
                            ) {
                                relations.forEach { relation ->
                                    WordPropertyItem(
                                        value = relation.relatedWord,
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    WordInfoGroup(
                        title = firstCapStringResource(R.string.creation),
                        icon = MDIconsSet.CreateTime
                    ) {
                        WordPropertyItem(
                            label = firstCapStringResource(R.string.created_at),
                            value = uiState.word.createdAt.toDefaultLocalDateTime().format(MDDateTimeFormat.EuropeanDateTime)
                        )
                        WordPropertyItem(
                            label = firstCapStringResource(R.string.updated_at),
                            value = uiState.word.updatedAt.toDefaultLocalDateTime().format(MDDateTimeFormat.EuropeanDateTime)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun WordPropertyItem(
    modifier: Modifier = Modifier,
    value: String = "",
    leadingIcon: MDIconsSet? = null,
    label: String? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    MDCard2ListItem(
        modifier = modifier.requiredHeightIn(42.dp, Dp.Unspecified),
        leading = leadingIcon?.let {
            {
                MDIcon(leadingIcon)
            }
        },
        trailing = trailingIcon
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun WordInfoGroup(
    title: String,
    modifier: Modifier = Modifier,
    titleHint: String? = null,
    icon: MDIconsSet? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    MDCard2(
        overline = {

            ContentWithHint(
                hint = titleHint,
            ) {
                MDCard2Overline(
                    leading = if (icon != null) {
                        {
                            MDIcon(icon)
                        }
                    } else null,
                    title = title,
                )
            }
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
                                    Tag(value = "object/word/tag1", color = Color.Red),
                                    Tag(value = "object/word/tag2", color = Color.Blue),
                                    Tag(value = "object/word/tag3"),
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
                        object : MDWordDetailsViewModeBusinessUiActions {
                            override fun onToggleWordDetailsAlignmentSource(source: WordDetailsDirectionSource?) {}
                        },
                    ),
                    wordAlignmentSource = WordDetailsDirectionSource.WordLanguage
                )
            }
        }
    }
}
