package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapPluralsResource
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.core.ui.MDSimpleLanguageSelectionDialog
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.allLanguages
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.MDWordSpaceTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.MDWordSpaceListItem

@Composable
fun MDWordSpaceScreen(
    uiState: MDWordSpaceUiState,
    uiActions: MDWordSpaceUiActions,
    modifier: Modifier = Modifier,
) {
    var showAddNewWordSpaceDialog by remember {
        mutableStateOf(false)
    }
    var query by remember {
        mutableStateOf("")
    }
    val wordSpaces by remember(uiState.wordSpacesWithActions, query) {
        derivedStateOf {
            val existedCodes = uiState.wordSpacesWithActions.map { it.first }.toSet()
            allLanguages.mapNotNull {
                if (it.key !in existedCodes && it.value.hasMatchQuery(query)) {
                    LanguageWordSpace(it.value.code)
                } else {
                    null
                }
            }
        }
    }
    MDSimpleLanguageSelectionDialog(
        showDialog = showAddNewWordSpaceDialog,
        primaryList = wordSpaces,
        primaryListCountTitleBuilder = {
            firstCapPluralsResource(R.plurals.language, it)
        },
        onDismissRequest = {
            showAddNewWordSpaceDialog = false
        },
        onSelectWordSpace = { wordSpace ->
            uiActions.onAddNewWordSpace(wordSpace)
        },
        query = query,
        onQueryChange = {
            query = it
        },
        secondaryList = emptyList()
    )
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        topBar = {
            MDWordSpaceTopAppBar(onNavigationIconClick = uiActions::onOpenNavDrawer)
        }
    ) {
        LazyVerticalGrid(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(8.dp),
            columns = GridCells.Adaptive(300.dp)
        ) {
            items(uiState.wordSpacesWithActions) { (state, actions) ->
                MDWordSpaceListItem(
                    state = state,
                    actions = actions,
                    currentEditableLanguageCode = uiState.currentEditableWordSpaceLanguageCode,
                    navigateToStatistics = uiActions::navigateToStatistics
                )
            }
            item(
                span = {
                    GridItemSpan(this.maxLineSpan)
                }
            ) {
                ElevatedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.currentEditableWordSpaceLanguageCode == null,
                    onClick = {
                        showAddNewWordSpaceDialog = true
                    }
                ) {
                    Text("Add New Word Space")
                }
            }
        }
    }
}