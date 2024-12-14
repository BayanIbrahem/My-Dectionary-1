package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme

import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeContrast


interface MDAppThemeBusinessUiActions {
    fun onClickContrast(theme: MDTheme, contrast: MDThemeContrast)
}

interface MDAppThemeNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDAppThemeUiActions(
    navigationActions: MDAppThemeNavigationUiActions,
    businessActions: MDAppThemeBusinessUiActions,
) : MDAppThemeBusinessUiActions by businessActions, MDAppThemeNavigationUiActions by navigationActions