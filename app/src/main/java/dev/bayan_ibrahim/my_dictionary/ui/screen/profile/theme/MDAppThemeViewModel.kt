package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.repo.UserPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeContrast
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDAppThemeViewModel @Inject constructor(
    private val userRepo: UserPreferencesRepo,
) : ViewModel() {
    private val _uiState: MDAppThemeMutableUiState = MDAppThemeMutableUiState()
    val uiState: MDAppThemeUiState = _uiState

    fun initWithNavArgs(
        args: MDDestination.AppTheme,
        context: Context,
    ) {
        viewModelScope.launch {
            _uiState.onExecute {
                _uiState.themes = MDTheme.entries.associateWith { theme ->
                    val lightThemes = theme.getVarianceContrasts(false).asThemeData(context)
                    val darkThemes = theme.getVarianceContrasts(true).asThemeData(context)
                    Pair(
                        first = lightThemes,
                        second = darkThemes
                    )
                }.toPersistentMap()
                true
            }
        }
    }

    fun onIsSystemDarkThemeChanged(
        isSystemDarkTheme: Boolean,
    ) {
        viewModelScope.launch {
            userRepo.getUserPreferences().let {
                _uiState.selectedTheme = it.theme
                _uiState.selectedContrast = it.theme.getContrast(
                    contrast = it.themeContrastType,
                    isDark = isSystemDarkTheme
                )
            }
        }
    }

    private suspend fun List<MDThemeContrast>.asThemeData(
        context: Context,
    ): MDThemeCardData = associateWith {
        it.buildColorScheme(context).identifierTriple()
    }

    fun getUiActions(
        navActions: MDAppThemeNavigationUiActions,
    ): MDAppThemeUiActions = MDAppThemeUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDAppThemeNavigationUiActions,
    ): MDAppThemeBusinessUiActions = object : MDAppThemeBusinessUiActions {
        override fun onClickContrast(theme: MDTheme, contrast: MDThemeContrast) {
            viewModelScope.launch {
                _uiState.selectedTheme = theme
                _uiState.selectedContrast = contrast
                userRepo.setUserPreferences {
                    it.copy(
                        theme = theme,
                        themeVariant = contrast.variant,
                        themeContrastType = contrast.type
                    )
                }
            }
        }
    }
}
