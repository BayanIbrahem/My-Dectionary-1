package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.ui.MDLanguageWordSpaceListItem
import dev.bayan_ibrahim.my_dictionary.core.ui.scrollbar
import dev.bayan_ibrahim.my_dictionary.domain.model.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.allLanguages
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MDWordsListLanguageSelectionPageDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    languagesWithWords: PersistentList<LanguageWordSpace>,
    languagesWithoutWords: PersistentList<LanguageWordSpace>,
    onSelectLanguage: (Language) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedLanguage: Language? by remember {
        mutableStateOf(null)
    }
    val queryChangeAction by remember {
        derivedStateOf {
            { query: String ->
                onQueryChange(query)
                // if the selected language filtered out from the selection, it became null
                if (selectedLanguage?.hasMatchQuery(query) == false) {
                    selectedLanguage = null
                }
            }
        }
    }
    MDAlertDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        title = {
            LanguageSearchBar(
                query = query,
                onQueryChange = queryChangeAction,
                modifier = Modifier.padding(8.dp),
            )
        },
        onPrimaryClick = {
            selectedLanguage?.let(onSelectLanguage)
        },
        primaryActionLabel = "Select Language", // TODO, string res
        primaryClickEnabled = selectedLanguage != null,
        onSecondaryClick = onDismissRequest,
        showActionsHorizontalDivider = false,
        modifier = modifier.width(250.dp),
    ) {
        LanguagesContent(
            languagesWithWords = languagesWithWords,
            languagesWithoutWords = languagesWithoutWords,
            selectedLanguageCode = selectedLanguage?.code,
            onClickLanguage = { selectedLanguage = it },
            modifier = Modifier
                .padding(8.dp)
                .size(250.dp, 200.dp),
        )
    }
}

@Composable
private fun LanguageSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val randomLanguage by remember {
        derivedStateOf {
            allLanguages.values.random()
        }
    }
    MDBasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        label = "Language",// TODO, string res
        placeholder = "${randomLanguage.code}, ${randomLanguage.selfDisplayName} or ${randomLanguage.localDisplayName}", // TODO, string res
        colors = MDTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        leadingIcons = {
            Icon(Icons.Default.Search, null)
        },
        trailingIcons = {
            IconButton(
                onClick = {
                    onQueryChange("")
                }
            ) {
                Icon(Icons.Rounded.Clear, null)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LanguagesContent(
    languagesWithWords: PersistentList<LanguageWordSpace>,
    languagesWithoutWords: PersistentList<LanguageWordSpace>,
    selectedLanguageCode: String?,
    onClickLanguage: (Language) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeWorkSpacesCount by remember(languagesWithWords) {
        derivedStateOf { languagesWithWords.count() }
    }
    val inactiveWorkSpacesCount by remember(languagesWithoutWords) {
        derivedStateOf { languagesWithoutWords.count() }
    }
    val state = rememberLazyListState()
    LazyColumn(
        modifier = modifier.scrollbar(state, stickHeadersContentType = "Label"),
        state = state,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (activeWorkSpacesCount > 0) {
            stickyHeader(contentType = "Label") {
                Text(
                    text = "Active work spaces $activeWorkSpacesCount", // TODO, string res
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                )
            }
        }
        items(languagesWithWords) { wordSpace ->
            MDLanguageWordSpaceListItem(
                wordSpace = wordSpace,
                isSelected = wordSpace.language.code == selectedLanguageCode,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                selectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                selectedContentColor = MaterialTheme.colorScheme.onSurface,
                onClick = { onClickLanguage(wordSpace.language) }
            )
        }
        if (inactiveWorkSpacesCount > 0) {
            item {
                HorizontalDivider(modifier = Modifier.padding(4.dp))
            }
            stickyHeader(contentType = "Label") {
                Text(
                    text = "Inactive work spaces $inactiveWorkSpacesCount", // TODO, string res
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                )
            }
        }
        items(languagesWithoutWords) { wordSpace ->
            MDLanguageWordSpaceListItem(
                wordSpace = wordSpace,
                isSelected = wordSpace.language.code == selectedLanguageCode,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                selectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                selectedContentColor = MaterialTheme.colorScheme.onSurface,
                onClick = { onClickLanguage(wordSpace.language) }
            )
        }
        if (inactiveWorkSpacesCount + activeWorkSpacesCount == 0) {
            // empty list
            item {
                Text(
                    text = "No languages", // TODO, string res
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}


@Preview
@Composable
private fun LanguageSearchBarPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            var query by remember { mutableStateOf("en") }
            LanguageSearchBar(
                query = query,
                onQueryChange = { query = it }
            )
        }
    }
}

@Preview
@Composable
private fun MDWordsListLanguageSelectionPageDialogPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            var query by remember {
                mutableStateOf("")
            }
            MDWordsListLanguageSelectionPageDialog(
                showDialog = true,
                onDismissRequest = {},
                query = query,
                onQueryChange = { query = it },
                languagesWithWords = persistentListOf(
                    LanguageWordSpace(
                        language = Language(code = "en", selfDisplayName = "English", localDisplayName = "English"),
                        wordsCount = 100,
                        averageLearningProgress = 0.5f
                    ),

                    LanguageWordSpace(
                        language = Language(code = "es", selfDisplayName = "Spanish", localDisplayName = "Español"),
                        wordsCount = 250,
                        averageLearningProgress = 0.1f
                    )
                ),
                languagesWithoutWords = persistentListOf(
                    LanguageWordSpace(
                        Language(code = "de", selfDisplayName = "German", localDisplayName = "Deutsch")
                    )
                ),
                onSelectLanguage = {}
            )
        }
    }
}
