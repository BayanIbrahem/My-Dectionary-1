package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsPack
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeContrastType
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeVariant

data class MDUserPreferences(
    val selectedLanguagePage: Language? = null,
    val liveMemorizingProbability: Boolean = false,
    val theme: MDTheme = MDTheme.Default,
    val themeVariant: MDThemeVariant = MDThemeVariant.System,
    val themeContrastType: MDThemeContrastType = MDThemeContrastType.Normal,
    val iconsPack: MDIconsPack = MDIconsPack.Default,
)

/**
 * if the value of [MDThemeVariant] is  [MDThemeVariant.System] then we use [isSystemDarkTheme]
 * to calculate the dark or light theme
 */
fun MDUserPreferences.getSelectedThemeIdentifier(
    isSystemDarkTheme: Boolean,
): String {
    return theme.identifierOf(
        variant = themeVariant,
        contrast = themeContrastType,
        isSystemDarkTheme = isSystemDarkTheme
    )
}