package dev.bayan_ibrahim.my_dictionary.ui.navigate

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
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.WordDetailsEditMode
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.WordDetailsViewMode
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiActions
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppUiState
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file.MDExportToFileRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.import_from_file.MDImportFromFileRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags.MDMarkerTagsRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_similar_words.MDMigrateSimilarWordsRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_tags.MDMigrateTagsRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.general.MDProfileRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme.MDAppThemeRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.MDStatisticsRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.MDStatisticsViewPreferences
import dev.bayan_ibrahim.my_dictionary.ui.screen.train.MDTrainRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode.MDWordDetailsEditModeRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.view_mode.MDWordDetailsViewModeRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.MDWordSpaceRoute
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.MDWordsListRoute

@Composable
fun MDNavHost(
    appUiState: MDAppUiState,
    appActions: MDAppUiActions,
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
                appUiState = appUiState,
                appActions = appActions,
                navigateToWordsDetails = { id, code ->
                    if (id != null) {
                        navController.navigate(WordDetailsViewMode(id, code.code))
                    } else {
                        navController.navigate(WordDetailsEditMode(id, code.code))
                    }
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
                appUiState = appUiState,
                appActions = appActions,
                navigateToScreen = navController::navigate
            )
        }
        composable<Statistics> { backStackEntry ->
            MDStatisticsRoute(
                args = MDDestination.Statistics(null),
                appUiState = appUiState,
                appActions = appActions,
            )
        }
        composable<WordDetailsViewMode> {
            val wordDetails: WordDetailsViewMode = it.toRoute()
            MDWordDetailsViewModeRoute(
                wordDetails = wordDetails,
                appUiState = appUiState,
                appActions = appActions,
                onNavigateToWordStatistics = { wordId ->
                    val preferences = MDStatisticsViewPreferences.Word(wordId)
                    val route = MDDestination.Statistics(preferences)
                    navController.navigate(route)
                },
                onEdit = { id, code ->
                    navController.navigate(WordDetailsEditMode(id, code.code))
                }
            )
        }

        composable<WordDetailsEditMode> {
            val wordDetails: WordDetailsEditMode = it.toRoute()
            MDWordDetailsEditModeRoute(
                wordDetails = wordDetails,
                appUiState = appUiState,
                appActions = appActions,
                onNavigateToDetailsViewMode = { id, code ->
                    navController.navigate(route = WordDetailsViewMode(wordId = id, languageCode = code.code))
                }
            )
        }

        composable<WordSpace> {
            val wordDetails: WordSpace = it.toRoute()
            MDWordSpaceRoute(
                navArgs = wordDetails,
                appUiState = appUiState,
                appActions = appActions,
                onNavigateToStatistics = { language ->
                    val preferences = MDStatisticsViewPreferences.Language(language)
                    val route = MDDestination.Statistics(preferences)
                    navController.navigate(route)
                }
            )
        }

        composable<MDDestination.ImportFromFile> {
            val importFromFile: MDDestination.ImportFromFile = it.toRoute()
            MDImportFromFileRoute(
                args = importFromFile,
                appUiState = appUiState,
                appActions = appActions,
            )
        }

        composable<ExportToFile> {
            val exportToFile: ExportToFile = it.toRoute()
            MDExportToFileRoute(
                args = exportToFile,
                appUiState = appUiState,
                appActions = appActions,
            )
        }

        composable<Train> {
            val train: Train = it.toRoute()
            MDTrainRoute(
                args = train,
                appUiState = appUiState,
                appActions = appActions,
                navigateToStatisticsScreen = {
                    navController.popBackStack()
                    navController.navigate(MDDestination.Statistics(MDStatisticsViewPreferences.Train()))
                },
            )
        }
        composable<MDDestination.Statistics> {
            val statistics: MDDestination.Statistics = it.toRoute()
            MDStatisticsRoute(
                args = statistics,
                appUiState = appUiState,
                appActions = appActions,
            )
        }

        composable<MDDestination.AppTheme> {
            val theme: MDDestination.AppTheme = it.toRoute()
            MDAppThemeRoute(
                args = theme,
                appUiState = appUiState,
                appActions = appActions,
            )
        }

        composable<MDDestination.MarkerTags> {
            val markerTags: MDDestination.MarkerTags = it.toRoute()
            MDMarkerTagsRoute(
                args = markerTags,
                appUiState = appUiState,
                appActions = appActions,
            )
        }

        composable<MDDestination.MigrateTags> {
            val markerTags: MDDestination.MigrateTags = it.toRoute()
            MDMigrateTagsRoute(
                markerTags,
                appUiState = appUiState,
                appActions = appActions,
            )
        }

        composable<MDDestination.MigrateSimilarWords> {
            val markerSimilarWords: MDDestination.MigrateSimilarWords = it.toRoute()
            MDMigrateSimilarWordsRoute(
                markerSimilarWords,
                appUiState = appUiState,
                appActions = appActions,
            )
        }
    }
}

