package dev.bayan_ibrahim.my_dictionary.ui.navigate

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.TopLevel.Profile
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.TopLevel.Statistics
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.TopLevel.WordSpace
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.TopLevel.WordsList
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.WordDetails
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.MDProfileRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.MDStatisticsRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.WordDetailsRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.MDWordSpaceRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.MDWordsListRoute

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
            MDWordsListRoute(
                navArgs = wordsList,
                navigateToWordsDetails = { id, code ->
                    navController.navigate(WordDetails(id, code.code))
                }
            )
        }
        composable<Profile> { backStackEntry ->
            val profile: Profile = backStackEntry.toRoute()
            MDProfileRoute(profile = profile)
        }
        composable<Statistics> { backStackEntry ->
            val statistics: Statistics = backStackEntry.toRoute()
            MDStatisticsRoute(statistics = statistics)
        }
        composable<WordDetails> {
            val wordDetails: WordDetails = it.toRoute()
            WordDetailsRoute(wordDetails = wordDetails, pop = navController::popBackStack)
        }

        composable<WordSpace> {
            val wordDetails: WordSpace = it.toRoute()
            MDWordSpaceRoute(navArgs = wordDetails)
        }
    }
}

