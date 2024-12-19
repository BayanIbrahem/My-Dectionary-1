package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.allLanguages
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Composable
fun MDLanguageSelectionDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    primaryList: PersistentList<LanguageWordSpace>,
    secondaryList: PersistentList<LanguageWordSpace>,
    onSelectWordSpace: (LanguageWordSpace) -> Unit,
    modifier: Modifier = Modifier,
    primaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        "Primary languages $it" // TODO, string res
    },
    secondaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        "Secondary languages $it" // TODO, string res
    },
    hideWordCountAndProgress: Boolean = false,
) {
    var selectedWordSpace: LanguageWordSpace? by remember {
        mutableStateOf(null)
    }
    val queryChangeAction by remember {
        derivedStateOf {
            { query: String ->
                onQueryChange(query)
                // if the selected language filtered out from the selection, it became null
                if (selectedWordSpace?.language?.hasMatchQuery(query) == false) {
                    selectedWordSpace = null
                }
            }
        }
    }
    MDAlertDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        headerModifier = Modifier,
        title = {
            LanguageSearchBar(
                query = query,
                onQueryChange = queryChangeAction,
                modifier = Modifier.padding(8.dp),
            )
        },
        actions = {
            MDAlertDialogActions(
                onDismissRequest = onDismissRequest,
                onPrimaryClick = {
                    selectedWordSpace?.let(onSelectWordSpace)
                },
                primaryActionLabel = "Select Language", // TODO, string res
                primaryClickEnabled = selectedWordSpace != null,
                onSecondaryClick = onDismissRequest,
            )
        },
        showActionsHorizontalDivider = false,
        modifier = modifier.width(250.dp),
    ) {
        LanguagesContent(
            primaryList = primaryList,
            secondaryList = secondaryList,
            selectedLanguageCode = selectedWordSpace?.language?.code,
            onClickWordSpace = { selectedWordSpace = it },
            modifier = Modifier
                .padding(8.dp)
                .size(250.dp, 200.dp),
            primaryListCountTitleBuilder = primaryListCountTitleBuilder,
            secondaryListCountTitleBuilder = secondaryListCountTitleBuilder,
            hideWordCountAndProgress = hideWordCountAndProgress,
        )
    }
}


@Composable
fun MDSimpleLanguageSelectionDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onSelectWordSpace: (LanguageWordSpace) -> Unit,
    modifier: Modifier = Modifier,
    primaryList: List<LanguageWordSpace>? = null,
    secondaryList: List<LanguageWordSpace>? = null,
    primaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        "Primary languages $it" // TODO, string res
    },
    secondaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        "Secondary languages $it" // TODO, string res
    },
    hideWordCountAndProgress: Boolean = true,
) {
    var query by remember {
        mutableStateOf("")
    }
    val primaryLanguagesList by remember(primaryList, query) {
        derivedStateOf {
            (primaryList ?: allLanguages.map { LanguageWordSpace(it.value) }).filter {
                it.language.hasMatchQuery(query)
            }.toPersistentList()
        }
    }

    val secondaryLanguagesList by remember(primaryList, query) {
        derivedStateOf {
            (secondaryList ?: emptyList()).filter {
                it.language.hasMatchQuery(query)
            }.toPersistentList()
        }
    }
    MDLanguageSelectionDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        query = query,
        onQueryChange = { query = it },
        primaryList = primaryLanguagesList,
        secondaryList = secondaryLanguagesList,
        onSelectWordSpace = onSelectWordSpace,
        modifier = modifier,
        primaryListCountTitleBuilder = primaryListCountTitleBuilder,
        secondaryListCountTitleBuilder = secondaryListCountTitleBuilder,
        hideWordCountAndProgress = hideWordCountAndProgress,
    )
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
            MDIcon(MDIconsSet.Search, contentDescription = null)  // field has already label // checked
        },
        trailingIcons = {
            IconButton(
                onClick = {
                    onQueryChange("")
                }
            ) {
                MDIcon(MDIconsSet.Close) // checked
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LanguagesContent(
    primaryList: PersistentList<LanguageWordSpace>,
    secondaryList: PersistentList<LanguageWordSpace>,
    selectedLanguageCode: LanguageCode?,
    onClickWordSpace: (LanguageWordSpace) -> Unit,
    modifier: Modifier = Modifier,
    hideWordCountAndProgress: Boolean = false,
    primaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        "Primary languages $it" // TODO, string res
    },
    secondaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        "Secondary languages $it" // TODO, string res
    },
) {
    val primaryItemsCount by remember(primaryList) {
        derivedStateOf { primaryList.count() }
    }
    val secondaryItemsCount by remember(secondaryList) {
        derivedStateOf { secondaryList.count() }
    }
    val state = rememberLazyListState()
    LazyColumn(
        modifier = modifier.scrollbar(state, stickHeadersContentType = "Label"),
        state = state,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (primaryItemsCount > 0) {
            stickyHeader(contentType = "Label") {
                HeaderTitle(text = primaryListCountTitleBuilder(primaryItemsCount))
            }
        }
        items(primaryList) { wordSpace ->
            MDLanguageWordSpaceListItem(
                wordSpace = wordSpace,
                hideWordCountAndProgress = hideWordCountAndProgress,
                isSelected = wordSpace.language.code == selectedLanguageCode,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                selectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                selectedContentColor = MaterialTheme.colorScheme.onSurface,
                onClick = { onClickWordSpace(wordSpace) }
            )
        }
        if (secondaryItemsCount > 0) {
            item {
                HorizontalDivider(modifier = Modifier.padding(4.dp))
            }
            stickyHeader(contentType = "Label") {
                HeaderTitle(
                    text = secondaryListCountTitleBuilder(secondaryItemsCount),
                )
            }
        }
        items(secondaryList) { wordSpace ->
            MDLanguageWordSpaceListItem(
                wordSpace = wordSpace,
                isSelected = wordSpace.language.code == selectedLanguageCode,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                selectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                selectedContentColor = MaterialTheme.colorScheme.onSurface,
                onClick = { onClickWordSpace(wordSpace) }
            )
        }
        if (secondaryItemsCount + primaryItemsCount == 0) {
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

@Composable
private fun HeaderTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
        )
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
            MDLanguageSelectionDialog(
                showDialog = true,
                onDismissRequest = {},
                query = query,
                onQueryChange = { query = it },
                primaryList = persistentListOf(
                    LanguageWordSpace(
                        language = Language(code = "en".code, selfDisplayName = "English", localDisplayName = "English"),
                        wordsCount = 100,
                        averageMemorizingProbability = 0.5f
                    ),

                    LanguageWordSpace(
                        language = Language(code = "es".code, selfDisplayName = "Spanish", localDisplayName = "Español"),
                        wordsCount = 250,
                        averageMemorizingProbability = 0.1f
                    )
                ),
                secondaryList = persistentListOf(
                    LanguageWordSpace(
                        Language(code = "de".code, selfDisplayName = "German", localDisplayName = "Deutsch")
                    )
                ),
                onSelectWordSpace = {}
            )
        }
    }
}
