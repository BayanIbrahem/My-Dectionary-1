package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabData

enum class MDWordsListViewPreferencesTab {
    Search,
    Filter, Sort;

    val tabData: MDTabData.LabelWithIcon<Int>
        @Composable
        @ReadOnlyComposable
        // TODO, string res
        // TODO, icon res
        get() = when (this) {
            Search -> MDTabData.LabelWithIcon(label = "Search", icon = Icons.Default.Search, key = Search.ordinal)
            Filter -> MDTabData.LabelWithIcon(label = "Filter", icon = Icons.AutoMirrored.Filled.List, key = Filter.ordinal)
            Sort -> MDTabData.LabelWithIcon(label = "Sort", icon = Icons.Default.PlayArrow, key = Sort.ordinal)
        }
}