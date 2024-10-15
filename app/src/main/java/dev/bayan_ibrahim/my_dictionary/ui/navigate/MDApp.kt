package dev.bayan_ibrahim.my_dictionary.ui.navigate

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun MDApp(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    Scaffold(
        modifier = modifier,
        bottomBar = {
            MDBottomNavigationBar(navController = navController)
        }
    ) { padding ->
        MDNavHost(
            navController,
            modifier = Modifier
                .padding(padding)
        )
    }
}