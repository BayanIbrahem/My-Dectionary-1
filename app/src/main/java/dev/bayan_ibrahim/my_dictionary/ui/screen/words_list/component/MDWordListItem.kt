package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.MDNormalizer
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningSearchNormalizer
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.searchQueryRegexNormalizer
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2Defaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.toAnnotatedString
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_INSTANT
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.DEFAULT_FRACTION

@Composable
fun MDWordListItem(
    word: Word,
    /**
     * first is the search query applied for meaning,
     * second is the search query applied for translation
     */
    searchQuery: Pair<String?, String?>,
    modifier: Modifier = Modifier,
    headerTheme: MDCard2ListItemTheme = MDCard2Defaults.defaultHeaderTheme,
    contentTheme: MDCard2ListItemTheme = MDCard2Defaults.defaultContentTheme,
    overrideHeaderColorsFromTags: Boolean = true,
    expanded: Boolean = true,
    onClickHeader: (() -> Unit)? = null,
    onLongClickHeader: (() -> Unit)? = null,
    onDoubleClickHeader: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    isSpeakInProgress: Boolean = false,
    onSpeak: (() -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onTrailingClick: (() -> Unit)? = null,
) {
    val mayNotMatchMeaningOrTranslation: Boolean by remember(searchQuery) {
        derivedStateOf {
            searchQuery.first != null && searchQuery.second != null
        }
    }
    val firstValidTagColor by remember(overrideHeaderColorsFromTags, word.tags) {
        derivedStateOf {
            if (overrideHeaderColorsFromTags) {
                word.tags.firstNotNullOfOrNull { it.color }
            } else {
                null
            }
        }
    }

    val updatedHeaderTheme = firstValidTagColor?.let {
        contentTheme.lerp(seed = it, fraction = DEFAULT_FRACTION)
    } ?: headerTheme

    MDCard2(
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        headerTheme = updatedHeaderTheme,
        contentTheme = contentTheme,
        header = {
            MDCard2ListItem(
                title = buildAnnotatedString {
                    val meaning = word.meaning.formatBySearchQuery(
                        searchQuery = searchQuery.first,
                        style = MaterialTheme.typography.bodyLarge.toSpanStyle(),
                        mayNotMatch = mayNotMatchMeaningOrTranslation,
                        contentColor = updatedHeaderTheme.titleColor,
                        containerColor = updatedHeaderTheme.containerColor,
                    )
                    append(meaning)
                },
                subtitle = word.transcription.nullIfInvalid()?.let {
                    buildAnnotatedString {
                        append(it)
                    }
                },
                onClick = onClickHeader,
                onLongClick = onLongClickHeader,
                onDoubleClick = onDoubleClickHeader,
                onLeadingClick = onSpeak,
                leadingIcon = {
                    AnimatedContent(
                        targetState = isSpeakInProgress,
                    ) {
                        if (it) {
                            CircularProgressIndicator()
                        } else {
                            MDIcon(MDIconsSet.WordTranscription) // TODO, icon res
                        }
                    }
                },
                trailingIcon = trailingIcon,
                onTrailingClick = onTrailingClick,
            )
        }
    ) {
        // Translation
        MDCard2ListItem(
            title = word.translation,
            subtitle = word.wordClass?.name,
            leadingIcon = {
                MDIcon(MDIconsSet.WordMeaning)
            },
        )
        AnimatedContent(expanded) { expanded ->
            CompositionLocalProvider(
                LocalTextStyle provides contentTheme.subtitleStyle,
                LocalContentColor provides contentTheme.subtitleColor,
            ) {
                if (expanded) {
                    val source by remember(word) {
                        derivedStateOf {
                            if (word.note.isNotBlank()) {
                                listOf(word.note)
                            } else if (word.examples.isNotEmpty()) {
                                word.examples
                            } else if (word.additionalTranslations.isNotEmpty()) {
                                word.additionalTranslations
                            } else if (word.tags.isNotEmpty()) {
                                word.tags.map { it.value }
                            } else if (word.wordClass != null) {
                                listOf(word.wordClass.name) + word.relatedWords.map {
                                    buildString {
                                        append(it.relationLabel)
                                        append(": ")
                                        append(it.value)
                                    }
                                }
                            } else {
                                emptyList()
                            }
                        }
                    }
                    val itemModifier by remember(source.count()) {
                        derivedStateOf {
                            if (source.count() == 1) {
                                Modifier
                            } else {
                                Modifier.basicMarquee(Int.MAX_VALUE, animationMode = MarqueeAnimationMode.WhileFocused)
                            }
                        }
                    }
                    val itemMaxLines by remember(source.count()) {
                        derivedStateOf {
                            if (source.count() == 1) {
                                3
                            } else {
                                1
                            }
                        }
                    }
                    Column {
                        source.forEach {
                            Text(
                                text = it,
                                maxLines = itemMaxLines,
                                modifier = itemModifier,
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun String.formatBySearchQuery(
    searchQuery: String?,
    style: SpanStyle,
    contentColor: Color,
    containerColor: Color,
    highlightedTextColor: Color = containerColor,
    highlightBackgroundColor: Color = contentColor,
    textNormalizer: MDNormalizer = meaningSearchNormalizer,
    queryNormalizer: MDNormalizer = searchQueryRegexNormalizer,
    mayNotMatch: Boolean = false,
): AnnotatedString {
    if (mayNotMatch) {
        if (searchQuery != null) {
            val normalized = textNormalizer.normalize(this)
            val query = queryNormalizer.normalize(searchQuery).toRegex()
            if (!query.matches(normalized)) {
                return this.toAnnotatedString()
            }
        }
    }

    val normalizedSearchRegex = searchQuery?.let {
        queryNormalizer.normalize(it).toRegex()
    } ?: return toAnnotatedString()

    val reversedNormalizedSearchRegex = "[^(${normalizedSearchRegex.pattern})]".toRegex()

    val nonMatchedCharsIndexes = textNormalizer.normalize(this).let {
        reversedNormalizedSearchRegex.findAll(it).map {
            it.range.toList()
        }.flatten().toSet()
    }
    val normalTextStyle = style.copy(
        color = contentColor,
        background = containerColor
    )

    val highlightedTextStyle = style.copy(
        color = highlightedTextColor,
        background = highlightBackgroundColor,
    )

    val len = length
    var lastIsNormal: Boolean? = null

    return buildAnnotatedString {
        repeat(len) { i ->
//            val textWithoutI = removeRange(i, i + 1)
//            val normalizedTextWithoutI = textNormalizer.normalize(textWithoutI)
//            val validWithoutI = normalizedSearchRegex.matches(normalizedTextWithoutI)
//            if (validWithoutI) {
            if (i in nonMatchedCharsIndexes) {
                // this is normal char
                if (lastIsNormal != true) {
                    pushStyle(normalTextStyle)
                    lastIsNormal = true
                }
            } else {
                // this is highlighted char
                if (lastIsNormal != false) {
                    pushStyle(highlightedTextStyle)
                    lastIsNormal = false
                }
            }
            append(this@formatBySearchQuery[i])
        }
    }
}

@Preview
@Composable
private fun MDWordListItemPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .width(275.dp)
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                var expanded by remember {
                    mutableStateOf(false)
                }
                MDWordListItem(
                    searchQuery = "ugug" to "y",
                    word = Word(
                        id = 0,
                        meaning = "Auge",
                        translation = "Eye",
                        additionalTranslations = listOf("Human Eye", "Human Eye 2"),
                        note = "THis is some long note for the word THis is some long note for the word THis is some long note for the word THis is some long note for the word THis is some long note for the word ",
                        language = Language("de", "Deutsch", "German"),
                        tags = setOf(
                            Tag(value = "Human body"),
                            Tag(value = "Organic")
                        ),
                        transcription = "auge",
                        examples = listOf("I habe zwei auge", "some other example"),
                        createdAt = INVALID_INSTANT,
                        updatedAt = INVALID_INSTANT,
                        wordClass = WordClass(
                            id = 0,
                            name = "name",
                            language = Language("de", "Deutsch", "German"),
                            relations = listOf(WordClassRelation("relation 1"), WordClassRelation("relation 2")),
                            wordsCount = 30,
                        )
                    ),
                    expanded = expanded,
                    trailingIcon = {
                        IconButton(onClick = {
                            expanded = !expanded
                        }) {
                            MDIcon(MDIconsSet.Close)
                        }
                    },
                )
            }
        }
    }
}