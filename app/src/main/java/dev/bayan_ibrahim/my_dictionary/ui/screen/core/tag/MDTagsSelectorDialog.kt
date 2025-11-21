package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2ActionRow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2CancelAction
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2ConfirmAction
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MDTagsSelectorDialog(
    uiState: MDTagsSelectorUiState,
    uiActions: MDTagsSelectorUiActions,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit) = {
        Text(
            // TODO, string res
            "Tags Selector"
        )
    },
    lazyState: LazyListState = rememberLazyListState(),
) {
    Dialog(
        onDismissRequest = {
            // in fact this is implemented as on dismiss in the container of this dialog cause there is no on pop in dialog
            uiActions.onPop()
        }
    ) {
        MDCard2(
            modifier = Modifier.sizeIn(
                maxHeight = 600.dp,
                maxWidth = 300.dp
            ),
            header = {
                MDCard2ListItem(
                    title = title,
                    trailing = {
                        IconButton(
                            onClick = {
                                uiActions.onConfirmSelectedTags()
                            }
                        ) {
                            MDIcon(MDIconsSet.Close)
                        }
                    },
                )
            },
            footer = {
                MDCard2ActionRow {
                    MDCard2ConfirmAction(
                        enabled = uiState.selectedTagsIds.isNotEmpty(),
                        onClick = {
                            uiActions.onConfirmSelectedTags()
                        }
                    )

                    MDCard2CancelAction(
                        onClick = {
                            uiActions.onPop()
                        }
                    )
                }
            }
        ) {
            MDTagsSelectorContent(
                uiState = uiState,
                uiActions = uiActions,
                modifier = modifier,
                lazyState = lazyState
            )
        }
    }
}
