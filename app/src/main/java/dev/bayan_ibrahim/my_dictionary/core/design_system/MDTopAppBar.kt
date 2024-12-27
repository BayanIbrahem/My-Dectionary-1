package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import dev.bayan_ibrahim.my_dictionary.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDTopAppBar(
    title: @Composable () -> Unit,
    isTopLevel: Boolean,
    /** open drawer if [isTopLevel] else navigate back */
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal),
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            if (navigationIcon != null) {
                navigationIcon()
            } else {
                IconButton(
                    onClick = onNavigationIconClick
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isTopLevel) {
                                R.drawable.menu
                            } else {
                                R.drawable.arrow_back
                            }
                        ), // TODO, icon res
                        contentDescription = null
                    )
                }
            }
        },
        actions = actions,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}