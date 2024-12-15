package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabData
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

enum class MDWordsListTrainPreferencesTab {
    TrainType,
    WordsOrder;

    val tabData: MDTabData.LabelWithIcon<Int>
        @Composable
        @ReadOnlyComposable
        // TODO, string res
        get() = when (this) {
            TrainType -> MDTabData.LabelWithIcon(
                label = "Train Type",
                icon = MDIconsSet.TrainType,
                key = TrainType.ordinal
            )

            WordsOrder -> MDTabData.LabelWithIcon(
                label = "Words Order",
                icon = MDIconsSet.TrainWordsOrder,
                key = WordsOrder.ordinal
            )
        }
}