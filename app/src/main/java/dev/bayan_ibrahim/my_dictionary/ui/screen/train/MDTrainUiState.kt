package dev.bayan_ibrahim.my_dictionary.ui.screen.train

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWord
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType

interface MDTrainUiState : MDUiState {
    val trainType: TrainWordType
    val trainWordsList: List<TrainWord>
    val currentWordIndex: Int
    val isLast: Boolean
        get() = currentWordIndex == trainWordsList.size - 1
}

class MDTrainMutableUiState : MDTrainUiState, MDMutableUiState() {
    override val trainWordsList: SnapshotStateList<TrainWord> = mutableStateListOf()
    override var currentWordIndex: Int by mutableIntStateOf(0)
    override var trainType: TrainWordType by mutableStateOf(TrainWordType.WriteWord)
}
