package dev.bayan_ibrahim.my_dictionary.ui.screen.train

import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainSubmitOption
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordAnswer

interface MDTrainBusinessUiActions {
    fun onSelectAnswer(answer: MDTrainWordAnswer)
    fun onSelectAnswerSubmit(index: Int, submitOption: MDTrainSubmitOption)
    fun onWriteWordSubmit(answer: String, submitOption: MDTrainSubmitOption)
}

interface MDTrainNavigationUiActions {
    fun onNavigateToResultsScreen()
}

@androidx.compose.runtime.Immutable
class MDTrainUiActions(
    navigationActions: MDTrainNavigationUiActions,
    businessActions: MDTrainBusinessUiActions,
) : MDTrainBusinessUiActions by businessActions, MDTrainNavigationUiActions by navigationActions