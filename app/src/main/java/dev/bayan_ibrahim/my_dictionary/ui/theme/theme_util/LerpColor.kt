@file:Suppress("SpellCheckingInspection")

package dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.percentInRange

const val DEFAULT_FRACTION = 0.5f
fun Color.lerp(
    other: Color,
    fraction: Float = DEFAULT_FRACTION,
): Color {
    val r = percentInRange(fraction, red, other.red).coerceIn(0f, 1f)
    val g = percentInRange(fraction, green, other.green).coerceIn(0f, 1f)
    val b = percentInRange(fraction, blue, other.blue).coerceIn(0f, 1f)
    val a = percentInRange(fraction, alpha, other.alpha).coerceIn(0f, 1f)
    return Color(red = r, green = g, blue = b, alpha = a)
}

@Composable
fun Color.lerpSurface(fraction: Float = DEFAULT_FRACTION): Color = lerp(
    other = MaterialTheme.colorScheme.surface,
    fraction = fraction
)

@Composable
fun Color.lerpSurfaceContainer(fraction: Float = DEFAULT_FRACTION): Color = lerp(
    other = MaterialTheme.colorScheme.surfaceContainer,
    fraction = fraction
)

@Composable
fun Color.lerpOnSurface(fraction: Float = DEFAULT_FRACTION): Color = lerp(
    other = MaterialTheme.colorScheme.onSurface,
    fraction = fraction
)


@Composable
fun Color.lerpPrimary(fraction: Float = DEFAULT_FRACTION): Color = lerp(
    other = MaterialTheme.colorScheme.primary,
    fraction = fraction
)

@Composable
fun Color.lerpPrimaryContainer(fraction: Float = DEFAULT_FRACTION): Color = lerp(
    other = MaterialTheme.colorScheme.primaryContainer,
    fraction = fraction
)

@Composable
fun Color.lerpOnPrimary(fraction: Float = DEFAULT_FRACTION): Color = lerp(
    other = MaterialTheme.colorScheme.onPrimary,
    fraction = fraction
)

@Composable
fun Color.lerpSecondary(fraction: Float = DEFAULT_FRACTION): Color = lerp(
    other = MaterialTheme.colorScheme.secondary,
    fraction = fraction
)

@Composable
fun Color.lerpSecondaryContainer(fraction: Float = DEFAULT_FRACTION): Color = lerp(
    other = MaterialTheme.colorScheme.secondaryContainer,
    fraction = fraction
)

@Composable
fun Color.lerpOnSecondary(fraction: Float = DEFAULT_FRACTION): Color = lerp(
    other = MaterialTheme.colorScheme.onSecondary,
    fraction = fraction
)
