package dev.bayan_ibrahim.my_dictionary.ui.navigate

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2SelectableItem
import dev.bayan_ibrahim.my_dictionary.core.ui.getLogoPainter
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlin.reflect.KClass

@Composable
fun MDNavigationDrawer(
    currentDestination: KClass<out MDDestination>?,
    onNavigateTo: (MDDestination) -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    scrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DrawerHeader()
                    DrawerContent(
                        currentDestination = currentDestination,
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        onNavigateTo = onNavigateTo,
                    )
                    DrawerFooter()
                }
            }
        },
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        scrimColor = scrimColor,
        content = content
    )
}

@Composable
private fun DrawerHeader(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = MDIconsSet.getLogoPainter(
                    dark = true,
                    simple = true,
                    colorful = false,
                ),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge
            )
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
    }
}

@Composable
private fun DrawerContent(
    currentDestination: KClass<out MDDestination>?,
    modifier: Modifier = Modifier,
    onNavigateTo: (MDDestination) -> Unit,
) {
    Column(modifier = modifier) {
        DrawerContentMainGroup(
            currentDestination = currentDestination,
            onNavigate = onNavigateTo,
        )
        DrawerContentToolsGroup(
            currentDestination = currentDestination,
            onNavigate = onNavigateTo,
        )
        DrawerContentMigrationGroup(
            currentDestination = currentDestination,
            onNavigate = onNavigateTo
        )
    }
}

@Composable
private fun DrawerContentMainGroup(
    currentDestination: KClass<out MDDestination>?,
    modifier: Modifier = Modifier,
    onNavigate: (MDDestination.TopLevel) -> Unit,
) {
    MDDrawerContentGroup(
        modifier = modifier,
        hasDivider = false,
    ) {
        MDDestination.TopLevel.Enum.entries.forEachIndexed { i, topLevel ->
            ContentGroupListItem(
                label = topLevel.label,
                leadingIcon = topLevel.icon,
                associatedDestination = topLevel.route::class,
                currentDestination = currentDestination,
                onClick = { onNavigate(topLevel.route) }
            )
        }
    }
}

@Composable
private fun DrawerContentToolsGroup(
    currentDestination: KClass<out MDDestination>?,
    modifier: Modifier = Modifier,
    onNavigate: (MDDestination) -> Unit,
) {
    MDDrawerContentGroup(
        modifier = modifier,
        title = firstCapStringResource(R.string.tools)
    ) {
        ContentGroupListItem(
            label = firstCapStringResource(R.string.marker_tags),
            leadingIcon = MDIconsSet.Colors,
            currentDestination = currentDestination,
            associatedDestination = MDDestination.MarkerTags::class,
            onClick = {
                onNavigate(MDDestination.MarkerTags)
            }
        )
    }
}

@Composable
private fun DrawerContentMigrationGroup(
    currentDestination: KClass<out MDDestination>?,
    onNavigate: (MDDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
//    MDDrawerContentGroup(
//        title = firstCapStringResource(R.string.migration),
//        modifier = modifier,
//    ) {
//        ContentGroupListItem(
//            label = firstCapStringResource(R.string.migrate_tags),
//            leadingIcon = MDIconsSet.WordTag,// TODO, icon res
//            currentDestination = currentDestination,
//            associatedDestination = MDDestination.MigrateTags::class,
//            onClick = {
//                onNavigate(MDDestination.MigrateTags)
//            },
//        )
//
//        ContentGroupListItem(
//            label = firstCapStringResource(R.string.migrate_similar_words),
//            leadingIcon = MDIconsSet.WordMeaning, // TODO, icon res
//            currentDestination = currentDestination,
//            associatedDestination = MDDestination.MigrateSimilarWords::class,
//            onClick = {
//                onNavigate(MDDestination.MigrateSimilarWords)
//            },
//        )
//    }
}

@Composable
private fun MDDrawerContentGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    hasDivider: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        if (title != null) {
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
        if (hasDivider) {
            HorizontalDivider(modifier = Modifier.width(120.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            content()
        }
    }

}

@Composable
private fun ContentGroupListItem(
    label: String,
    leadingIcon: MDIconsSet,
    /**
     * would only be called if it is not selected ([currentDestination] != [associatedDestination])
     */
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentDestination: KClass<out MDDestination>? = null,
    associatedDestination: KClass<out MDDestination>? = null,
    enabled: Boolean = true,
) {
    val selected by remember(currentDestination, associatedDestination) {
        derivedStateOf {
            associatedDestination != null && currentDestination == associatedDestination
        }
    }
    val onNonSelectedClick: (() -> Unit)? by remember(enabled, selected) {
        derivedStateOf {
            if (enabled) {
                {
                    if (!selected) onClick()
                }
            } else {
                null
            }
        }
    }
    val opacity by animateFloatAsState(if (enabled) 1f else 0.38f)
    MDCard2SelectableItem(
        checked = selected,
        clip=   true,
        onClick = {
            onNonSelectedClick?.invoke()
        },
        modifier = modifier.graphicsLayer { alpha = opacity },
        theme = MDCard2ListItemTheme.SurfaceContainerHighest,
        checkedTheme = MDCard2ListItemTheme.PrimaryContainer,
        leading = {
            MDIcon(leadingIcon, contentDescription = null)
        },
        title = label,
    )
}

/** app name with version */
@Composable
private fun DrawerFooter(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(modifier = Modifier.padding(start = 24.dp))
        Text(
            text = "${stringResource(R.string.app_name)} v ${Build.VERSION.RELEASE}",
            modifier = Modifier.align(Alignment.End),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(device = "id:pixel_9")
@Composable
private fun MDNavigationDrawerPreview() {
    MyDictionaryTheme {
        MDNavigationDrawer(
            currentDestination = MDDestination.TopLevel.WordSpace::class,
            onNavigateTo = {},
            drawerState = rememberDrawerState(DrawerValue.Open)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {

                }
            }
        }
    }
}