package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination

@Composable
fun MDAppThemeRoute(
    args: MDDestination.AppTheme,
    modifier: Modifier = Modifier,
    viewModel: MDAppThemeViewModel = hiltViewModel(),

) {
    val context= LocalContext.current
    LaunchedEffect(args) {
        viewModel.initWithNavArgs(args, context)
    }
    val isSystemDarkTheme = isSystemInDarkTheme()
    LaunchedEffect(isSystemDarkTheme) {
        viewModel.onIsSystemDarkThemeChanged(isSystemDarkTheme)
    }

    val uiState = viewModel.uiState
    val navActions by remember {
        derivedStateOf {
            object : MDAppThemeNavigationUiActions {
            }
        }
    }
    val uiActions by remember {
        derivedStateOf {
            viewModel.getUiActions(navActions)
        }
    }
    MDAppThemeScreen(
        uiState = uiState,
        uiActions = uiActions,
        modifier = modifier,
    )
}
