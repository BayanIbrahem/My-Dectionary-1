package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.safeSubList
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDCard
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.domain.model.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MDWordListItem(
    word: Word,
    modifier: Modifier = Modifier,
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
    val additionalTranslationAlpha by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = floatTween,
        label = "alpha"
    )
    MDCard(
        modifier = modifier,
        headerClickable = headerClickable,
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
                    text = word.language.code.uppercase(),
                    style = if (word.language.isLongCode) {
                        MaterialTheme.typography.titleSmall
                    } else {
                        MaterialTheme.typography.titleLarge
                    },
                )
                Text(
                    text = buildAnnotatedString {
                        pushStyle(MaterialTheme.typography.bodyLarge.toSpanStyle())
                        append(word.meaning)
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
            Text(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = "${word.wordTypeTag?.name?.plus(", ") ?: ""}progress ${word.learningProgress.times(100).roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                text = word.tags.safeSubList(0, 3).joinToString(", #", "#"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Row {
                Text(
                    text = word.translation,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier.graphicsLayer {
                        alpha = additionalTranslationAlpha
                    },
                    text = word.additionalTranslations.joinToString(
                        separator = ", ",
                        prefix = if (word.additionalTranslations.isNotEmpty()) ", " else ""
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
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
                        text = "Examples", // TODO, string res
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
                    word = Word(
                        id = 0,
                        meaning = "Auge",
                        translation = "Eye",
                        additionalTranslations = listOf("Human Eye", "Human Eye 2"),
                        language = Language("de", "Deutsch", "German"),
                        tags = listOf("Human body", "Organic"),
                        transcription = "auge",
                        examples = listOf("I habe zwei auge", "some other example"),
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
                            Icon(Icons.Default.Build, null)
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