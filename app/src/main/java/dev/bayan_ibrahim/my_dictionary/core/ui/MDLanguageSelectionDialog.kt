package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.eachFirstCapPluralsResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.lowerPluralsResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDSearchField
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2ActionRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.card2Content
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2CancelAction
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2ConfirmAction
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.allLanguages
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.MDLanguageCode
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDLanguageSelectionDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    primaryList: List<LanguageWordSpace>,
    secondaryList: List<LanguageWordSpace>,
    onSelectWordSpace: (LanguageWordSpace) -> Unit,
    modifier: Modifier = Modifier,
    primaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        eachFirstCapPluralsResource(R.plurals.x_language, it, stringResource(R.string.primary))
    },
    secondaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        eachFirstCapPluralsResource(R.plurals.x_language, it, stringResource(R.string.secondary))
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
                if (selectedWordSpace?.hasMatchQuery(query) == false) {
                    selectedWordSpace = null
                }
            }
        }
    }
    if (showDialog)
        Dialog(
            onDismissRequest = onDismissRequest,
        ) {
            MDCard2(
                modifier = modifier,
                header = {
                    LanguageSearchBar(
                        query = query,
                        onQueryChange = queryChangeAction,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                    )
                },
                footer = {
                    MDCard2ActionRow {
                        MDCard2ConfirmAction(
                            label = firstCapStringResource(R.string.select_x, firstCapStringResource(R.string.language)),
                            enabled = selectedWordSpace != null,
                        ) {
                            selectedWordSpace?.let {
                                onSelectWordSpace(it)
                                onDismissRequest()
                            }
                        }
                        MDCard2CancelAction(onClick = onDismissRequest)

                    }
                },
            ) {
                LanguagesContent(
                    primaryList = primaryList,
                    secondaryList = secondaryList,
                    selectedLanguageCode = selectedWordSpace,
                    onClickWordSpace = { selectedWordSpace = it },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .sizeIn(maxWidth = 300.dp, maxHeight = 350.dp),
                    primaryListCountTitleBuilder = primaryListCountTitleBuilder,
                    secondaryListCountTitleBuilder = secondaryListCountTitleBuilder,
                    hideWordCountAndProgress = hideWordCountAndProgress,
                )
            }
        }
}


@Composable
fun MDSimpleLanguageSelectionDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    query: String,
    onSelectWordSpace: (LanguageWordSpace) -> Unit,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    primaryList: List<LanguageWordSpace>,
    secondaryList: List<LanguageWordSpace>,
    primaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        eachFirstCapPluralsResource(R.plurals.x_language, it, stringResource(R.string.primary))
    },
    secondaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        eachFirstCapPluralsResource(R.plurals.x_language, it, stringResource(R.string.secondary))
    },
    hideWordCountAndProgress: Boolean = true,
) {
    MDLanguageSelectionDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        query = query,
        onQueryChange = onQueryChange,
        primaryList = primaryList,
        secondaryList = secondaryList,
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
    MDSearchField(
        searchQuery = query,
        onSearchQueryChange = onQueryChange,
        modifier = modifier,
        label = stringResource(R.string.language),
        placeholder = "${randomLanguage.code}, ${stringResource(R.string.x_or_y, randomLanguage.selfDisplayName, randomLanguage.localDisplayName)}",
    )
}

@Composable
private fun LanguagesContent(
    primaryList: List<LanguageWordSpace>,
    secondaryList: List<LanguageWordSpace>,
    selectedLanguageCode: LanguageCode?,
    onClickWordSpace: (LanguageWordSpace) -> Unit,
    modifier: Modifier = Modifier,
    hideWordCountAndProgress: Boolean = false,
    primaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        eachFirstCapPluralsResource(R.plurals.x_language, it, stringResource(R.string.primary))
    },
    secondaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        eachFirstCapPluralsResource(R.plurals.x_language, it, stringResource(R.string.secondary))
    },
) {
    val primaryItemsCount by remember(primaryList) {
        derivedStateOf { primaryList.count() }
    }
    val secondaryItemsCount by remember(secondaryList) {
        derivedStateOf { secondaryList.count() }
    }
    val state = rememberLazyListState()

    val selectedTheme = MDCard2ListItemTheme.PrimaryContainer
    val normalTheme = MDCard2ListItemTheme.SurfaceContainerHighest
    LazyColumn(
        modifier = modifier
            .scrollbar(state = state, stickHeadersContentType = "Label"),
        state = state,
    ) {
        card2Content(
            contentCount = primaryItemsCount,
            headerContentType = "Label",
            header = if (primaryItemsCount > 0) {
                {
                    HeaderTitle(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = primaryListCountTitleBuilder(primaryItemsCount),
                    )
                }
            } else null,
            stickyHeader = true,
        ) { i ->
            val wordSpace by remember(primaryList, i) {
                derivedStateOf {
                    primaryList[i]
                }
            }
            MDWordSpaceListItem(
                selectedLanguageCode = selectedLanguageCode,
                wordSpace = wordSpace,
                selectedTheme = selectedTheme,
                normalTheme = normalTheme,
                onClickWordSpace = onClickWordSpace
            )
        }
        if (secondaryItemsCount > 0) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            card2Content(
                contentCount = secondaryItemsCount,
                headerContentType = "Label",
                header = {
                    HeaderTitle(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = secondaryListCountTitleBuilder(secondaryItemsCount),
                    )
                },
                stickyHeader = true,
            ) { i ->
                val wordSpace by remember(secondaryList, i) {
                    derivedStateOf {
                        secondaryList[i]
                    }
                }
                MDWordSpaceListItem(
                    selectedLanguageCode = selectedLanguageCode,
                    wordSpace = wordSpace,
                    selectedTheme = selectedTheme,
                    normalTheme = normalTheme,
                    onClickWordSpace = onClickWordSpace
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (secondaryItemsCount + primaryItemsCount == 0) {
            // empty list
            item {
                Text(
                    text = eachFirstCapPluralsResource(R.plurals.language, 0),
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
private fun MDWordSpaceListItem(
    selectedLanguageCode: LanguageCode?,
    wordSpace: LanguageWordSpace,
    selectedTheme: MDCard2ListItemTheme,
    normalTheme: MDCard2ListItemTheme,
    onClickWordSpace: (LanguageWordSpace) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme by remember(selectedLanguageCode) {
        derivedStateOf {
            if (selectedLanguageCode?.code == wordSpace.code) {
                selectedTheme
            } else {
                normalTheme
            }
        }
    }
    MDCard2ListItem(
        title = wordSpace.localDisplayName,
        modifier = modifier,
        leadingIcon = {
            MDLanguageCode(wordSpace)
        },
        onClick = {
            onClickWordSpace(wordSpace)
        },
        theme = theme,
        subtitle = lowerPluralsResource(R.plurals.word, wordSpace.wordsCount)
    )
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

