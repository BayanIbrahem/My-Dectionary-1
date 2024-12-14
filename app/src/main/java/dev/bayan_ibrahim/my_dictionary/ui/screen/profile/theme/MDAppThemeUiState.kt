package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme.component.MDThemeCardIdentifier
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeContrast
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeContrastType
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

typealias MDThemeCardData = Map<MDThemeContrast, MDThemeCardIdentifier>

interface MDAppThemeUiState : MDUiState {
    val selectedTheme: MDTheme
    val selectedContrast: MDThemeContrast
    val themes: PersistentMap<MDTheme, Pair<MDThemeCardData, MDThemeCardData>>
}

class MDAppThemeMutableUiState : MDAppThemeUiState, MDMutableUiState() {
    override var selectedTheme: MDTheme by mutableStateOf(MDTheme.Default)
    override var selectedContrast: MDThemeContrast by mutableStateOf(MDTheme.Default.getContrastVariance(MDThemeContrastType.Normal).first)
    override var themes: PersistentMap<MDTheme, Pair<MDThemeCardData, MDThemeCardData>> = persistentMapOf()
}
