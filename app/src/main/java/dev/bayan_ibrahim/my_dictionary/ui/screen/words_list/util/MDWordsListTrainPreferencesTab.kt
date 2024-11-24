package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabData

enum class MDWordsListTrainPreferencesTab {
    TrainType,
    WordsOrder;

    val tabData: MDTabData.LabelWithIcon<Int>
        @Composable
        @ReadOnlyComposable
        // TODO, string res
        // TODO, icon res
        get() = when (this) {
            TrainType -> MDTabData.LabelWithIcon(label = "Train Type", icon = Icons.Default.Search, key = TrainType.ordinal)
            WordsOrder -> MDTabData.LabelWithIcon(label = "Words Order", icon = Icons.Default.PlayArrow, key = WordsOrder.ordinal)
        }
}