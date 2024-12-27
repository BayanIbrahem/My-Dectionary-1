package dev.bayan_ibrahim.my_dictionary.ui.navigate.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDBottomNavigationBar
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDNavHost
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDNavigationDrawer
import dev.bayan_ibrahim.my_dictionary.ui.navigate.allowDrawerGestures
import dev.bayan_ibrahim.my_dictionary.ui.navigate.getCurrentDestination
import dev.bayan_ibrahim.my_dictionary.ui.navigate.isTopLevel
import kotlinx.coroutines.launch

@Composable
fun MDApp(
    modifier: Modifier = Modifier,
    appViewModel: MDAppViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val navBackState by navController.currentBackStackEntryFlow.collectAsStateWithLifecycle(initialValue = null)
    val currentDestination by remember(navBackState) {
        derivedStateOf {
            navBackState?.destination?.getCurrentDestination()
        }
    }
    val showBottomBar by remember(currentDestination) {
        derivedStateOf {
            currentDestination?.isTopLevel() == true
        }
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val gesturesEnabled by remember(currentDestination) {
        derivedStateOf {
            currentDestination?.allowDrawerGestures() != false
        }
    }
    LaunchedEffect(gesturesEnabled) {
        if (!gesturesEnabled) {
            drawerState.close()
        }
    }
    val coroutineScope = rememberCoroutineScope()

    val appState = appViewModel.uiState
    val appNavActions by remember {
        derivedStateOf {
            object: MDAppNavigationUiActions{
                override fun onOpenNavDrawer() {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }

                override fun onCloseNavDrawer() {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }

                override fun onPop() {
                    currentDestination?.let {
                        navController.popBackStack(it, inclusive = true)
                    }
                }
            }
        }
    }
    val appActions by remember {
        derivedStateOf {
            appViewModel.getUiActions(appNavActions)
        }
    }
    MDNavigationDrawer(
        currentDestination = currentDestination,
        onNavigateTo = {
            if(it is MDDestination.TopLevel) {
                navController.popBackStack()

            }
            navController.navigate(it)
        },
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled
    ) {
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
            BackHandler(true) {
                if (drawerState.isOpen) {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                } else {
                    navController.popBackStack()
                }
            }
            MDNavHost(
                appUiState = appState,
                appActions = appActions,
                navController = navController,
                modifier = Modifier.padding(padding),
            )
        }
    }
}