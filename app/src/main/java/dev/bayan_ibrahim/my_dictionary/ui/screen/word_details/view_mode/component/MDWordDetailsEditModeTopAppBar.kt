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
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component.MDWordDetailsTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDWordDetailsViewModeTopAppBar(
    language: Language,
    onShare: () -> Unit,
    onNavigateUp: () -> Unit,
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
                onNavigateUp = onNavigateUp
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
    onNavigateUp: () -> Unit,
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
                contentDescription = firstCapStringResource(R.string.share_x, firstCapStringResource(R.string.word)),
            )
        }
        IconButton(
            onClick = onNavigateUp
        ) {
            MDIcon(
                icon = MDIconsSet.ArrowForward,
                contentDescription = firstCapStringResource(R.string.navigate_up),
            )
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
                    language = Language("en", "English", "English"),
                    onShare = {},
                    onNavigateUp = {},
                    onClickWordStatistics = {},
                )
            }
        }
    }
}