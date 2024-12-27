package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState


interface MDMarkerTagsUiState : MDUiState {

}

class MDMarkerTagsMutableUiState : MDMarkerTagsUiState, MDMutableUiState() {

}
