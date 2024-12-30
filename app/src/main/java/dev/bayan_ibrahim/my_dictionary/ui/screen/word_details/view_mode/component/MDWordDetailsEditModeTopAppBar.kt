package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.view_mode.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component.MDWordDetailsTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode.component.MDWordDetailsEditModeTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDWordDetailsViewModeTopAppBar(
    language: Language,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onClickWordStatistics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDWordDetailsTopAppBar(
        modifier = modifier,
        language = language,
        leading = {
            LeadingActions(onClickWordStatistics = onClickWordStatistics)
        },
        trailing = {
            TrailingActions(
                onShare = onShare,
                onEdit = onEdit
            )
        }
    )
}

@Composable
private fun LeadingActions(
    onClickWordStatistics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = onClickWordStatistics
    ) {
        MDIcon(MDIconsSet.Statistics/* TODO, icon res */)
    }
}

@Composable
private fun TrailingActions(
    onShare: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
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
}

@Preview
@Composable
private fun MDWordsDetailsViewModeTopAppBarPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDWordDetailsViewModeTopAppBar(
                    language = Language("en".code, "English", "English"),
                    onShare = {},
                    onEdit = {},
                    onClickWordStatistics = {},
                )
            }
        }
    }
}