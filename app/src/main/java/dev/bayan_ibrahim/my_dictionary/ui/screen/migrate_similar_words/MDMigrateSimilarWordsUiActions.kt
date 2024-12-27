package dev.bayan_ibrahim.my_dictionary.ui.screen.migrate_similar_words

import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions


interface MDMigrateSimilarWordsBusinessUiActions {
}

interface MDMigrateSimilarWordsNavigationUiActions: MDAppNavigationUiActions {
}

@androidx.compose.runtime.Immutable
class MDMigrateSimilarWordsUiActions(
    navigationActions: MDMigrateSimilarWordsNavigationUiActions,
    businessActions: MDMigrateSimilarWordsBusinessUiActions,
) : MDMigrateSimilarWordsBusinessUiActions by businessActions, MDMigrateSimilarWordsNavigationUiActions by navigationActions