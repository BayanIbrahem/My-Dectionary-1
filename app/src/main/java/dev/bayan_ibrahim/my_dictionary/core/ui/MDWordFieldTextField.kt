package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDImeAction
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDropDownMenu
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.MyDictionaryTheme

@Composable
fun MDWordFieldTextField(
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector?,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = label,
    showLabelOnEditMode: Boolean = false,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    focusManager: FocusManager = LocalFocusManager.current,
    onFocusEvent: (FocusState) -> Unit = {},
    colors: TextFieldColors = MDTextFieldDefaults.colors(),
    hasBottomHorizontalDivider: Boolean = false,
    textStyle: TextStyle = MDTextFieldDefaults.textStyle,
    index: Int? = null,
    indexFormat: (Int) -> String = { "$it. " },
    imeAction: MDImeAction = MDImeAction.Done,
    onKeyboardAction: KeyboardActionScope.() -> Unit = {},
) {
    var isFocused by remember {
        mutableStateOf(false)
    }
    val prefixColor by animateColorAsState(
        targetValue = if (isFocused) colors.focusedTextColor else colors.unfocusedTextColor,
        label = "prefix color"
    )
    val textFieldLabel by remember(readOnly, showLabelOnEditMode) {
        derivedStateOf { if (readOnly || showLabelOnEditMode) label else "" }
    }
    MDBasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.onFocusEvent {
            isFocused = it.isFocused
            onFocusEvent(it)
        },
        maxLines = 1,
        placeholder = placeholder,
        label = textFieldLabel,
        readOnly = readOnly,
        enabled = enabled,
        hasBottomHorizontalDivider = hasBottomHorizontalDivider,
        prefix = index?.let {
            {
                FieldPrefix(
                    indexFormat = indexFormat,
                    index = index,
                    textStyle = textStyle,
                    prefixColor = prefixColor
                )
            }
        },
        leadingIcons = leadingIcon?.let {
            {
                FieldLeadingIcons(leadingIcon)
            }
        },
        trailingIcons = fieldTrailingIcons(
            isFocused = isFocused,
            onValueChange = onValueChange,
            focusManager = focusManager
        ),
        colors = colors,
        textStyle = textStyle,
        imeAction = imeAction,
        onKeyboardAction = onKeyboardAction,
        focusManager = focusManager,
    )
}

@Composable
fun <Data : Any> MDWordFieldTextField(
    value: Data?,
    onValueChange: (String) -> Unit,
    suggestions: List<Data>,
    suggestionTitle: @Composable Data.() -> String,
    onSelectSuggestion: (Int, Data?) -> Unit,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    suggestionSubtitle: @Composable Data.() -> String? = { null },
    fieldModifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = label,
    showLabelOnPreviewMode: Boolean = false,
    fieldReadOnly: Boolean = false,
    menuReadOnly: Boolean = false,
    enabled: Boolean = true,
    focusManager: FocusManager = LocalFocusManager.current,
    onFocusEvent: (FocusState) -> Unit = {},
    colors: TextFieldColors = MDTextFieldDefaults.colors(),
    hasBottomHorizontalDivider: Boolean = false,
    textStyle: TextStyle = MDTextFieldDefaults.textStyle,
    index: Int? = null,
    indexFormat: (Int) -> String = { "$it. " },
    imeAction: MDImeAction = MDImeAction.Done,
    onKeyboardAction: KeyboardActionScope.() -> Unit = {},
    allowCancelSelection: Boolean = true
) {
    var isFocused by remember {
        mutableStateOf(false)
    }
    val prefixColor by animateColorAsState(
        targetValue = if (isFocused) colors.focusedTextColor else colors.unfocusedTextColor,
        label = "prefix color"
    )
    val textFieldLabel by remember(fieldReadOnly, showLabelOnPreviewMode) {
        derivedStateOf { if (fieldReadOnly || showLabelOnPreviewMode) label else "" }
    }
    MDBasicDropDownMenu(
        value = value,
        suggestions = suggestions,
        onValueChange = onValueChange,
        onSelectSuggestion = onSelectSuggestion,
        suggestionTitle = suggestionTitle,
        suggestionSubtitle = suggestionSubtitle,
        modifier = modifier,
        fieldModifier = fieldModifier.onFocusEvent {
            isFocused = it.isFocused
            onFocusEvent(it)
        },
        maxLines = 1,
        placeholder = placeholder,
        label = textFieldLabel,
        fieldReadOnly = fieldReadOnly,
        menuReadOnly = menuReadOnly,
        enabled = enabled,
        hasBottomHorizontalDivider = hasBottomHorizontalDivider,
        prefix = index?.let {
            {
                FieldPrefix(
                    indexFormat = indexFormat,
                    index = index,
                    textStyle = textStyle,
                    prefixColor = prefixColor
                )
            }
        },
        leadingIcons = {
            FieldLeadingIcons(leadingIcon)
        },
        trailingIcons = fieldTrailingIcons(
            isFocused = isFocused,
            onValueChange = onValueChange,
            focusManager = focusManager
        ),
        fieldColors = colors,
        textStyle = textStyle,
        imeAction = imeAction,
        onKeyboardAction = onKeyboardAction,
        focusManager = focusManager,
        allowCancelSelection = allowCancelSelection
    )
}


@Composable
private fun FieldPrefix(
    indexFormat: (Int) -> String,
    index: Int,
    textStyle: TextStyle,
    prefixColor: Color,
) {
    Text(
        text = indexFormat(index),
        style = textStyle,
        color = prefixColor,
    )
}

private fun fieldTrailingIcons(
    isFocused: Boolean,
    onValueChange: (String) -> Unit,
    focusManager: FocusManager,
): (@Composable RowScope.() -> Unit)? {
    return if (isFocused) {
        {
            // clear
            IconButton(
                onClick = {
                    onValueChange("")
                },
                modifier = Modifier.size(36.dp),
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
            IconButton(
                onClick = {
                    focusManager.clearFocus()
                },
                modifier = Modifier.size(36.dp),
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
            }
        }
    } else null
}

@Composable
private fun FieldLeadingIcons(leadingIcon: ImageVector) {
    Icon(imageVector = leadingIcon, contentDescription = null)
}

@Preview
@Composable
private fun MDWordFieldTextFieldPreview() {
    var value by remember { mutableStateOf("") }
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MDWordFieldTextField(
                        value = value,
                        onValueChange = { value = it },
                        leadingIcon = Icons.Default.Info,
                        placeholder = "Normal Field",
                        index = 1,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    MDWordFieldTextField(
                        value = value,
                        suggestions = List(10) {
                            "Item $it"
                        },
                        onValueChange = { value = it },
                        onSelectSuggestion = { index, suggestion ->

                        },
                        suggestionTitle = { this },
                        leadingIcon = Icons.Default.Info,
                        placeholder = "Field With suggestions",
                        index = 1,
                        fieldModifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}