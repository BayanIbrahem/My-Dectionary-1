package dev.bayan_ibrahim.my_dictionary

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDUserPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.getSelectedThemeIdentifier
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.DefaultDarkColorScheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.DefaultLightColorScheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsPack
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeVariant
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val dataStore: MDPreferencesDataStore,
    @ApplicationContext
    private val context: Context,
) : ViewModel() {
    val userPreferences = dataStore.getUserPreferencesStream()
        .onStart {
            MDUserPreferences()
        }.distinctUntilChangedBy {
            it.getSelectedThemeIdentifier(false) to it.getSelectedThemeIdentifier(true)
        }.map { (_, theme, themeVariantType, themeContrastVariantType, iconsSet) ->
            val currentVariant = theme.getContrastVariance(themeContrastVariantType)
            MainActivityUiState(
                themeVariant = themeVariantType,
                lightColorScheme = currentVariant.first.buildColorScheme(context).toColorScheme(),
                darkColorScheme = currentVariant.second.buildColorScheme(context).toColorScheme(),
                iconsPack = iconsSet,
                initialized = true,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MDUserPreferences().let {
                MainActivityUiState(
                    themeVariant = MDThemeVariant.System,
                    lightColorScheme = DefaultLightColorScheme,
                    darkColorScheme = DefaultDarkColorScheme,
                    iconsPack = MDIconsPack.Default,
                    initialized = false,
                )
            }
        )
}