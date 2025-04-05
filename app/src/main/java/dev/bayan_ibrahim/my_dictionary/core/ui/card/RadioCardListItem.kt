package dev.bayan_ibrahim.my_dictionary.core.ui.card

import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.LocalMDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem

/**
 * checkbox button version of [MDCard2ListItem], where the leading or trailing is a checkbox button
 * and have [onClick]
 * to toggle [selected] state.
 * @param onClick required action for toggling state
 * @param selected trigger where this is selected or not
 * @param leadingRadioButton whether the radio button is leading or trailing, and in both cases, [secondary]
 * is on the opposite side of the radio
 * @param secondary what is the content in the opposite side of the checkbox
 * - when [leadingRadioButton] is `true` then [secondary] is the trailing icon
 * - when [leadingRadioButton] is `false` then [secondary] is the leading icon
 * @param selectedTheme default value is [theme], used if the selected value needed to be different than unselected value
 * @see MDCard2ListItem
 */
@Composable
fun MDCard2RadioButtonItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondary: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    selectedTheme: MDCard2ListItemTheme = theme,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    leadingRadioButton: Boolean = true,
    title: @Composable () -> Unit,
) {
    MDCard2SelectableItem(
        checked = selected,
        onClick = { onClick() },
        modifier = modifier,
        leading = if (leadingRadioButton) {
            {
                Radio(selected)
            }
        } else secondary,
        trailing = if (leadingRadioButton) secondary else {
            {
                Radio(selected)
            }
        },
        subtitle = subtitle,
        theme = theme,
        checkedTheme = selectedTheme,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        title = title,
    )
}


/**
 * checkbox button version of [MDCard2ListItem], where the leading or trailing is a checkbox button
 * and have [onClick]
 * to toggle [selected] state.
 * @param onClick required action for toggling state
 * @param selected trigger where this is selected or not
 * @param leadingRadioButton whether the radio button is leading or trailing, and in both cases, [secondary]
 * is on the opposite side of the radio
 * @param secondary what is the content in the opposite side of the checkbox
 * - when [leadingRadioButton] is `true` then [secondary] is the trailing icon
 * - when [leadingRadioButton] is `false` then [secondary] is the leading icon
 * @param selectedTheme default value is [theme], used if the selected value needed to be different than unselected value
 * @see MDCard2ListItem
 */
@Composable
fun MDCard2RadioButtonItem(
    selected: Boolean,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondary: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
    theme: MDCard2ListItemTheme = LocalMDCard2ListItemTheme.current,
    selectedTheme: MDCard2ListItemTheme = theme,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    leadingRadioButton: Boolean = true,
) {
    MDCard2SelectableItem(
        checked = selected,
        onClick = { onClick() },
        modifier = modifier,
        leading = if (leadingRadioButton) {
            { Radio(selected) }
        } else secondary,
        trailing = if (leadingRadioButton) secondary else {
            { Radio(selected) }
        },
        subtitle = subtitle,
        theme = theme,
        checkedTheme = selectedTheme,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        title = title
    )
}

@Composable
private fun Radio(
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    RadioButton(
        modifier = modifier,
        selected = selected,
        onClick = null,
    )
}
