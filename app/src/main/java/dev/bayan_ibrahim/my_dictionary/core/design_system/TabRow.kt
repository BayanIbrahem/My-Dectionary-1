package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme


data object MDTabRowDefaults {
    val leadingEdgeAnimation: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessHigh,
    )
    val trailingEdgeAnimation: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow,
    )

    @Composable
    fun colors(
        indicatorColor: Color = MaterialTheme.colorScheme.onPrimary,
        containerColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = MaterialTheme.colorScheme.onPrimary,
        selectedContentColor: Color = MaterialTheme.colorScheme.primary,
    ): MDTabRowColors = MDTabRowColors(
        indicatorColor = indicatorColor,
        containerColor = containerColor,
        contentColor = contentColor,
        selectedContentColor = selectedContentColor,
    )

    @Composable
    fun colorsPrimary() = colors()

    @Composable
    fun colorsSecondary() = MDTabRowColors(
        indicatorColor = MaterialTheme.colorScheme.onSecondary,
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        selectedContentColor = MaterialTheme.colorScheme.secondary,
    )
}

data class MDTabRowColors(
    val indicatorColor: Color,
    val containerColor: Color,
    val contentColor: Color,
    val selectedContentColor: Color,
)

@Composable
fun <K : Any> MDTabRow(
    tabs: List<MDTabData<K>>,
    selectedTabIndex: Int,
    onClickTab: (i: Int, key: K?) -> Unit,
    modifier: Modifier = Modifier,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    leadingEdgeAnimation: FiniteAnimationSpec<Float> = MDTabRowDefaults.leadingEdgeAnimation,
    trailingEdgeAnimation: FiniteAnimationSpec<Float> = MDTabRowDefaults.trailingEdgeAnimation,
    colors: MDTabRowColors = MDTabRowDefaults.colors(),
    isTabEnabled: (i: Int, key: K?) -> Boolean = { _, _ -> true },
) {
    val tabsCount by remember {
        derivedStateOf { tabs.count() }
    }
    var isMovingForward by remember {
        mutableStateOf(true)
    }
    var selectedTab by remember {
        mutableIntStateOf(selectedTabIndex)
    }

    LaunchedEffect(selectedTabIndex) {
        isMovingForward = selectedTab < selectedTabIndex
        selectedTab = selectedTabIndex
    }

    val transition = updateTransition(selectedTab, "offset transition")
    val indicatorStartOffset by transition.animateFloat(
        transitionSpec = {
            if (isMovingForward) {
                trailingEdgeAnimation
            } else {
                leadingEdgeAnimation
            }
        },
        label = "start",
    ) {
        it.toFloat().div(tabsCount)
    }

    val indicatorEndOffset by transition.animateFloat(
        transitionSpec = {
            if (isMovingForward) {
                leadingEdgeAnimation
            } else {
                trailingEdgeAnimation
            }
        },
        label = "start",
    ) {
        it.inc().toFloat().div(tabsCount)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val scale = when (layoutDirection) {
                    LayoutDirection.Ltr -> 1f
                    LayoutDirection.Rtl -> -1f
                }
                scale(scale) {
                    drawRect(colors.containerColor)
                    val width = (indicatorEndOffset - indicatorStartOffset) * size.width
                    drawRect(
                        colors.indicatorColor,
                        topLeft = Offset(indicatorStartOffset * size.width, 0f),
                        size = Size(width, size.height)
                    )
                }
            }
    ) {
        tabs.forEachIndexed { i, data ->
            MDTab(
                data = data,
                selected = i == selectedTab,
                enabled = isTabEnabled(i, data.key),
                contentColor = colors.contentColor,
                selectedContentColor = colors.selectedContentColor,
                onClick = {
                    onClickTab(i, data.key)
                }
            )
        }
    }
}

@Composable
fun <K : Any> RowScope.MDTab(
    data: MDTabData<K>,
    selected: Boolean,
    contentColor: Color,
    selectedContentColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val color by animateColorAsState(
        targetValue = if (selected) selectedContentColor else contentColor,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "content color"
    )
    val alpha by animateFloatAsState(
        targetValue = if (enabled || selected) 1f else 0.38f,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "content alpha"
    )
    Row(
        modifier = modifier
            .heightIn(min = 40.dp, 52.dp)
            .weight(1f)
            .graphicsLayer {
                this.alpha = alpha
            }
            .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        data.icon?.let { icon ->
            Icon(imageVector = icon, contentDescription = null, tint = color)
        }
        data.label?.let { label ->
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
}

sealed interface MDTabData<K : Any> {
    val label: String?
    val icon: ImageVector?
    val key: K?

    data class Label<K : Any>(
        override val label: String,
        override val key: K? = null,
    ) : MDTabData<K> {
        override val icon: ImageVector? = null
    }

    data class Icon<K : Any>(
        override val icon: ImageVector,
        override val key: K? = null,
    ) : MDTabData<K> {
        override val label: String? = null
    }

    data class LabelWithIcon<K : Any>(
        override val label: String,
        override val icon: ImageVector,
        override val key: K? = null,
    ) : MDTabData<K>
}

@Preview
@Composable
private fun MDTabRowPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            var selectedTabIndex by remember {
                mutableIntStateOf(0)
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDTabRow(
                    tabs = listOf(
                        MDTabData.Label("label only"),
                        MDTabData.Icon(Icons.Default.Home),
                        MDTabData.LabelWithIcon("label with icon", Icons.Default.Home),
                    ),
                    selectedTabIndex = selectedTabIndex,
                    onClickTab = { i, _ ->
                        selectedTabIndex = i
                    },
                    isTabEnabled = { i, _ ->
                        (i + selectedTabIndex) % 2 == 1
                    }
                )
            }
        }
    }
}