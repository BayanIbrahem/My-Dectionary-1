package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDLanguageWordSpaceListItem(
    wordSpace: LanguageWordSpace,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primary,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    hideWordCountAndProgress: Boolean = false,
    cornerRadius: Dp = 8.dp,
) {
    val animatedContainerColor by animateColorAsState(
        targetValue = if (isSelected) selectedContainerColor else containerColor,
        label = "container color"
    )
    val animatedContentColor by animateColorAsState(
        targetValue = if (isSelected) selectedContentColor else contentColor,
        label = "content color"
    )
    val animatedContentVariantColor by animateColorAsState(
        targetValue = (if (isSelected) selectedContentColor else contentColor).copy(alpha = 0.5f),
        label = "content variant color"
    )
    Row(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                drawRoundRect(
                    color = animatedContainerColor,
                )
            }
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
            Text(
                text = wordSpace.uppercaseCode,
                style = if (wordSpace.isLongCode) {
                    MaterialTheme.typography.titleSmall
                } else {
                    MaterialTheme.typography.titleLarge
                },
                color = animatedContentColor
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = if (hideWordCountAndProgress) wordSpace.selfDisplayName else wordSpace.fullDisplayName,
                style = MaterialTheme.typography.bodyLarge,
                color = animatedContentColor
            )
            Row {
                Text(
                    text = if (hideWordCountAndProgress) wordSpace.localDisplayName else "${wordSpace.wordsCount} words", // TODO, string res
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    color = animatedContentVariantColor,
                )
            }
        }
    }
}

@Preview
@Composable
private fun LanguagesContentPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            MDLanguageWordSpaceListItem(
                LanguageWordSpace(code = "en", wordsCount = 100),
                isSelected = false,
                onClick = {}
            )
        }
    }
}
