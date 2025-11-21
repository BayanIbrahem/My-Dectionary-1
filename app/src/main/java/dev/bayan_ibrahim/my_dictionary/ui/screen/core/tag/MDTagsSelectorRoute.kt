package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.ui.navigate.app.MDAppNavigationUiActions

@Composable
fun MDTagsSelectorRoute(
    modifier: Modifier = Modifier,
    isDialog: Boolean = true,
    selectorViewModel: MDTagsSelectorViewModel = hiltViewModel(),
    onConfirmSelectedTags: (List<Tag>) -> Unit = {},
    allowedFilter: (Tag) -> Boolean = { true },
    forbiddenFilter: (Tag) -> Boolean = { false },
    /**
     * this callback is called on pop or on dismiss dialog, you need to pass real onPop if [isDialog] is false
     */
    onPopOrDismissDialog: () -> Unit = {},
    selectedTagsMaxSize: Int = Int.MAX_VALUE,
    isSelectEnabled: Boolean = false,
    isAddEnabled: Boolean = false,
    isEditEnabled: Boolean = false,
    isDeleteEnabled: Boolean = false,
    isDeleteSubtreeEnabled: Boolean = false,
    title: (@Composable () -> Unit) = {
        Text(
            // TODO, string res
            "Tags Selector"
        )
    },
) {

    DisposableEffect(Unit) {
        selectorViewModel.init(
            allowedFilter = allowedFilter,
            forbiddenFilter = forbiddenFilter,
            selectedTagsMaxSize = selectedTagsMaxSize,
            isSelectEnabled = isSelectEnabled,
            isAddEnabled = isAddEnabled,
            isEditEnabled = isEditEnabled,
            isDeleteEnabled = isDeleteEnabled,
            isDeleteSubtreeEnabled = isDeleteSubtreeEnabled
        )
        this.onDispose { }
    }
    val uiState = selectorViewModel.uiState
    val lazyState = rememberLazyListState()
    val navActions: MDTagsSelectorNavigationUiActions by remember() {
        derivedStateOf {
            object : MDTagsSelectorNavigationUiActions, MDAppNavigationUiActions {
                override suspend fun onAnimateScrollToIndex(index: Int) {
                    lazyState.animateScrollToItem(index)
                }

                override fun onNotifyConfirmSelectedTags(selectedTags: List<Tag>) {
                    super.onNotifyConfirmSelectedTags(selectedTags)
                    onConfirmSelectedTags(selectedTags)
                }

                override fun onPop() {
                    onPopOrDismissDialog()
                }
            }
        }
    }
    val uiActions by remember(navActions) {
        derivedStateOf {
            selectorViewModel.getUiActions(navActions)
        }
    }
    if (isDialog) {
        MDTagsSelectorDialog(
            uiState = uiState,
            uiActions = uiActions,
            modifier = modifier,
            lazyState = lazyState,
            title = title,
        )
    } else {
        MDTagsSelectorScreen(
            uiState = uiState,
            uiActions = uiActions,
            modifier = modifier,
            lazyState = lazyState,
            title = title,
        )
    }
}
