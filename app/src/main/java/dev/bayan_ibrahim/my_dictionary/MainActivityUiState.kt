package dev.bayan_ibrahim.my_dictionary

import androidx.compose.material3.ColorScheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsPack
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeVariant


data class MainActivityUiState(
    val themeVariant: MDThemeVariant,
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme,
    val iconsPack: MDIconsPack,
    val initialized: Boolean,
)
