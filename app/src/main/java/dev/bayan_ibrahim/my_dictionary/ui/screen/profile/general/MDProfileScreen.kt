package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.upperStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2CheckboxItem
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2RadioButtonItem
import dev.bayan_ibrahim.my_dictionary.domain.model.MDUserPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordDetailsDirectionSource
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.component.MDProfileTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDProfileScreen(
    uiState: MDProfileUiState,
    userPreferences: MDUserPreferences,
    uiActions: MDProfileUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        topBar = {
            MDProfileTopAppBar(onNavigationIconClick = uiActions::onOpenNavDrawer)
        },
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BackupAndRestoreGroup(
                onClickImportFromFile = uiActions::navigateToImportFromFile,
                onClickExportToFile = uiActions::navigateToExportToFile,
                onClickSync = uiActions::navigateToSync,
            )
            ThemeGroup(uiActions::navigateToAppTheme)
            WordsListGroup(
                isLiveTemplateEnabled = userPreferences.liveMemorizingProbability,
                onToggleLiveTemplate = uiActions::onToggleLiveTemplate
            )
            WordDetailsGroup(
                selectedAlignmentSource = userPreferences.wordDetailsDirectionSource,
                onSelectSource = uiActions::onToggleWordDetailsAlignmentSource
            )
        }
    }
}

@Composable
private fun BackupAndRestoreGroup(
    onClickImportFromFile: () -> Unit,
    onClickExportToFile: () -> Unit,
    onClickSync: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDCard2(
        modifier = modifier,
        header = {
            MDCard2ListItem(firstCapStringResource(R.string.backup_restore))
        }
    ) {
        MDCard2ListItem(
            onClick = onClickImportFromFile,
            leadingIcon = {
                MDIcon(MDIconsSet.ImportFromFile, contentDescription = null)
            },
            title = firstCapStringResource(R.string.import_from_file)
        )

        MDCard2ListItem(
            onClick = onClickExportToFile,
            leadingIcon = {
                MDIcon(MDIconsSet.ExportToFile, contentDescription = null)
            },
            title = firstCapStringResource(R.string.export_to_file)
        )

        MDCard2ListItem(
            onClick = null, // TODO onClickSync
            theme = MDCard2ListItemTheme.DisabledSurface,
            leadingIcon = {
                MDIcon(MDIconsSet.Sync, contentDescription = null)
            },
            title = firstCapStringResource(R.string.sync),
        )
    }
}

@Composable
private fun ThemeGroup(
    onClickAppTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDCard2(
        modifier = modifier,
        header = {
            MDCard2ListItem(firstCapStringResource(R.string.app_theme))
        }
    ) {
        MDCard2ListItem(
            onClick = onClickAppTheme,
            leadingIcon = {
                MDIcon(icon = MDIconsSet.Colors, contentDescription = null)
            },
            title = firstCapStringResource(R.string.theme)
        )
    }
}

@Composable
private fun WordsListGroup(
    isLiveTemplateEnabled: Boolean,
    onToggleLiveTemplate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    MDCard2(
        modifier = modifier,
        header = {
            MDCard2ListItem(title = firstCapStringResource(R.string.words_list))
        }
    ) {
        MDCard2CheckboxItem(
            checked = isLiveTemplateEnabled,
            onCheckedChange = {
                onToggleLiveTemplate(!isLiveTemplateEnabled)
            },
            subtitle = if (isLiveTemplateEnabled) {
                firstCapStringResource(R.string.memorize_probability_live_preview_checked)
            } else {
                firstCapStringResource(R.string.memorize_probability_live_preview_unchecked)
            },
            title = firstCapStringResource(R.string.memorize_probability_live_preview)
        )
    }
}

@Composable
private fun WordDetailsGroup(
    selectedAlignmentSource: WordDetailsDirectionSource,
    onSelectSource: (WordDetailsDirectionSource) -> Unit,
    modifier: Modifier = Modifier,
) {
    MDCard2(
        modifier = modifier,
        header = {
            MDCard2ListItem(
                title = firstCapStringResource(R.string.word_details),
                subtitle = firstCapStringResource(R.string.alignment_hint)
            )
        },
    ) {
        WordDetailsDirectionSource.entries.forEach { source ->
            MDCard2RadioButtonItem(
                selected = source == selectedAlignmentSource,
                onClick = {
                    onSelectSource(source)
                },
                secondary = {
                    MDIcon(source.icon)
                },
                leadingRadioButton = true
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(source.label)
                        val current = source.current
                        if (current != null) {
                            append(" ")
                            pushStyle(
                                SpanStyle(
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            val text = when (current) {
                                LayoutDirection.Ltr -> upperStringResource(R.string.ltr)
                                LayoutDirection.Rtl -> upperStringResource(R.string.rtl)
                            }
                            append("${upperStringResource(R.string.current)} $text")
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun MDProfileScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDProfileScreen(
                    uiState = MDProfileMutableUiState().apply {
                        onExecute { true }
                    },
                    userPreferences = MDUserPreferences(),
                    uiActions = MDProfileUiActions(
                        object : MDProfileNavigationUiActions, MDAppNavigationUiActions {
                            override fun onOpenNavDrawer() {}
                            override fun onCloseNavDrawer() {}

                            override fun navigateToImportFromFile() {}
                            override fun navigateToExportToFile() {}
                            override fun navigateToSync() {}
                            override fun navigateToAppTheme() {}
                        },
                        object : MDProfileBusinessUiActions {
                            override fun onToggleLiveTemplate(liveTemplate: Boolean) {}
                            override fun onToggleWordDetailsAlignmentSource(source: WordDetailsDirectionSource) {}
                        },
                    )
                )
            }
        }
    }
}