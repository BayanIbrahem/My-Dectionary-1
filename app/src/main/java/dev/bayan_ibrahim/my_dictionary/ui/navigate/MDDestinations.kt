package dev.bayan_ibrahim.my_dictionary.ui.navigate

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.TopLevel.WordsList
import kotlinx.serialization.Serializable

@Serializable
sealed interface MDDestination {
    @Serializable
    sealed interface TopLevel {
        @Serializable
        data class WordsList(val languageCode: String? = null) : TopLevel

        @Serializable
        data object Statistics : TopLevel

        @Serializable
        data object Profile : TopLevel

        @Serializable
        data object WordSpace : TopLevel

        enum class Enum(
            val selectedIcon: ImageVector, // TODO, icon res
            val unselectedIcon: ImageVector, // TODO, icon res
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
            ),
            WordSpace(
                selectedIcon = Icons.Filled.Face,
                unselectedIcon = Icons.Default.Face
            ),
            ;

            val route
                get() = when (this) {
                    WordsList -> WordsList()
                    Statistics -> TopLevel.Statistics
                    Profile -> TopLevel.Profile
                    WordSpace -> TopLevel.WordSpace
                }
        }
    }

    @Serializable
    data class WordDetails(
        val wordId: Long? = null,
        val languageCode: String,
    ) : MDDestination

    @Serializable
    data object ImportFromFile: MDDestination

    @Serializable
    data object ExportToFile: MDDestination

    @Serializable
    data object AutoBackup: MDDestination
}

