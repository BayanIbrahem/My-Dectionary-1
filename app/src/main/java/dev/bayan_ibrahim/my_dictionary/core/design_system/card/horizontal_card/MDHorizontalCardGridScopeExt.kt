package dev.bayan_ibrahim.my_dictionary.core.design_system.card.horizontal_card

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun MDHorizontalCardGridScope.item(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    colors: MDHorizontalCardColors? = null,
    styles: MDHorizontalCardStyles? = null,
    span: (LazyGridItemSpanScope.(currentIndex: Int) -> GridItemSpan)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
) = item(
    item = MDHorizontalCardGridScopeItem(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        onLongClick = onLongClick,
        colors = colors,
        styles = styles,
        span = span,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        subtitle = subtitle,
        title = title
    )
)

fun MDHorizontalCardGridScope.radioItem(
    selected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    colors: MDHorizontalCardColors? = null,
    styles: MDHorizontalCardStyles? = null,
    span: (LazyGridItemSpanScope.(currentIndex: Int) -> GridItemSpan)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
) = item(
    item = MDHorizontalCardGridScopeItem(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        onLongClick = null,
        colors = colors,
        styles = styles,
        span = span,
        leadingIcon = {
            RadioButton(
                selected = selected,
                onClick = null,
                colors = colors?.let {
                    RadioButtonDefaults.colors(
                        unselectedColor = if (enabled) {
                            it.enabledTrailingIconColor
                        } else {
                            it.disabledTrailingIconColor
                        },
                    )
                } ?: RadioButtonDefaults.colors()
            )
        },
        trailingIcon = trailingIcon,
        subtitle = subtitle,
        title = title
    )
)

fun MDHorizontalCardGridScope.checkboxItem(
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    colors: MDHorizontalCardColors? = null,
    styles: MDHorizontalCardStyles? = null,
    span: (LazyGridItemSpanScope.(currentIndex: Int) -> GridItemSpan)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
) = item(
    item = MDHorizontalCardGridScopeItem(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        onLongClick = null,
        colors = colors,
        styles = styles,
        span = span,
        leadingIcon = leadingIcon,
        trailingIcon = {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                colors = colors?.let {
                    CheckboxDefaults.colors(
                        uncheckedColor = if (enabled) {
                            it.enabledTrailingIconColor
                        } else {
                            it.disabledTrailingIconColor
                        },
                    )
                } ?: CheckboxDefaults.colors()
            )
        },
        subtitle = subtitle,
        title = title
    )
)
