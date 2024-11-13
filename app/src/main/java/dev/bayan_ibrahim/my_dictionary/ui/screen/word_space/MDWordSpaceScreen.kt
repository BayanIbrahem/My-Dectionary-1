package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.ui.MDLanguageWordSpaceListItem
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.MDWordSpaceListItem

@Composable
fun MDWordSpaceScreen(
    uiState: MDWordSpaceUiState,
    uiActions: MDWordSpaceUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
    ) {
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(uiState.wordSpacesWithActions) {(state, actions) ->
                MDWordSpaceListItem(
                    state = state,
                    actions = actions,
                    currentEditableLanguageCode = uiState.currentEditableWordSpaceLanguageCode,
                )
            }
        }
    }
}