package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    /** fill color of icon */
    fill: Color? = null,
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
    val modifierWithFill by remember(fill, modifier) {
        derivedStateOf {
            if(fill == null) {
                modifier
            } else {
                modifier.clip(CircleShape).background(fill)
            }
        }
    }
    val animatedTint by animateColorAsState(tint)
    Icon(
        painter = builder(),
        contentDescription = contentDescription?.let {
            it.ifBlank {
                currentIcon.contentDescriptionValue
            }
        },
        modifier = modifierWithFill,
        tint = animatedTint,
    )
}