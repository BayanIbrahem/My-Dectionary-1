package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component.MDWordDetailsTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDWordDetailsEditModeTopAppBar(
    language: Language,
    validWord: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDWordDetailsTopAppBar(
        modifier = modifier,
        language = language,
        leading = {
            LeadingActions()
        },
        trailing = {
            TrailingActions(
                validWord = validWord,
                onCancel = onCancel,
                onSave = onSave,
            )
        }
    )
}

@Composable
private fun LeadingActions(
    modifier: Modifier = Modifier,
) {
    Text(
        text = "New", // TODO, string res
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier,
    )
}

@Composable
private fun TrailingActions(
    validWord: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
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
                MDWordDetailsEditModeTopAppBar(
                    language = Language("en".code, "English", "English"),
                    validWord = true,
                    onCancel = {},
                    onSave = {},
                )
            }
        }
    }
}