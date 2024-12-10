package dev.bayan_ibrahim.my_dictionary.ui.navigate

import dev.bayan_ibrahim.my_dictionary.ui.screen.train.MDTrainRoute
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.ExportToFile
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.TopLevel.Profile
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.TopLevel.Statistics
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.TopLevel.WordSpace
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.TopLevel.WordsList
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.Train
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.WordDetails
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file.MDExportToFileRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file.MDImportFromFileRoute
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
                },
                navigateToTrainScreen = {
                    navController.navigate(Train)
                }
            )
        }
        composable<Profile> { backStackEntry ->
            val profile: Profile = backStackEntry.toRoute()
            MDProfileRoute(
                profile = profile,
                navigateToScreen = navController::navigate
            )
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

        composable<MDDestination.ImportFromFile> {
            val importFromFile: MDDestination.ImportFromFile = it.toRoute()
            MDImportFromFileRoute(importFromFile)
        }

        composable<ExportToFile> {
            val exportToFile: ExportToFile = it.toRoute()
            MDExportToFileRoute(exportToFile)
        }

        composable<Train> {
            val train: Train = it.toRoute()
            MDTrainRoute(train)

        }
    }
}

