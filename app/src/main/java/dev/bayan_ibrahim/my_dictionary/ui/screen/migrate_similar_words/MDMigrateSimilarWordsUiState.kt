package dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_similar_words

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState


interface MDMigrateSimilarWordsUiState : MDUiState {

}

class MDMigrateSimilarWordsMutableUiState : MDMigrateSimilarWordsUiState, MDMutableUiState() {

}
