package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileStrategy

val MDFileStrategy.label: String
    @Composable
    @ReadOnlyComposable
    // TODO, string res
    get() = when (this) {
        MDFileStrategy.Ignore -> "Ignore Word"
        MDFileStrategy.OverrideAll -> "Override Word"
        MDFileStrategy.Abort -> "Abort All Transaction"
    }