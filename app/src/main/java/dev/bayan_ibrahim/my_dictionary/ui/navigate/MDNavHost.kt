package dev.bayan_ibrahim.my_dictionary.ui.navigate

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDTopLevelDestinations.Profile
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDTopLevelDestinations.Statistics
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDTopLevelDestinations.WordsList
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.ProfileRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.StatisticsRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.WordsListRoute

@Composable
fun MDNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = WordsList()
    ) {
        composable<WordsList> { backStackEntry ->
            val wordsList: WordsList = backStackEntry.toRoute()
            WordsListRoute(wordsList = wordsList)
        }
        composable<Profile> { backStackEntry ->
            val profile: Profile = backStackEntry.toRoute()
            ProfileRoute(profile = profile)
        }
        composable<Statistics> { backStackEntry ->
            val statistics: Statistics = backStackEntry.toRoute()
            StatisticsRoute(statistics = statistics)
        }
    }
}

