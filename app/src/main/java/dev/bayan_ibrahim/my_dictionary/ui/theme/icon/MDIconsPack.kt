package dev.bayan_ibrahim.my_dictionary.ui.theme.icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.icon_set.geDefaultIcon

val LocalIconsPack: ProvidableCompositionLocal<MDIconsPack> = compositionLocalOf { MDIconsPack.Default }

/**
 * contains several icons set
 */
enum class MDIconsPack {
    Default;

    @Composable
    fun getIcon(icon: MDIconsSet): MDIcon = when(this) {
        Default -> icon.geDefaultIcon()
    }
}