package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component.train_preferences

import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListTrainPreferences

interface WordsListTrainPreferencesState : WordsListTrainPreferences {
    val showDialog: Boolean
    val isLoading: Boolean
}

