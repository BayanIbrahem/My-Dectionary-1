package dev.bayan_ibrahim.my_dictionary.ui.navigate

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
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
import dev.bayan_ibrahim.my_dictionary.core.util.removePadding
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.currentFilledPainter
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.currentOutlinedPainter

@Composable
fun MDBottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar(
        modifier = modifier.height(42.dp)
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
                icon = {
                    Icon(
                        painter = if (selected) topLevelRoute.icon.currentOutlinedPainter else topLevelRoute.icon.currentFilledPainter,
                        contentDescription = null
                    )
                },
                onClick = {
                    navController.topLevelNavigate(topLevelRoute.route)
                }
            )
        }
    }

}