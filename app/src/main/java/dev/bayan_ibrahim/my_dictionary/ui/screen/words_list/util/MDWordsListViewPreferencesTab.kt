package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabData
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

enum class MDWordsListViewPreferencesTab {
    Search,
    Filter,
    Sort;

    val tabData: MDTabData.LabelWithIcon<Int>
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Search -> MDTabData.LabelWithIcon(
                label = firstCapStringResource(R.string.search),
                icon = MDIconsSet.SearchList,
                key = Search.ordinal
            )

            Filter -> MDTabData.LabelWithIcon(
                label = firstCapStringResource(R.string.filter),
                icon = MDIconsSet.Filter,
                key = Filter.ordinal
            )

            Sort -> MDTabData.LabelWithIcon(
                label = firstCapStringResource(R.string.sort),
                icon = MDIconsSet.Sort,
                key = Sort.ordinal
            )
        }
}