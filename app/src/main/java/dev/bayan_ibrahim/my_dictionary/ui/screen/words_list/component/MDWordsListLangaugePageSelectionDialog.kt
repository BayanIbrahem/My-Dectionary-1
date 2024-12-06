package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.ui.MDLanguageSelectionDialog
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import kotlinx.collections.immutable.PersistentList

@Composable
fun MDWordsListLanguageSelectionPageDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    languagesWithWords: PersistentList<LanguageWordSpace>,
    languagesWithoutWords: PersistentList<LanguageWordSpace>,
    onSelectWordSpace: (LanguageWordSpace) -> Unit,
    modifier: Modifier = Modifier,
) {
    MDLanguageSelectionDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        query = query,
        onQueryChange = onQueryChange,
        primaryList = languagesWithWords,
        secondaryList = languagesWithoutWords,
        onSelectWordSpace = onSelectWordSpace,
        modifier = modifier,
        primaryListCountTitleBuilder = {
            "Languages with words $it" // TODO, string res,
        },
        secondaryListCountTitleBuilder = {
            "Languages without words $it" // TODO, string res
        }
    )
}
