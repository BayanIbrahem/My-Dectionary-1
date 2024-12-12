package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.train_preferences

import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.WordsListTrainPreferencesLimit
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType

interface WordsListTrainPreferencesActions {
    fun onHideTrainPreferencesDialog()
    fun onShowTrainPreferencesDialog()

    fun onSelectTrainType(trainType: TrainWordType)
    fun onSelectTrainTarget(trainTarget: WordsListTrainTarget)
    fun onSelectLimit(limit: WordsListTrainPreferencesLimit)
    fun onSelectSortBy(sortBy: WordsListTrainPreferencesSortBy)
    fun onSelectSortByOrder(sortByOrder: WordsListSortByOrder)
    fun onConfirmTrain()
    fun onResetTrainPreferences()
}