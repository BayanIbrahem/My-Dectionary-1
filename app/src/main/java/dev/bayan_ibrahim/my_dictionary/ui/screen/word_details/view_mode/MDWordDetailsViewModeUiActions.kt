package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.view_mode

import dev.bayan_ibrahim.my_dictionary.domain.model.WordDetailsDirectionSource
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions


interface MDWordDetailsViewModeBusinessUiActions {
    fun onToggleWordDetailsAlignmentSource(source: WordDetailsDirectionSource?)
}

interface MDWordDetailsViewModeNavigationUiActions: MDAppNavigationUiActions {
    fun onClickWordStatistics()
    fun onEdit()
    fun onShare()
}

@androidx.compose.runtime.Immutable
class MDWordDetailsViewModeUiActions(
    navigationActions: MDWordDetailsViewModeNavigationUiActions,
    businessActions: MDWordDetailsViewModeBusinessUiActions,
) : MDWordDetailsViewModeBusinessUiActions by businessActions, MDWordDetailsViewModeNavigationUiActions by navigationActions