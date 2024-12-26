package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipScope
import androidx.compose.material3.TooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDTooltipBox(
    tooltip: @Composable TooltipScope.() -> Unit,
    state: TooltipState,
    modifier: Modifier = Modifier,
    density: Density = LocalDensity.current,
    verticalOffset: Dp = 4.dp,
    focusable: Boolean = true,
    enableUserInput: Boolean = true,
    content: @Composable () -> Unit,
) {
    val provider by remember {
        derivedStateOf {
            MDToolTipPositionProvider(
                density = density,
                verticalOffset = verticalOffset
            )
        }
    }
    TooltipBox(
        positionProvider = provider,
        tooltip = tooltip,
        state = state,
        modifier = modifier,
        focusable = focusable,
        enableUserInput = enableUserInput,
        content = content
    )
}

data class MDToolTipPositionProvider(
    val density: Density,
    val verticalOffset: Dp = 4.dp,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {

        // Calculate the vertical offset
        val verticalOffsetPx = with(density) { verticalOffset.toPx() }

        // Determine the horizontal center of the anchor
        val anchorCenterX = anchorBounds.left + (anchorBounds.width / 2)

        // Calculate the horizontal position of the popup
        val popupX = anchorCenterX - (popupContentSize.width / 2)

        // Constrain the popup to the window bounds horizontally
        val constrainedX = popupX.coerceIn(
            minimumValue = 0,
            maximumValue = windowSize.width - popupContentSize.width
        )

        // Calculate potential top and bottom positions
        val potentialTopY = anchorBounds.top - popupContentSize.height - verticalOffsetPx
        val potentialBottomY = anchorBounds.bottom + verticalOffsetPx

        // Determine the vertical position based on available space
        val popupY = when {
            potentialTopY >= 0 -> potentialTopY.toInt() // Position at the top if there's enough space
            potentialBottomY + popupContentSize.height <= windowSize.height -> potentialBottomY.toInt() // Position at the bottom if there's enough space
            else -> {
                // If neither top nor bottom has enough space, position to intersect with the composable
                val topSpace = anchorBounds.top
                val bottomSpace = windowSize.height - anchorBounds.bottom
                if (topSpace > bottomSpace) {
                    // More space above, adjust top position
                    (anchorBounds.top - popupContentSize.height).coerceAtLeast(0)
                } else {
                    // More space below, adjust bottom position
                    (anchorBounds.bottom + verticalOffsetPx).coerceAtMost((windowSize.height - popupContentSize.height).toFloat()).roundToInt()
                }
            }
        }

        return IntOffset(constrainedX, popupY)
    }
}