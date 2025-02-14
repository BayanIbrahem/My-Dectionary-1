package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapPluralsResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource

interface QuantityEnum {
    val quantity: Int

    val pluralsRes: Int
    val allStringRes: Int

    val label: String
        @Composable
        @ReadOnlyComposable
        get() {
            return if (quantity == Int.MAX_VALUE) {
                buildString {
                    append(firstCapStringResource(R.string.all))
                    append(" ")
                    append(stringResource(allStringRes))
                }
            } else {
                firstCapPluralsResource(pluralsRes, quantity)
            }
        }
}