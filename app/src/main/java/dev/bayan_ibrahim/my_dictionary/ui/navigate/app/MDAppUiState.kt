package dev.bayan_ibrahim.my_dictionary.ui.navigate.app

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState


interface MDAppUiState : MDUiState {

}

class MDAppMutableUiState(
    isLoading: Boolean = true,
    error: String? = null,
    validData: Boolean = false,
) : MDAppUiState, MDMutableUiState(
    isLoading = isLoading,
    error = error,
    validData = validData
)
