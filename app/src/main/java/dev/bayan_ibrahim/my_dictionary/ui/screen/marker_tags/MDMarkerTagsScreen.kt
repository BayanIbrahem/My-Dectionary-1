package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags


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
fun MDMarkerTagsScreen(
    uiState: MDMarkerTagsUiState,
    uiActions: MDMarkerTagsUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
//       topBar = {
//           MDMarkerTagsTopAppBar()
//       },
    ) {
    }
}

@Preview
@Composable
private fun MDMarkerTagsScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDMarkerTagsScreen(
                    uiState = MDMarkerTagsMutableUiState().apply {
                        onExecute { true }
                    },
                    uiActions = MDMarkerTagsUiActions(
                        object : MDMarkerTagsNavigationUiActions, MDAppNavigationUiActions {
                            override fun onOpenNavDrawer() {}
                            override fun onCloseNavDrawer() {}

                        },
                        object : MDMarkerTagsBusinessUiActions {},
                    )
                )
            }
        }
    }
}
