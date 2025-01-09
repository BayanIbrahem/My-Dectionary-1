package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.checkboxItem
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.item
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType

@Composable
fun MDFilePartsSelector(
    selectedParts: Map<MDFilePartType, Boolean>,
    onToggleAvailablePart: (MDFilePartType, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
    ) {
        val primaryColors = MDHorizontalCardDefaults.primaryColors
        val sortedPartsList by remember(selectedParts) {
            derivedStateOf {
                selectedParts.entries.sortedBy { it.key }
            }
        }
        MDHorizontalCardGroup {
            item(colors = primaryColors) {
                Text("Available Parts (${selectedParts.count()})")
            }
            sortedPartsList.forEach { (part, selected) ->
                checkboxItem(
                    checked = selected,
                    onClick = {
                        onToggleAvailablePart(part, !selected)
                    },
                    leadingIcon = {
                        MDIcon(icon = part.icon)
                    }
                ) {
                    Text(part.label)
                }
            }
        }
    }
}
