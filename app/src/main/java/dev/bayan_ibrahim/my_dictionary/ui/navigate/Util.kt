package dev.bayan_ibrahim.my_dictionary.ui.navigate

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

fun NavHostController.topLevelNavigate(route: MDDestination.TopLevel) = navigate(route) {
    popUpTo(graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}
