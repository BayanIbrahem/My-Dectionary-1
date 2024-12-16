package dev.bayan_ibrahim.my_dictionary.ui.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeVariant

val DefaultDarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val DefaultLightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MyDictionaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DefaultDarkColorScheme
        else -> DefaultLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyDictionaryDynamicTheme(
    themeVariant: MDThemeVariant,
    darkColorScheme: ColorScheme,
    lightColorScheme: ColorScheme,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark by remember(themeVariant, isSystemDark) {
        derivedStateOf {
            themeVariant.isDarkTheme(isSystemDark)
        }
    }
    val colorScheme by remember(isDark, darkColorScheme, lightColorScheme) {
        derivedStateOf {
            if (isDark) darkColorScheme else lightColorScheme
        }
    }
    val context = LocalContext.current as ComponentActivity
    DisposableEffect(isDark, colorScheme) {
        val lightStyle = SystemBarStyle.light(
            colorScheme.surface.toArgb(),
            colorScheme.surface.toArgb(),
        )
        val darkStyle = SystemBarStyle.dark(
            colorScheme.surfaceContainer.toArgb(),
        )
        context.enableEdgeToEdge(
            statusBarStyle = if (!isDark) {
                lightStyle
            } else {
                darkStyle
            },
            navigationBarStyle = if (!isDark) {
                lightStyle
            } else {
                darkStyle
            }
        )

        onDispose { }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}