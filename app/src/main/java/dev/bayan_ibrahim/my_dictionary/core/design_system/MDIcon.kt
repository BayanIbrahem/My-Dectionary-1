package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.contentDescriptionValue
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.currentIcon
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.filledPainter
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.outlinedPainter

@Composable
fun MDIcon(
    icon: MDIconsSet,
    modifier: Modifier = Modifier,
    outline: Boolean = true,
    tint: Color = LocalContentColor.current,
    /**
     * pass null to the content description to disable it or leave it blank screen to get default icon description
     */
    contentDescription: String? = "",
) {
    val currentIcon = icon.currentIcon
    val builder: @Composable () -> Painter by remember(icon, outline) {
        derivedStateOf {
            if (outline) {
                {
                    currentIcon.outlinedPainter
                }
            } else {
                {
                    currentIcon.filledPainter
                }
            }
        }
    }
    Icon(
        painter = builder(),
        contentDescription = contentDescription?.let {
            it.ifBlank {
                currentIcon.contentDescriptionValue
            }
        },
        modifier = modifier,
        tint = tint,
    )
}