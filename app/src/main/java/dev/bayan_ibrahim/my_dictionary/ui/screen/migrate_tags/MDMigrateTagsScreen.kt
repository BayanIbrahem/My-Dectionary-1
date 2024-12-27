package dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_tags


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDMigrateTagsScreen(
    uiState: MDMigrateTagsUiState,
    uiActions: MDMigrateTagsUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
//       topBar = {
//           MDMigrateTagsTopAppBar()
//       },
    ) {
    }
}

@Preview
@Composable
private fun MDMigrateTagsScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDMigrateTagsScreen(
                    uiState = MDMigrateTagsMutableUiState().apply {
                        onExecute { true }
                    },
                    uiActions = MDMigrateTagsUiActions(
                        object : MDMigrateTagsNavigationUiActions, MDAppNavigationUiActions {
                            override fun onOpenNavDrawer() {}
                            override fun onCloseNavDrawer() {}

                        },
                        object : MDMigrateTagsBusinessUiActions {},
                    )
                )
            }
        }
    }
}
