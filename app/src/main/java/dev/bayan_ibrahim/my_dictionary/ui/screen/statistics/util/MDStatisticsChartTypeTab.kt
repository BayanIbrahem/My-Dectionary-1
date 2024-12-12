package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabData

enum class MDStatisticsChartTypeTab {
    LINE,
    BAR;

    val tabData: MDTabData.LabelWithIcon<MDStatisticsChartTypeTab>
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            LINE -> MDTabData.LabelWithIcon(
                label = "Line chart",
                icon = Icons.Default.Face,
                key = this
            )

            BAR -> MDTabData.LabelWithIcon(
                label = "Bar chart",
                icon = Icons.Default.Face,
                key = this
            )
        }
}