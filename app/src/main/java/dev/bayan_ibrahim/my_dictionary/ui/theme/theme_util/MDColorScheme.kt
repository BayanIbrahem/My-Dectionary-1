package dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class MDColorScheme(
    val primary: String,
    val onPrimary: String,
    val primaryContainer: String,
    val onPrimaryContainer: String,
    val inversePrimary: String,
    val secondary: String,
    val onSecondary: String,
    val secondaryContainer: String,
    val onSecondaryContainer: String,
    val tertiary: String,
    val onTertiary: String,
    val tertiaryContainer: String,
    val onTertiaryContainer: String,
    val background: String,
    val onBackground: String,
    val surface: String,
    val onSurface: String,
    val surfaceVariant: String,
    val onSurfaceVariant: String,
    val surfaceTint: String,
    val inverseSurface: String,
    val inverseOnSurface: String,
    val error: String,
    val onError: String,
    val errorContainer: String,
    val onErrorContainer: String,
    val outline: String,
    val outlineVariant: String,
    val scrim: String,
    val surfaceBright: String,
    val surfaceDim: String,
    val surfaceContainer: String,
    val surfaceContainerHigh: String,
    val surfaceContainerHighest: String,
    val surfaceContainerLow: String,
    val surfaceContainerLowest: String,
) {
    fun toColorScheme() = ColorScheme(
        primary.toColor(),
        onPrimary.toColor(),
        primaryContainer.toColor(),
        onPrimaryContainer.toColor(),
        inversePrimary.toColor(),
        secondary.toColor(),
        onSecondary.toColor(),
        secondaryContainer.toColor(),
        onSecondaryContainer.toColor(),
        tertiary.toColor(),
        onTertiary.toColor(),
        tertiaryContainer.toColor(),
        onTertiaryContainer.toColor(),
        background.toColor(),
        onBackground.toColor(),
        surface.toColor(),
        onSurface.toColor(),
        surfaceVariant.toColor(),
        onSurfaceVariant.toColor(),
        surfaceTint.toColor(),
        inverseSurface.toColor(),
        inverseOnSurface.toColor(),
        error.toColor(),
        onError.toColor(),
        errorContainer.toColor(),
        onErrorContainer.toColor(),
        outline.toColor(),
        outlineVariant.toColor(),
        scrim.toColor(),
        surfaceBright.toColor(),
        surfaceDim.toColor(),
        surfaceContainer.toColor(),
        surfaceContainerHigh.toColor(),
        surfaceContainerHighest.toColor(),
        surfaceContainerLow.toColor(),
        surfaceContainerLowest.toColor(),
    )

    fun identifierTriple(): Triple<Color, Color, Color> = Triple(
        first = primary.toColor(),
        second = primaryContainer.toColor(),
        third = surfaceContainer.toColor(),
    )
}

/**
 * must the color be # follwoed by 6 chars in hex
 */
private fun String.toColor(): Color {
    val colorLong = this.replaceFirst("#", "FF").toLong(16)
    return Color(colorLong)
}