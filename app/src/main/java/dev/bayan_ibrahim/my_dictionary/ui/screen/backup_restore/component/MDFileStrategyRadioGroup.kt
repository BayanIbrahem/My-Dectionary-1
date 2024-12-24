package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.MDHorizontalCardGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card.radioItem
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

@Composable
fun <E : LabeledEnum> MDPropertyStrategyGroup(
    selectedStrategy: E,
    availableStrategies: List<E>,
    onSelectStrategy: (E) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    label: @Composable (E) -> String = { it.label },
) {
    MDHorizontalCardGroup(
        modifier = modifier,
        title = {
            Text(title)
        },
        subtitle = subtitle?.let {
            {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    ) {
        availableStrategies.forEach { strategy ->
            val selected = selectedStrategy == strategy
            radioItem(
                selected = selected,
                onClick = {
                    onSelectStrategy(strategy)
                },
            ) {
                Text(label(strategy))
            }
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
                MDPropertyStrategyGroup(
                    selectedStrategy = MDPropertyConflictStrategy.IgnoreProperty,
                    availableStrategies = MDPropertyConflictStrategy.entries,
                    onSelectStrategy = {},
                    title = "Corrupted File",
                )
            }
        }
    }
}