package dev.bayan_ibrahim.my_dictionary.core.ui.card

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.action.MDCard2Action

@Composable
fun RowScope.MDCard2ImportantAction(
    modifier: Modifier = Modifier,
    label: String = firstCapStringResource(R.string.delete),
    theme: MDCard2ListItemTheme = MDCard2ListItemTheme.ErrorOnSurface,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    MDCard2Action(
        label = label,
        onClick = onClick,
        modifier = modifier.weight(1f),
        enabled = enabled,
        theme = theme,
    )
}

@Composable
fun RowScope.MDCard2ConfirmAction(
    modifier: Modifier = Modifier,
    label: String = firstCapStringResource(R.string.confirm),
    theme: MDCard2ListItemTheme = MDCard2ListItemTheme.PrimaryOnSurface,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    MDCard2Action(
        label = label,
        onClick = onClick,
        modifier = modifier.weight(1f),
        enabled = enabled,
        theme = theme,
    )
}

@Composable
fun RowScope.MDCard2CancelAction(
    modifier: Modifier = Modifier,
    label: String = firstCapStringResource(R.string.cancel),
    theme: MDCard2ListItemTheme = MDCard2ListItemTheme.SurfaceVariant,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    MDCard2Action(
        label = label,
        onClick = onClick,
        modifier = modifier.weight(1f),
        enabled = enabled,
        theme = theme,
    )
}
