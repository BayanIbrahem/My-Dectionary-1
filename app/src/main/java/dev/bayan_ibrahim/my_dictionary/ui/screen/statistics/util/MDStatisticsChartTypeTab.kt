package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTabData
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

enum class MDStatisticsChartTypeTab {
    LINE,
    BAR;

    val tabData: MDTabData.LabelWithIcon<MDStatisticsChartTypeTab>
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            LINE -> MDTabData.LabelWithIcon(
                label = firstCapStringResource(R.string.line_chart),
                icon = MDIconsSet.LineChart,
                key = this
            )

            BAR -> MDTabData.LabelWithIcon(
                label = firstCapStringResource(R.string.bar_chart),
                icon = MDIconsSet.BarChart,
                key = this
            )
        }
}