package dev.bayan_ibrahim.my_dictionary.ui.screen.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDTopLevelDestinations

@Composable
fun ProfileRoute(
    profile: MDTopLevelDestinations.Profile,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // TODO, add profile route
        Text("Profile Screen")
    }
}