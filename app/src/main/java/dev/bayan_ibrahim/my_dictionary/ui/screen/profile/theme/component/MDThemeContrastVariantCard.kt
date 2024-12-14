package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.MyDictionaryTheme


@Suppress("InfiniteTransitionLabel", "InfinitePropertiesLabel", "AnimateAsStateLabel")
@Composable
fun MDThemeContrastVariantCard(
    primaryColor: Color,
    primaryContainerColor: Color,
    surfaceContainerColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    selected: Boolean = true,
    radius: Dp = 8.dp,
    size: Dp = 48.dp,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerPosition by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, delayMillis = 1000), RepeatMode.Restart)
    )
    Box(
        modifier = modifier
            .size(size)
            .drawBehind {
                val radiusPx = radius.toPx()
                val cornerRadius = CornerRadius(radiusPx)
                drawRoundRect(
                    surfaceContainerColor,
                    cornerRadius = cornerRadius
                )
                val width = this.size.width - radiusPx.times(2)
                val heightSum = this.size.height - radiusPx.times(3)
                drawRoundRect(
                    color = primaryContainerColor,
                    cornerRadius = CornerRadius(radiusPx.div(2)),
                    topLeft = Offset(radiusPx, radiusPx),
                    size = Size(width, heightSum - radiusPx)
                )
                drawRoundRect(
                    color = primaryColor,
                    cornerRadius = cornerRadius,
                    topLeft = Offset(radiusPx, heightSum + radiusPx),
                    size = Size(width, radiusPx)
                )
                val t = this.size
                    .times(shimmerPosition)
                    .toRect().bottomRight
                drawRoundRect(
                    cornerRadius = cornerRadius,
                    brush = Brush.linearGradient(
                        0f to Color.Transparent,
                        0.5f to surfaceContainerColor,
                        1f to Color.Transparent,
                        start = Offset(0f, 0f) + t,
                        end = Offset(this.size.width, this.size.height) + t
                    ),
                    alpha = 0.75f
                )
            },
        contentAlignment = Alignment.BottomEnd
    ) {
        onClick?.let {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(radius))
                    .clickable(onClick = onClick),
            )
        }
        val sizeScale by animateFloatAsState(if (selected) 1f else 0f)
        Icon(
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer {
                    translationX = 4.dp.toPx()
                    translationY = 4.dp.toPx()
                    scaleX = sizeScale
                    scaleY = sizeScale
                },
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = primaryColor
        )
    }
}

@Preview
@Composable
private fun MDThemeContrastVariantCardPreview() {
    MyDictionaryTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                var selected by remember {
                    mutableStateOf(false)
                }
                MDThemeContrastVariantCard(
                    primaryColor = MaterialTheme.colorScheme.primary,
                    primaryContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    surfaceContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.clickable {
                        selected = !selected
                    },
                    selected = selected,
                )
            }
        }
    }
}