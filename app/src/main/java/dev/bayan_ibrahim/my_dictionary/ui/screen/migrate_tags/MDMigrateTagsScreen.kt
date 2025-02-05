package dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_tags


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelector
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorUiActions
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.MDContextTagsSelectorUiState
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDMigrateTagsScreen(
    uiState: MDMigrateTagsUiState,
    uiActions: MDMigrateTagsUiActions,
    tagsSelectorUiState: MDContextTagsSelectorUiState,
    tagsSelectorUiActions: MDContextTagsSelectorUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
//       topBar = {
//           MDMigrateTagsTopAppBar()
//       },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // TODO, string res
            Text("Select Target Tags", style = MaterialTheme.typography.titleMedium)
            MDContextTagsSelector(
                state = tagsSelectorUiState,
                actions = tagsSelectorUiActions,
                allowEditTags = true,
                allowAddTags = false,
                allowRemoveTags = false,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
