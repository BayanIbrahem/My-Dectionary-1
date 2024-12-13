package dev.bayan_ibrahim.my_dictionary.core.design_system.horizontal_card

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDUiScope

fun MDUiScope<MDHorizontalCardScopeItem>.item(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    theme: MDFieldsGroupTheme? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
) = item(
    item = MDHorizontalCardScopeItem(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        onLongClick = onLongClick,
        theme = theme,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        subtitle = subtitle,
        title = title
    )
)

fun MDUiScope<MDHorizontalCardScopeItem>.radioItem(
    selected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    theme: MDFieldsGroupTheme? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
) = item(
    item = MDHorizontalCardScopeItem(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        onLongClick = null,
        theme = theme,
        leadingIcon = {
            RadioButton(selected = selected, onClick = null)
        },
        trailingIcon = trailingIcon,
        subtitle = subtitle,
        title = title
    )
)

fun MDUiScope<MDHorizontalCardScopeItem>.checkboxItem(
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    theme: MDFieldsGroupTheme? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
) = item(
    item = MDHorizontalCardScopeItem(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        onLongClick = null,
        theme = theme,
        leadingIcon = leadingIcon,
        trailingIcon = {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
//                    checkedColor = contentColor,
                )
            )
        },
        subtitle = subtitle,
        title = title
    )
)
