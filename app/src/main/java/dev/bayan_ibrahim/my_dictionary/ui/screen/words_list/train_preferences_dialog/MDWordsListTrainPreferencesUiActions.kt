package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.train_preferences_dialog

import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.WordsListTrainPreferencesLimit
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget

interface MDWordsListTrainPreferencesBusinessUiActions {
    fun onSelectTrainType(trainType: TrainWordType)
    fun onSelectTrainTarget(trainTarget: WordsListTrainTarget)
    fun onSelectLimit(limit: WordsListTrainPreferencesLimit)
    fun onSelectSortBy(sortBy: WordsListTrainPreferencesSortBy)
    fun onSelectSortByOrder(sortByOrder: WordsListSortByOrder)
    fun onConfirmTrain()
    fun onResetTrainPreferences()
}

interface MDWordsListTrainPreferencesNavigationUiActions {
    fun onDismissDialog()
    fun navigateToTrainScreen()
}

@androidx.compose.runtime.Immutable
class MDWordsListTrainPreferencesUiActions(
    navigationActions: MDWordsListTrainPreferencesNavigationUiActions,
    businessActions: MDWordsListTrainPreferencesBusinessUiActions,
) : MDWordsListTrainPreferencesBusinessUiActions by businessActions, MDWordsListTrainPreferencesNavigationUiActions by navigationActions