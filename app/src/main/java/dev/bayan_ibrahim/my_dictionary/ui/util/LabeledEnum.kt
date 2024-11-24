package dev.bayan_ibrahim.my_dictionary.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.vector.ImageVector

interface LabeledEnum {
    val strLabel: String

    val label: String
        @Composable
        @ReadOnlyComposable
        get() = strLabel //TODO,string res
}

@Suppress("unused")
interface IconedEnum {
    val icon: ImageVector
}
