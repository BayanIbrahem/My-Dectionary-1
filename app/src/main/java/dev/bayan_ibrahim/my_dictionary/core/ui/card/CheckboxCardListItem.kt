package dev.bayan_ibrahim.my_dictionary.core.ui.card

import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LocalMDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem

/**
 * checkbox button version of [MDCard2ListItem], where the leading or trailing is a checkbox button
 * and have [onCheckedChange]
 * to toggle [checked] state.
 * @param onCheckedChange required action for toggling state
 * @param checked trigger where this is selected or not
 * @param leadingCheckbox whether the radio button is leading or trailing, and in both cases, [secondary]
 * is on the opposite side of the radio
 * @param secondary what is the content in the opposite side of the checkbox
 * - when [leadingCheckbox] is `true` then [secondary] is the trailing icon
 * - when [leadingCheckbox] is `false` then [secondary] is the leading icon
 * @param checkedTheme default value is [theme], used if the selected value needed to be different than unselected value
 * @see MDCard2ListItem
 */
@Composable
fun MDCard2CheckboxItem(
    checked: Boolean,
    modifier: Modifier = Modifier,
    secondary: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    checkedTheme: MDCard2ListItemTheme = theme,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    leadingCheckbox: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    MDCard2SelectableItem(
        checked = checked,
        onClick = onCheckedChange?.let { callback ->
            { checked ->
                callback(!checked)
            }
        },
        modifier = modifier,
        leading = if (leadingCheckbox) {
            {
                Check(checked)
            }
        } else secondary,
        trailing = if (leadingCheckbox) secondary else {
            {
                Check(checked)
            }
        },
        subtitle = subtitle,
        theme = theme,
        checkedTheme = checkedTheme,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        title = title,
    )
}


/**
 * checkbox button version of [MDCard2ListItem], where the leading or trailing is a checkbox button
 * and have [onCheckedChange]
 * to toggle [checked] state.
 * @param onCheckedChange required action for toggling state
 * @param checked trigger where this is selected or not
 * @param leadingCheckbox whether the radio button is leading or trailing, and in both cases, [secondary]
 * is on the opposite side of the radio
 * @param secondary what is the content in the opposite side of the checkbox
 * - when [leadingCheckbox] is `true` then [secondary] is the trailing icon
 * - when [leadingCheckbox] is `false` then [secondary] is the leading icon
 * @param checkedTheme default value is [theme], used if the selected value needed to be different than unselected value
 * @see MDCard2ListItem
 */
@Composable
fun MDCard2CheckboxItem(
    checked: Boolean,
    title: String,
    modifier: Modifier = Modifier,
    secondary: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    checkedTheme: MDCard2ListItemTheme = theme,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    leadingCheckbox: Boolean = true,
) {
    MDCard2SelectableItem(
        checked = checked,
        onClick = onCheckedChange?.let { callback ->
            { checked ->
                callback(!checked)
            }
        },
        modifier = modifier,
        leading = if (leadingCheckbox) {
            { Check(checked) }
        } else secondary,
        trailing = if (leadingCheckbox) secondary else {
            { Check(checked) }
        },
        subtitle = subtitle,
        theme = theme,
        checkedTheme = checkedTheme,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        title = title
    )
}

@Composable
private fun Check(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    Checkbox(
        modifier = modifier,
        checked = checked,
        onCheckedChange = null,
    )
}
