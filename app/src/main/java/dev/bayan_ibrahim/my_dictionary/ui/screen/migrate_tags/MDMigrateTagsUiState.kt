package dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_tags

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState


interface MDMigrateTagsUiState : MDUiState {

}

class MDMigrateTagsMutableUiState : MDMigrateTagsUiState, MDMutableUiState() {

}
