package dev.bayan_ibrahim.my_dictionary.ui.navigate

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed interface MDTopLevelDestinations {
    @Serializable
    data class WordsList(val languageCode: String? = null) : MDTopLevelDestinations

    @Serializable
    data object Statistics : MDTopLevelDestinations

    @Serializable
    data object Profile : MDTopLevelDestinations

    enum class Enum(
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
    ) {
        WordsList(
            selectedIcon = Icons.AutoMirrored.Filled.List,
            unselectedIcon = Icons.AutoMirrored.Default.List
        ),
        Statistics(
            selectedIcon = Icons.Filled.Check,
            unselectedIcon = Icons.Default.Check
        ),
        Profile(
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Default.Person
        );

        val route
            get() = when (this) {
                WordsList -> MDTopLevelDestinations.WordsList()
                Statistics -> MDTopLevelDestinations.Statistics
                Profile -> MDTopLevelDestinations.Profile
            }
    }
}