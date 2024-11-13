package dev.bayan_ibrahim.my_dictionary.ui.navigate

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.rememberNavController

@Composable
fun MDApp(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    val navBackState by navController.currentBackStackEntryFlow.collectAsStateWithLifecycle(initialValue = null)
    val showBottomBar by remember(navBackState) {
        derivedStateOf {
            navBackState?.destination?.let {
                MDDestination.TopLevel.Enum.entries.any { mdDestination ->
                    it.hasRoute(mdDestination.route::class)
                }
            } ?: true
        }
    }
    Scaffold(
        modifier = modifier,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn() + expandVertically { it },
                exit = fadeOut() + shrinkVertically { it } // TODO, set animation
            ) {
                MDBottomNavigationBar(navController = navController)
            }
        },
    ) { padding ->
        MDNavHost(
            navController,
            modifier = Modifier.padding(padding)
        )
    }
}