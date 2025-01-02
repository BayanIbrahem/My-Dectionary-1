package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.train_preferences_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.WordsListTrainPreferencesLimit
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import dev.bayan_ibrahim.my_dictionary.domain.repo.TrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainPreferencesSortBy
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListTrainTarget
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDWordsListTrainPreferencesViewModel @Inject constructor(
    private val trainPreferencesRepo: TrainPreferencesRepo,
) : ViewModel() {
    private val _uiState: MDWordsListTrainPreferencesMutableUiState = MDWordsListTrainPreferencesMutableUiState()
    val uiState: MDWordsListTrainPreferencesUiState = _uiState
    fun initWithNavArgs() {
        viewModelScope.launch {
            _uiState.onExecute {
                val data = trainPreferencesRepo.getTrainPreferences()
                _uiState.onApplyPreferences(data)
                true
            }
        }

        // init state with train preferences
    }

    fun getUiActions(
        navActions: MDWordsListTrainPreferencesNavigationUiActions,
    ): MDWordsListTrainPreferencesUiActions = MDWordsListTrainPreferencesUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDWordsListTrainPreferencesNavigationUiActions,
    ): MDWordsListTrainPreferencesBusinessUiActions = object : MDWordsListTrainPreferencesBusinessUiActions {
        override fun onSelectTrainType(trainType: TrainWordType) = editPreferences {
            this.trainType = trainType
        }

        override fun onSelectTrainTarget(trainTarget: WordsListTrainTarget) = editPreferences {
            this.trainTarget = trainTarget
        }

        override fun onSelectLimit(limit: WordsListTrainPreferencesLimit) = editPreferences {
            this.limit = limit
        }

        override fun onSelectSortBy(sortBy: WordsListTrainPreferencesSortBy) = editPreferences {
            this.sortBy = sortBy
        }

        override fun onSelectSortByOrder(sortByOrder: MDWordsListSortByOrder) = editPreferences {
            this.sortByOrder = sortByOrder
        }

        override fun onConfirmTrain() {
            navActions.navigateToTrainScreen()
        }

        override fun onResetTrainPreferences() = editPreferences {
            this.onApplyPreferences(defaultWordsListTrainPreferences)
        }
    }

    private fun editPreferences(body: MDWordsListTrainPreferencesMutableUiState.() -> Unit) {
        _uiState.body()
        viewModelScope.launch {
            launch {
                trainPreferencesRepo.setTrainPreferences(uiState)
            }
        }
    }
}
