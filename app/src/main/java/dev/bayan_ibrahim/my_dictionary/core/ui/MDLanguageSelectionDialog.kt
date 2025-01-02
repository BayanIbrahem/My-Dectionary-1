package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDSearchDialogInputField
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCard
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardColors
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroupDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.horizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.allLanguages
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.bottomOnly
import dev.bayan_ibrahim.my_dictionary.ui.theme.topOnly

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
                if (selectedWordSpace?.hasMatchQuery(query) == false) {
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
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
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
        "Primary languages $it" // TODO, string res
    },
    secondaryListCountTitleBuilder: @Composable (count: Int) -> String = {
        "Secondary languages $it" // TODO, string res
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
    MDSearchDialogInputField(
        searchQuery = query,
        onSearchQueryChange = onQueryChange,
        modifier = modifier,
        label = "Language",// TODO, string res
        placeholder = "${randomLanguage.code}, ${randomLanguage.selfDisplayName} or ${randomLanguage.localDisplayName}", // TODO, string res
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LanguagesContent(
    primaryList: List<LanguageWordSpace>,
    secondaryList: List<LanguageWordSpace>,
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
    val singleListShape = MDHorizontalCardGroupDefaults.shape
    val firstItemShape = singleListShape.topOnly
    val lastItemShape = singleListShape.bottomOnly
    val middleItemShape = singleListShape.copy(CornerSize(0.dp))
    val selectedColor = MDHorizontalCardDefaults.primaryColors
    val normalColor = MDHorizontalCardDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
    )
    val cardColors = MDHorizontalCardGroupDefaults.colors()
    val cardStyles = MDHorizontalCardGroupDefaults.styles()
    LazyColumn(
        modifier = modifier.scrollbar(state, stickHeadersContentType = "Label"),
        state = state,
    ) {
        if (primaryItemsCount > 0) {
            stickyHeader(contentType = "Label") {
                HeaderTitle(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = primaryListCountTitleBuilder(primaryItemsCount),
                )
            }
        }
        horizontalCardGroup(
            itemsCount = primaryItemsCount,
            shape = singleListShape,
            topOnlyShape = firstItemShape,
            bottomOnlyShape = lastItemShape,
            middleShape = middleItemShape,
            colors = cardColors,
            styles = cardStyles
        ) { i ->
            val wordSpace by remember(primaryList, i) {
                derivedStateOf {
                    primaryList[i]
                }
            }
            MDWordSpaceCardItem2(
                selectedLanguageCode = selectedLanguageCode,
                wordSpace = wordSpace,
                selectedColor = selectedColor,
                normalColor = normalColor,
                onClickWordSpace = onClickWordSpace
            )
        }
        if (secondaryItemsCount > 0) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            stickyHeader(contentType = "Label") {
                HeaderTitle(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = secondaryListCountTitleBuilder(secondaryItemsCount),
                )
            }
        }
        horizontalCardGroup(
            itemsCount = secondaryItemsCount,
            shape = singleListShape,
            topOnlyShape = firstItemShape,
            bottomOnlyShape = lastItemShape,
            middleShape = middleItemShape,
            colors = cardColors,
            styles = cardStyles
        ) { i ->
            val wordSpace by remember(secondaryList, i) {
                derivedStateOf {
                    secondaryList[i]
                }
            }
            MDWordSpaceCardItem2(
                selectedLanguageCode = selectedLanguageCode,
                wordSpace = wordSpace,
                selectedColor = selectedColor,
                normalColor = normalColor,
                onClickWordSpace = onClickWordSpace
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
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
private fun MDWordSpaceCardItem2(
    selectedLanguageCode: LanguageCode?,
    wordSpace: LanguageWordSpace,
    selectedColor: MDHorizontalCardColors,
    normalColor: MDHorizontalCardColors,
    onClickWordSpace: (LanguageWordSpace) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors by remember(selectedLanguageCode) {
        derivedStateOf {
            if (selectedLanguageCode?.code == wordSpace.code) {
                selectedColor
            } else {
                normalColor
            }
        }
    }
    MDHorizontalCard(
        onClick = {
            onClickWordSpace(wordSpace)
        },
        modifier = modifier,
        colors = colors,
        leadingIcon = {
            Text(
                text = wordSpace.uppercaseCode,
                style = if (wordSpace.isLongCode) {
                    MaterialTheme.typography.titleSmall
                } else {
                    MaterialTheme.typography.titleLarge
                },
            )
        },
        subtitle = {
            Text("${wordSpace.wordsCount} words") // TODO string res
        }
    ) {
        Text(wordSpace.localDisplayName)
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

