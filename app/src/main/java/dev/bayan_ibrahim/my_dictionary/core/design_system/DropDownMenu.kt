package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDImeAction
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Stable
data object MDDropDownMenuDefaults {
    val fieldColors: TextFieldColors
        @Composable
        get() = MDTextFieldDefaults.colors()

    @Composable
    fun menuColors(
        fieldColors: TextFieldColors = this.fieldColors,
    ): MDMenuColors = MDMenuColors(
        menuColor = Color.Transparent,
        itemContainerColor = fieldColors.focusedContainerColor,
        itemContentColor = fieldColors.focusedTextColor,
        disabledItemContainerColor = fieldColors.unfocusedContainerColor,
        disabledItemContentColor = fieldColors.unfocusedTextColor
    )
}

data class MDMenuColors(
    val menuColor: Color,
    val itemContainerColor: Color,
    val itemContentColor: Color,
    val disabledItemContainerColor: Color,
    val disabledItemContentColor: Color,
) {
    val cardColors = CardColors(
        containerColor = itemContainerColor,
        contentColor = itemContentColor,
        disabledContainerColor = disabledItemContainerColor,
        disabledContentColor = disabledItemContentColor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <Data : Any> MDBasicDropDownMenu(
    value: Data?,
    onValueChange: (String) -> Unit,
    suggestions: List<Data>,
    suggestionTitle: @Composable Data.() -> String,
    onSelectSuggestion: (Int, Data?) -> Unit,
    modifier: Modifier = Modifier,
    suggestionSubtitle: @Composable Data.() -> String? = { null },
    fieldModifier: Modifier = Modifier,
    placeholder: String = "",
    label: String = "",
    minLines: Int = 1,
    maxLines: Int = 3,
    fieldReadOnly: Boolean = false,
    menuReadOnly: Boolean = false,
    enabled: Boolean = true,
    leadingIcons: (@Composable RowScope.() -> Unit)? = null,
    trailingIcons: (@Composable RowScope.() -> Unit)? = null,
    imeAction: MDImeAction = MDImeAction.Done,
    onKeyboardAction: KeyboardActionScope.() -> Unit = {},
    focusManager: FocusManager = LocalFocusManager.current,
    fieldColors: TextFieldColors = MDDropDownMenuDefaults.fieldColors,
    menuColors: MDMenuColors = MDDropDownMenuDefaults.menuColors(),
    textStyle: TextStyle = MDTextFieldDefaults.textStyle,
    labelStyle: TextStyle = MDTextFieldDefaults.labelStyle,
    hasBottomHorizontalDivider: Boolean = false,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    allowCancelSelection: Boolean = true,
) {
    var showDropDownMenu by remember {
        mutableStateOf(false)
    }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = showDropDownMenu,
        onExpandedChange = {
            showDropDownMenu = it && !menuReadOnly
        }
    ) {
        val fieldValue = value?.suggestionTitle() ?: ""
        MDBasicTextField(
            value = fieldValue,
            onValueChange = {
                onValueChange(it)
            },
            modifier = fieldModifier
                .menuAnchor(MenuAnchorType.PrimaryEditable),
            placeholder = placeholder,
            label = label,
            minLines = minLines,
            maxLines = maxLines,
            readOnly = fieldReadOnly,
            enabled = enabled,
            leadingIcons = leadingIcons,
            trailingIcons = trailingIcons,
            imeAction = imeAction,
            onKeyboardAction = onKeyboardAction,
            focusManager = focusManager,
            colors = fieldColors,
            textStyle = textStyle,
            labelStyle = labelStyle,
            hasBottomHorizontalDivider = hasBottomHorizontalDivider,
            prefix = prefix,
            suffix = suffix
        )
        ExposedDropdownMenu(
            expanded = showDropDownMenu,
            onDismissRequest = {
                showDropDownMenu = false
            },
            containerColor = menuColors.menuColor,
        ) {
            Box {
                Column(
                    modifier = modifier,
                ) {
                    if (allowCancelSelection) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                onSelectSuggestion(0, null)
                                onValueChange(INVALID_TEXT)
                                showDropDownMenu = false
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = menuColors.cardColors,
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = "Cancel selection",/* TODO, string res */
                                    modifier = Modifier.padding(8.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
                                )
                            }
                        }
                    }
                    suggestions.forEachIndexed { i, suggestion ->
                        val title = suggestion.suggestionTitle()
                        val subtitle = suggestion.suggestionSubtitle()
                        MDMenuItem(
                            title = title,
                            subtitle = subtitle,
                            cardColors = menuColors.cardColors,
                            onClick = {
                                onSelectSuggestion(i, suggestion)
                                onValueChange(title)
                                showDropDownMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MDMenuItem(
    title: String,
    subtitle: String?,
    cardColors: CardColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = cardColors,
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(8.dp)
                .basicMarquee(),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
        )
        if (subtitle != null) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(8.dp)
                    .basicMarquee(),
                style = MaterialTheme.typography.bodySmall,
                color = cardColors.contentColor.copy(alpha = 0.62f),
                maxLines = 1,
            )
        }
    }
}

@Preview
@Composable
private fun JPBasicDropDownPreview() {
    MyDictionaryTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var value1 by remember {
                    mutableStateOf("")
                }
                MDBasicDropDownMenu<String>(
                    value = value1,
                    onValueChange = { value1 = it },
                    fieldReadOnly = true,
                    placeholder = "placeholder, large read only",
                    suggestions = List(3) {
                        "item $it"
                    },
                    onSelectSuggestion = { i, suggestion -> },
                    suggestionTitle = { this },
                    suggestionSubtitle = { this }
                )
                var value2 by remember {
                    mutableStateOf("")
                }
                MDBasicDropDownMenu(
                    value = value2,
                    onValueChange = { value2 = it },
                    fieldReadOnly = false,
                    placeholder = "placeholder, editable medium",
                    suggestions = List(3) { "item $it" },
                    onSelectSuggestion = { i, suggestion -> },
                    suggestionTitle = { this },
                    suggestionSubtitle = { this }
                )
            }
        }
    }
}
