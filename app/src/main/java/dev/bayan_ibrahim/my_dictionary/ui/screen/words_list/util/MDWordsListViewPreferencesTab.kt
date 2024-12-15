package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabData
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

enum class MDWordsListViewPreferencesTab {
    Search,
    Filter,
    Sort;

    val tabData: MDTabData.LabelWithIcon<Int>
        @Composable
        @ReadOnlyComposable
        // TODO, string res
        get() = when (this) {
            Search -> MDTabData.LabelWithIcon(
                label = "Search",
                icon = MDIconsSet.SearchList,
                key = Search.ordinal
            )

            Filter -> MDTabData.LabelWithIcon(
                label = "Filter",
                icon = MDIconsSet.Filter,
                key = Filter.ordinal
            )

            Sort -> MDTabData.LabelWithIcon(
                label = "Sort",
                icon = MDIconsSet.Sort,
                key = Sort.ordinal
            )
        }
}