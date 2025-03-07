package dev.bayan_ibrahim.my_dictionary.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.painter.Painter
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.currentFilledPainter
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.currentOutlinedPainter

interface LabeledEnum {
    @get:Composable
    @get:ReadOnlyComposable
    val label: String
}

@Suppress("unused")
interface IconedEnum {
    val icon: MDIconsSet
    val outline: Boolean
        get() = true
}

val IconedEnum.currentPainter: Painter
    @Composable
    get() = if (outline) {
        icon.currentOutlinedPainter
    } else {
        icon.currentFilledPainter
    }
