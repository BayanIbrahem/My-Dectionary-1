package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun WordDetailsTopAppBar(
    isEditModeOn: Boolean,
    validWord: Boolean,
    isNewWord: Boolean,
    language: Language,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onClickWordStatistics: () -> Unit,
    modifier: Modifier = Modifier,
    /**
     * if the top app bar container has padding then we should draw larger background to cover it
     */
    extraPadding: Dp = 32.dp,
) {
    val bgColor = MaterialTheme.colorScheme.surfaceContainer
    val dividerColor = MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier
            .height(60.dp)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .drawBehind {
                val width = size.width + extraPadding.toPx()
                val left = extraPadding.toPx() / 2
                drawRect(
                    color = bgColor,
                    size = size.copy(width = width),
                    topLeft = Offset(-left, 0f),
                )
                drawLine(
                    color = dividerColor,
                    start = Offset(-left, size.height),
                    end = Offset(size.width + left, size.height)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Leading(
            isNewWord = isNewWord,
            onClickWordStatistics = onClickWordStatistics,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = language.fullDisplayName,
            style = MaterialTheme.typography.titleSmall
        )
        TrailingActions(
            isEditModeOn = isEditModeOn,
            validWord = validWord,
            onCancel = onCancel,
            onSave = onSave,
            onShare = onShare,
            onEdit = onEdit,
            modifier = Modifier
                .align(Alignment.CenterEnd),
        )
    }
}

@Composable
private fun Leading(
    isNewWord: Boolean,
    onClickWordStatistics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isNewWord) {
        Text(
            text = "New", // TODO, string res
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error,
            modifier = modifier,
        )
    } else {
        IconButton(
            modifier = modifier,
            onClick = onClickWordStatistics
        ) {
            MDIcon(MDIconsSet.Statistics/* TODO, icon res */)
        }
    }
}

@Composable
private fun TrailingActions(
    isEditModeOn: Boolean,
    validWord: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        if (isEditModeOn) {
            EditModeTrailingActions(validWord = validWord, onCancel = onCancel, onSave = onSave)
        } else {
            PreviewModeTrailingActions(onShare = onShare, onEdit = onEdit)
        }
    }
}

@Composable
private fun RowScope.EditModeTrailingActions(
    validWord: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    IconButton(
        onClick = onCancel,
    ) {
        MDIcon(
            icon = MDIconsSet.Close,
            contentDescription = "cancel changes"
        ) // TODO, string res
    }
    IconButton(
        onClick = onSave,
        enabled = validWord,
    ) {
        MDIcon(
            icon = MDIconsSet.Save,
            contentDescription = "save word",
        ) // TODO, string res
    }

}

@Composable
private fun PreviewModeTrailingActions(onShare: () -> Unit, onEdit: () -> Unit) {
    IconButton(
        onClick = onShare
    ) {
        MDIcon(
            icon = MDIconsSet.Share,
            contentDescription = "share word",
        ) // TODO, string res
    }
    IconButton(
        onClick = onEdit
    ) {
        MDIcon(
            icon = MDIconsSet.Edit,
            contentDescription = "edit word",
        ) // TODO, string res
    }
}

@Preview
@Composable
private fun WordDetailsTopAppBarPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                WordDetailsTopAppBar(
                    isEditModeOn = false,
                    isNewWord = false,
                    language = Language("en".code, "English", "English"),
                    validWord = true,
                    onCancel = {},
                    onSave = {},
                    onShare = {},
                    onEdit = {},
                    onClickWordStatistics = {},
                )
                WordDetailsTopAppBar(
                    isEditModeOn = true,
                    isNewWord = false,
                    language = Language("en".code, "English", "English"),
                    validWord = true,
                    onCancel = {},
                    onSave = {},
                    onShare = {},
                    onEdit = {},
                    onClickWordStatistics = {}
                )
                WordDetailsTopAppBar(
                    isEditModeOn = true,
                    isNewWord = false,
                    language = Language("en".code, "English", "English"),
                    validWord = false,
                    onCancel = {},
                    onSave = {},
                    onShare = {},
                    onEdit = {},
                    onClickWordStatistics = {}
                )

                WordDetailsTopAppBar(
                    isEditModeOn = true,
                    isNewWord = true,
                    language = Language("en".code, "English", "English"),
                    validWord = true,
                    onCancel = {},
                    onSave = {},
                    onShare = {},
                    onEdit = {},
                    onClickWordStatistics = {}
                )
            }
        }
    }
}