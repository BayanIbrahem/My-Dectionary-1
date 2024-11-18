package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDField
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDFieldsGroup
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileStrategy
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.util.label
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDFileStrategyRadioGroup(
    selectedStrategy: MDFileStrategy,
    onSelectStrategy: (MDFileStrategy) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    availableStrategies: List<MDFileStrategy> = MDFileStrategy.entries,
    label: @Composable (MDFileStrategy) -> String = { it.label },
) {
    MDFieldsGroup(
        modifier = modifier,
        title = {
            Column {
                Text(title)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) {
        availableStrategies.forEachIndexed { i, strategy ->
            val selected by remember(selectedStrategy) {
                derivedStateOf {
                    selectedStrategy == strategy
                }
            }
            val bottomBarThickness by remember(availableStrategies) {
                derivedStateOf {
                    if (i == availableStrategies.count() - 1) {
                        0.dp
                    } else {
                        MDFieldDefaults.horizontalDividerThickness
                    }
                }
            }
            MDField(
                onClick = { onSelectStrategy(strategy) },
                leadingIcon = { RadioButton(selected = selected, onClick = null) },
                trailingIcon = {},
                bottomHorizontalDividerThickness = bottomBarThickness
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
                MDFileStrategyRadioGroup(
                    selectedStrategy = MDFileStrategy.Ignore,
                    onSelectStrategy = {},
                    title = "Corrupted File",
                )
            }
        }
    }
}