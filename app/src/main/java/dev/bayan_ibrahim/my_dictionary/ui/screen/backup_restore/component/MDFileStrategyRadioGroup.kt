package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2
import dev.bayan_ibrahim.my_dictionary.core.ui.card.MDCard2RadioButtonItem
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

@Composable
fun <E : LabeledEnum> MDOptionSelectionGroup(
    selectedOption: E,
    availableOptions: List<E>,
    onSelectOption: (E) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    label: @Composable (E) -> String = { it.label },
) {
    MDCard2(
        modifier = modifier,
        header = {
            MDCard2ListItem(title, subtitle = subtitle)
        },
    ) {
        availableOptions.forEach { strategy ->
            val selected = selectedOption == strategy
            MDCard2RadioButtonItem(
                selected = selected,
                onClick = {
                    onSelectOption(strategy)
                },
                title = label(strategy)
            )
        }
    }
}

@Preview
@Composable
private fun MDFileStrategyRadioGroupPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDOptionSelectionGroup(
                    selectedOption = MDPropertyConflictStrategy.IgnoreProperty,
                    availableOptions = MDPropertyConflictStrategy.entries,
                    onSelectOption = {},
                    title = "Corrupted File",
                )
            }
        }
    }
}