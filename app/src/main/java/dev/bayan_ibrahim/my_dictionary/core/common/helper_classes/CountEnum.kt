package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

interface CountEnum {
    val count: Int


//    TODO, string res
//    /**
//     * if the count is [Int.MAX_VALUE] then we use this res for label
//     */
//    @get:StringRes
//    val allRes: Int
//
//    /**
//     * plural res for this count
//     */
//    @get:PluralsRes
//     val pluralRes: Int

    val label: String
        @Composable
        @ReadOnlyComposable
        get() {
            return if (count == Int.MAX_VALUE) {
                "$count"
            } else {
                "$count"
            }
        }
}