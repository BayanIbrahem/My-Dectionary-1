package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.train_preferences_dialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.WordsListTrainPreferencesLimit
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType

class MDWordsListTrainPreferencesMutableUiState(
    trainType: TrainWordType = defaultWordsListTrainPreferences.trainType,
    trainTarget: WordsListTrainTarget = defaultWordsListTrainPreferences.trainTarget,
    sortBy: WordsListTrainPreferencesSortBy = defaultWordsListTrainPreferences.sortBy,
    sortByOrder: MDWordsListSortByOrder = defaultWordsListTrainPreferences.sortByOrder,
    limit: WordsListTrainPreferencesLimit = defaultWordsListTrainPreferences.limit,
) : MDWordsListTrainPreferencesUiState, MDMutableUiState() {
    constructor(data: MDWordsListTrainPreferences) : this(
        trainType = data.trainType,
        trainTarget = data.trainTarget,
        sortBy = data.sortBy,
        sortByOrder = data.sortByOrder,
        limit = data.limit,
    )

    override var trainType: TrainWordType by mutableStateOf(trainType)
    override var trainTarget: WordsListTrainTarget by mutableStateOf(trainTarget)
    override var sortBy: WordsListTrainPreferencesSortBy by mutableStateOf(sortBy)
    override var sortByOrder: MDWordsListSortByOrder by mutableStateOf(sortByOrder)
    override var limit: WordsListTrainPreferencesLimit by mutableStateOf(limit)

    fun onApplyPreferences(preferences: MDWordsListTrainPreferences) {
        trainType = preferences.trainType
        trainTarget = preferences.trainTarget
        sortBy = preferences.sortBy
        sortByOrder = preferences.sortByOrder
        limit = preferences.limit
    }
}
