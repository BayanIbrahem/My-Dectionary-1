package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.train_preferences

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.WordsListTrainPreferencesLimit
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType

class WordsListTrainPreferencesMutableState(
    trainType: TrainWordType = defaultWordsListTrainPreferences.trainType,
    trainTarget: WordsListTrainTarget = defaultWordsListTrainPreferences.trainTarget,
    sortBy: WordsListTrainPreferencesSortBy = defaultWordsListTrainPreferences.sortBy,
    sortByOrder: WordsListSortByOrder = defaultWordsListTrainPreferences.sortByOrder,
    limit: WordsListTrainPreferencesLimit = defaultWordsListTrainPreferences.limit,
) : WordsListTrainPreferencesState {
    constructor(data: WordsListTrainPreferences) : this(
        trainType = data.trainType,
        trainTarget = data.trainTarget,
        sortBy = data.sortBy,
        sortByOrder = data.sortByOrder,
        limit = data.limit,
    )

    override var showDialog: Boolean by mutableStateOf(false)
    override var trainType: TrainWordType by mutableStateOf(trainType)
    override var trainTarget: WordsListTrainTarget by mutableStateOf(trainTarget)
    override var sortBy: WordsListTrainPreferencesSortBy by mutableStateOf(sortBy)
    override var sortByOrder: WordsListSortByOrder by mutableStateOf(sortByOrder)
    override var limit: WordsListTrainPreferencesLimit by mutableStateOf(limit)

    fun onApplyPreferences(preferences: WordsListTrainPreferences) {
        trainType = preferences.trainType
        trainTarget = preferences.trainTarget
        sortBy = preferences.sortBy
        sortByOrder = preferences.sortByOrder
        limit = preferences.limit
    }
}
