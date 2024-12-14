package dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

enum class MDThemeVariant {
    System,
    Light,
    Dark;

    fun isDarkTheme(isSystemDark: Boolean): Boolean {
        return when (this) {
            System -> isSystemDark
            Light -> false
            Dark -> true
        }

    }

    val dark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = isDarkTheme(isSystemInDarkTheme())

    companion object {
        fun of(isDark: Boolean) = if (isDark) {
            Dark
        } else {
            Light
        }
    }
}