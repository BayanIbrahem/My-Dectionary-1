package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.WordsListTrainPreferencesLimit
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget


interface MDWordsListTrainPreferences {
    val trainType: TrainWordType
    val trainTarget: WordsListTrainTarget
    val sortBy: WordsListTrainPreferencesSortBy
    val sortByOrder: MDWordsListSortByOrder
    val limit: WordsListTrainPreferencesLimit
}

data class MDWordsListTrainPreferencesBuilder(
    override val trainType: TrainWordType,
    override val trainTarget: WordsListTrainTarget,
    override val sortBy: WordsListTrainPreferencesSortBy,
    override val sortByOrder: MDWordsListSortByOrder,
    override val limit: WordsListTrainPreferencesLimit,
) : MDWordsListTrainPreferences

val defaultWordsListTrainPreferences by lazy {
    MDWordsListTrainPreferencesBuilder(
        trainType = TrainWordType.SelectWordMeaning,
        trainTarget = WordsListTrainTarget.Translation,
        sortBy = WordsListTrainPreferencesSortBy.Random,
        sortByOrder = MDWordsListSortByOrder.Asc,
        limit = WordsListTrainPreferencesLimit._10
    )
}