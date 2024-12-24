package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDVerticalCard
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTagSegmentSeparator
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

fun LazyListScope.wordDetailsContextTagListItems(
    tags: List<ContextTag>,
    isEditModeOn: Boolean,
    onAddNewTagClick: () -> Unit,
    onDeleteTag: (Int, ContextTag) -> Unit,
) {
    itemsIndexed(
        tags,
    ) { i, tag ->
        MDBasicTextField(
            value = tag.segments.joinToString(ContextTagSegmentSeparator), // TODO, string res
            onValueChange = {},
            readOnly = true,
            leadingIcons = {
                MDIcon(MDIconsSet.WordTag)
            },
            trailingIcons = {
                IconButton(
                    onClick = {
                        onDeleteTag(i, tag)
                    }
                ) {
                    MDIcon(MDIconsSet.Delete) // TODO, icon res
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .animateItem(),
            prefix = {
                Text("${i.inc()}.")
            },
            label = if (i == 0) "Context tags" else "",
            hasBottomHorizontalDivider = false,
        )
    }
    if (isEditModeOn) {
        item {
            MDVerticalCard(
                headerModifier = Modifier,
                footerModifier = Modifier,
                contentModifier = Modifier,
                onClick = onAddNewTagClick,
                colors = MDCardDefaults.colors(
                    contentContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MDIcon(MDIconsSet.WordTag)
                    Text("${tags.count().inc()}.", style = MDTextFieldDefaults.textStyle)
                    Text("Add New Context Tag", style = MDTextFieldDefaults.textStyle)
                }
            }
        }
    }
}
