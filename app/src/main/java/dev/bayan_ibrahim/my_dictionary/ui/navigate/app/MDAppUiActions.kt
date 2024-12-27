package dev.bayan_ibrahim.my_dictionary.ui.navigate.app


interface MDAppBusinessUiActions {
}

interface MDAppNavigationUiActions {
    fun onOpenNavDrawer()
    fun onCloseNavDrawer() {}
    fun onPop() {}
}

@androidx.compose.runtime.Immutable
class MDAppUiActions(
    navigationActions: MDAppNavigationUiActions,
    businessActions: MDAppBusinessUiActions,
) : MDAppBusinessUiActions by businessActions, MDAppNavigationUiActions by navigationActions