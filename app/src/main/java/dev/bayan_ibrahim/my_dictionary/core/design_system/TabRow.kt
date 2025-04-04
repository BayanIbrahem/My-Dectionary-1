package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2ActionRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2SelectableAction
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet


@Composable
fun <K : Any> MDTabRow(
    tabs: List<MDTabData<K>>,
    selectedTabIndex: Int,
    onClickTab: (i: Int, key: K?) -> Unit,
    modifier: Modifier = Modifier,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    normalTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.SurfaceContainer,
    selectedTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.PrimaryContainer,
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

    MDCard2ActionRow {
        tabs.forEachIndexed { i, data ->
            if (data.icon != null)
                MDCard2SelectableAction(
                    modifier = modifier.weight(1f),
                    label = data.label,
                    icon = { MDIcon(data.icon!!) },
//                data = data,
                    selected = i == selectedTab,
                    enabled = isTabEnabled(i, data.key),
                    normalTheme = normalTheme,
                    selectedTheme = selectedTheme,
                    onClick = {
                        onClickTab(i, data.key)
                    }
                )
            else if (data.label != null) {
                MDCard2SelectableAction(
                    modifier = modifier.weight(1f),
                    label = data.label!!,
                    selected = i == selectedTab,
                    enabled = isTabEnabled(i, data.key),
                    normalTheme = normalTheme,
                    selectedTheme = selectedTheme,
                    onClick = {
                        onClickTab(i, data.key)
                    }
                )
            }
        }
    }
}

sealed interface MDTabData<K : Any> {
    val label: String?
    val icon: MDIconsSet?
    val outlinedIcon: Boolean
        get() = true
    val key: K?

    data class Label<K : Any>(
        override val label: String,
        override val key: K? = null,
    ) : MDTabData<K> {
        override val icon: MDIconsSet? = null
    }

    data class Icon<K : Any>(
        override val icon: MDIconsSet,
        override val outlinedIcon: Boolean = true,
        override val key: K? = null,
    ) : MDTabData<K> {
        override val label: String? = null
    }

    data class LabelWithIcon<K : Any>(
        override val label: String,
        override val icon: MDIconsSet,
        override val outlinedIcon: Boolean = true,
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
                        MDTabData.Icon(MDIconsSet.WordsList),
                        MDTabData.LabelWithIcon("label with icon", MDIconsSet.WordsList),
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