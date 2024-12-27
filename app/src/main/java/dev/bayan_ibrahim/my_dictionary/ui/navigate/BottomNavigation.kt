package dev.bayan_ibrahim.my_dictionary.ui.navigate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon

@Composable
fun MDBottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Row(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        MDDestination.TopLevel.Enum.entries.forEach { topLevelRoute ->
            val selected by remember(currentDestination?.hierarchy) {
                derivedStateOf {
                    currentDestination?.hierarchy?.any {
                        it.hasRoute(topLevelRoute.route::class)
                    } == true
                }
            }
            NavigationBarItem(
                selected = selected,
                alwaysShowLabel = false,
                icon = {
                    MDIcon(
                        icon = topLevelRoute.icon,
                        outline = !selected,
                    )
                },
                onClick = {
                    navController.topLevelNavigate(topLevelRoute.route)
                }
            )
        }
    }

}