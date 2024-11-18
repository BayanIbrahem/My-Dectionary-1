package dev.bayan_ibrahim.my_dictionary.ui.screen.profile

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState

interface MDProfileUiState: MDUiState {

}

class MDProfileMutableUiState: MDProfileUiState, MDMutableUiState() {

}
