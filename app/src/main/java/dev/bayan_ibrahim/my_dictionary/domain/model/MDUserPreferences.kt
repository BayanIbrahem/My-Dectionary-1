package dev.bayan_ibrahim.my_dictionary.domain.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.upperStringResource
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsPack
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeContrastType
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeVariant
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

data class MDUserPreferences(
    val selectedLanguagePage: Language? = null,
    val liveMemorizingProbability: Boolean = false,
    val theme: MDTheme = MDTheme.Default,
    val themeVariant: MDThemeVariant = MDThemeVariant.System,
    val themeContrastType: MDThemeContrastType = MDThemeContrastType.Normal,
    val iconsPack: MDIconsPack = MDIconsPack.Default,
    val wordDetailsDirectionSource: WordDetailsDirectionSource = WordDetailsDirectionSource.WordLanguage,
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

enum class WordDetailsDirectionSource(override val icon: MDIconsSet) : LabeledEnum, IconedEnum {
    Ltr( MDIconsSet.Ltr),
    Rtl(MDIconsSet.Rtl),
    Device(MDIconsSet.DeviceDirection),
    WordLanguage( MDIconsSet.LanguageWordSpace);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = when(this) {
            Ltr -> upperStringResource(R.string.ltr)
            Rtl -> upperStringResource(R.string.rtl)
            Device -> upperStringResource(R.string.device_locale)
            WordLanguage -> upperStringResource(R.string.language_locale)
        }

    val current: LayoutDirection?
        @Composable
        @ReadOnlyComposable
        get() = if (this == Device) {
            LocalLayoutDirection.current
        } else {
            null
        }
}