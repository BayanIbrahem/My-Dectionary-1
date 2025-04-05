package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.overline.MDCard2Overline
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2CheckboxItem
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
        val sortedPartsList by remember(selectedParts) {
            derivedStateOf {
                selectedParts.entries.sortedBy { it.key }
            }
        }
        MDCard2(
            overline = {
                // TODO, string res
                MDCard2Overline(
                    title = "Available Parts (${selectedParts.count()})",
                )
            },
        ) {
            sortedPartsList.forEach { (part, checked) ->
                MDCard2CheckboxItem(
                    title = part.label,
                    checked = checked,
                    onCheckedChange = {
                        onToggleAvailablePart(part, it)
                    },
                    leadingCheckbox = false,
                    secondary = {
                        MDIcon(icon = part.icon)
                    }
                )
            }
        }
    }
}
