package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDVerticalCard
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme.MDThemeCardData
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeContrast
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeContrastType

typealias MDThemeCardIdentifier = Triple<Color, Color, Color>

data object MDThemeCardDefaults {
    // 3 * 48 + 2 * 16
    val rowWidth = 176.dp
    val cardWidth = rowWidth + 16.dp
}

@Composable
fun MDThemeCard(
    theme: MDTheme,
    /**
     * unique identifier for selected theme
     */
    selectedTheme: MDTheme,
    selectedContrast: MDThemeContrast,
    lightVariants: MDThemeCardData,
    darkVariants: MDThemeCardData,
    onClickContrast: (MDThemeContrast) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentThemeSelectedContrast by remember(selectedTheme, selectedContrast) {
        derivedStateOf {
            if (selectedTheme == theme) {
                selectedContrast
            } else {
                null
            }
        }
    }
    val isSystemDarkTheme = isSystemInDarkTheme()
    val similarPrimaryColor by remember(selectedContrast, darkVariants, lightVariants, isSystemDarkTheme) {
        derivedStateOf {
            if (selectedContrast.isDark ?: isSystemDarkTheme) {
                darkVariants
            } else {
                lightVariants
            }.firstNotNullOfOrNull {
                if (it.key.isSimilar(selectedContrast)) {
                    it.value.second
                } else {
                    null
                }
            }
        }
    }
    MDVerticalCard(
        modifier = modifier.width(IntrinsicSize.Min),
        footerModifier = Modifier,
        contentModifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = MDCardDefaults.colors(
            headerContainerColor = similarPrimaryColor ?:  MaterialTheme.colorScheme.primaryContainer,
            contentContainerColor = MaterialTheme.colorScheme.background
        ),
        header = {
            Text(theme.label)
        }
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MDThemeCardRow(
                variants = lightVariants,
                selectedContrast = currentThemeSelectedContrast,
                onClick = onClickContrast,
            )
            MDThemeCardRow(
                variants = darkVariants,
                selectedContrast = currentThemeSelectedContrast,
                onClick = onClickContrast
            )
        }
    }
}

@Composable
private fun MDThemeCardRow(
    variants: Map<MDThemeContrast, MDThemeCardIdentifier>,
    onClick: (MDThemeContrast) -> Unit,
    selectedContrast: MDThemeContrast?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val lastIndex by remember(variants) {
            derivedStateOf { variants.count().dec() }
        }
        variants.entries.forEachIndexed { i, (variant, colors) ->
            MDThemeContrastVariantCard(
                primaryColor = colors.first,
                primaryContainerColor = colors.second,
                surfaceContainerColor = colors.third,
                selected = variant.isSimilar(selectedContrast),
                onClick = {
                    onClick(variant)
                }
            )
            if (i != lastIndex) {
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Preview
@Composable
private fun MDThemeCardPreview() {
    MyDictionaryTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                val context = LocalContext.current
                var light: Map<MDThemeContrast, Triple<Color, Color, Color>> by remember {
                    mutableStateOf(mapOf())
                }
                var dark: Map<MDThemeContrast, Triple<Color, Color, Color>> by remember {
                    mutableStateOf(mapOf())
                }
                LaunchedEffect(Unit) {
                    light = MDTheme.Blue.getVarianceContrasts(false).associateWith {
                        it.buildColorScheme(context).identifierTriple()
                    }
                    dark = MDTheme.Blue.getVarianceContrasts(true).associateWith {
                        it.buildColorScheme(context).identifierTriple()
                    }
                }
                MDThemeCard(
                    theme = MDTheme.Blue,
                    selectedTheme = MDTheme.Blue,
                    selectedContrast = MDTheme.Blue.getContrastVariance(MDThemeContrastType.High).first,// light, high
                    lightVariants = light,
                    darkVariants = dark,
                    onClickContrast = { _ -> }
                )
            }
        }
    }
}