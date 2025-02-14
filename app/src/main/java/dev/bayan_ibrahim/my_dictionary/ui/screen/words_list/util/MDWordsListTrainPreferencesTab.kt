package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.eachFirstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabData
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

enum class MDWordsListTrainPreferencesTab {
    TrainType,
    WordsOrder;

    val tabData: MDTabData.LabelWithIcon<Int>
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            TrainType -> MDTabData.LabelWithIcon(
                label = eachFirstCapStringResource(R.string.train_type),
                icon = MDIconsSet.TrainType,
                key = TrainType.ordinal
            )

            WordsOrder -> MDTabData.LabelWithIcon(
                label = eachFirstCapStringResource(R.string.words_order),
                icon = MDIconsSet.TrainWordsOrder,
                key = WordsOrder.ordinal
            )
        }
}