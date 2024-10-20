package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

interface MDUiState {
    val isLoading: Boolean
    val error: String?
    val validData: Boolean
}

abstract class MDMutableUiState(
    isLoading: Boolean = true,
    error: String? = null,
    validData: Boolean = false,
) : MDUiState {
    override var isLoading: Boolean by mutableStateOf(isLoading)
    override var error: String? by mutableStateOf(error)
    override var validData: Boolean by mutableStateOf(validData)

    fun onFinishDataLoad(validData: Boolean = true) {
        isLoading = false
        error = null
        this.validData = validData
    }

    fun onLoadData() {
        isLoading = true
        error = null
        validData = false
    }

    inline fun onExecute(body: () -> Boolean) {
        onLoadData()
        onFinishDataLoad(body())
    }
}