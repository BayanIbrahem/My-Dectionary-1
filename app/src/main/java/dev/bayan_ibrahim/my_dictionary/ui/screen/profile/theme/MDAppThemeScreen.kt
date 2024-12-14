package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.ui.MDScreen
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme.component.MDAppThemeTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme.component.MDThemeCard
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme.component.MDThemeCardDefaults
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeContrast

@Composable
fun MDAppThemeScreen(
    uiState: MDAppThemeUiState,
    uiActions: MDAppThemeUiActions,
    modifier: Modifier = Modifier,
) {
    MDScreen(
        uiState = uiState,
        modifier = modifier,
        contentWindowInsets = WindowInsets(8.dp, 8.dp, 8.dp, 8.dp),
        topBar = {
            MDAppThemeTopAppBar()
        },
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(MDThemeCardDefaults.cardWidth),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            uiState.themes.forEach { (theme, data) ->
                item {
                    MDThemeCard(
                        theme = theme,
                        selectedTheme = uiState.selectedTheme,
                        selectedContrast = uiState.selectedContrast,
                        lightVariants = data.first,
                        darkVariants = data.second,
                        onClickContrast = { contrast ->
                            uiActions.onClickContrast(theme, contrast)
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MDAppThemeScreenPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDAppThemeScreen(
                    uiState = MDAppThemeMutableUiState().apply {
                        onExecute { true }
                    },
                    uiActions = MDAppThemeUiActions(
                        object : MDAppThemeNavigationUiActions {
                        },
                        object : MDAppThemeBusinessUiActions {
                            override fun onClickContrast(theme: MDTheme, contrast: MDThemeContrast) {}
                        },
                    )
                )
            }
        }
    }
}
