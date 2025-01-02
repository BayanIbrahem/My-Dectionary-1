package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.MDNormalizer
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningSearchNormalizer
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.searchQueryRegexNormalizer
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardColors
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDVerticalCard
import dev.bayan_ibrahim.my_dictionary.core.design_system.toAnnotatedString
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_INSTANT
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.lerp

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun MDWordListItem(
    word: Word,
    /**
     * first is the search query applied for meaning,
     * second is the search query applied for translation
     */
    searchQuery: Pair<String?, String?>,
    modifier: Modifier = Modifier,
    colors: MDCardColors = MDCardDefaults.colors(),
    overrideHeaderColorsFromTags: Boolean = true,
    expanded: Boolean = true,
    animationDuration: Int = 300,
    animationEasing: Easing = FastOutSlowInEasing,
    headerClickable: Boolean = true,
    cardClickable: Boolean = true,
    onClickHeader: () -> Unit = {},
    onLongClickHeader: () -> Unit = {},
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    primaryAction: @Composable RowScope.() -> Unit = {},
    secondaryAction: @Composable RowScope.() -> Unit = {},
) {
    val intSizeTween by remember(animationEasing, animationDuration) {
        derivedStateOf { tween<IntSize>(animationDuration, easing = animationEasing) }
    }
    val floatTween by remember(animationEasing, animationDuration) {
        derivedStateOf { tween<Float>(animationDuration, easing = animationEasing) }
    }

    val mayNotMatchMeaningOrTranslation: Boolean by remember(searchQuery) {
        derivedStateOf {
            searchQuery.first != null && searchQuery.second != null
        }
    }
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val updatedColors by remember(colors, overrideHeaderColorsFromTags, surfaceColor, onSurfaceColor) {
        derivedStateOf {
            if (overrideHeaderColorsFromTags) {
                word.tags.firstNotNullOfOrNull { it.color }?.let { seed ->
                    colors.copy(
                        headerContainerColor = seed.lerp(surfaceColor),
                        headerContentColor = seed.lerp(onSurfaceColor),
                    )
                } ?: colors
            } else {
                colors
            }
        }
    }
    MDVerticalCard(
        modifier = modifier,
        headerClickable = headerClickable,
        colors = updatedColors,
        cardClickable = cardClickable,
        onClickHeader = onClickHeader,
        onLongClickHeader = onLongClickHeader,
        onClick = onClick,
        onLongClick = onLongClick,
        header = {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Text(
                    text = word.language.uppercaseCode,
                    style = if (word.language.isLongCode) {
                        MaterialTheme.typography.titleSmall
                    } else {
                        MaterialTheme.typography.titleLarge
                    },
                )
                Text(
                    text = buildAnnotatedString {
                        val meaning = word.meaning.formatBySearchQuery(
                            searchQuery = searchQuery.first,
                            style = MaterialTheme.typography.bodyLarge.toSpanStyle(),
                            mayNotMatch = mayNotMatchMeaningOrTranslation,
                            textColor = colors.headerContentColor,
                            backgroundColor = colors.headerContainerColor,
                        )
                        append(meaning)
                        pushStyle(MaterialTheme.typography.labelSmall.toSpanStyle())
                        append("  " + word.transcription)
                    },
                )
                Spacer(modifier = Modifier.weight(1f))
                primaryAction()
                secondaryAction()
            }
        },
        footerModifier = Modifier
            .fillMaxWidth()
            .padding(MDCardDefaults.footerPaddingValues),
        footer = {
            if (word.tags.isNotEmpty()) {
                Text(
                    modifier = Modifier.align(Alignment.BottomStart),
                    text = word.tags.random().value,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = word.wordTypeTag?.name ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            val enterTransaction by remember(floatTween, intSizeTween) {
                derivedStateOf {
                    fadeIn(animationSpec = floatTween) + expandVertically(animationSpec = intSizeTween)
                }
            }

            val exitTransaction by remember(floatTween, intSizeTween) {
                derivedStateOf {
                    fadeOut(animationSpec = floatTween) + shrinkVertically(animationSpec = intSizeTween)
                }
            }
            FlowRow {
                Text(
                    text = word.translation.formatBySearchQuery(
                        searchQuery = searchQuery.second,
                        style = MaterialTheme.typography.bodyMedium.toSpanStyle().copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        mayNotMatch = mayNotMatchMeaningOrTranslation,
                        textColor = colors.contentContentColor,
                        backgroundColor = colors.contentContainerColor,
                    ),
                )
                word.additionalTranslations.forEach { additionalTranslation ->
                    AnimatedVisibility(
                        visible = expanded,
                        enter = enterTransaction,
                        exit = exitTransaction,
                    ) {
                        Text(
                            text = ", $additionalTranslation",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = enterTransaction,
                exit = exitTransaction,
            ) {
                Column(
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text(
                        modifier = Modifier.basicMarquee(),
                        text = if (word.examples.isEmpty()) "No Examples yet" else "Examples", // TODO, string res
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    word.examples.forEach { example ->
                        Text(
                            modifier = Modifier.basicMarquee(),
                            text = example,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

private fun String.formatBySearchQuery(
    searchQuery: String?,
    style: SpanStyle,
    textColor: Color,
    backgroundColor: Color,
    highlightedTextColor: Color = backgroundColor,
    highlightBackgroundColor: Color = textColor,
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
        color = textColor,
        background = backgroundColor
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
                        language = Language("de", "Deutsch", "German"),
                        tags = setOf(
                            ContextTag(value = "Human body"),
                            ContextTag(value = "Organic")
                        ),
                        transcription = "auge",
                        examples = listOf("I habe zwei auge", "some other example"),
                        createdAt = INVALID_INSTANT,
                        updatedAt = INVALID_INSTANT,
                        wordTypeTag = WordTypeTag(
                            id = 0,
                            name = "name",
                            language = Language("de", "Deutsch", "German"),
                            relations = listOf(WordTypeTagRelation("relation 1"), WordTypeTagRelation("relation 2")),
                            wordsCount = 30,
                        )
                    ),
                    expanded = expanded,
                    primaryAction = {
                        IconButton(onClick = {
                            expanded = !expanded
                        }) {
                            MDIcon(MDIconsSet.Close)
                        }
                    },
                    secondaryAction = {
                        Checkbox(checked = false, onCheckedChange = null)
                    }
                )
            }
        }
    }
}