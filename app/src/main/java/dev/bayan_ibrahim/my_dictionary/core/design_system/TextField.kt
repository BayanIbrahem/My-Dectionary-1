package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDImeAction
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

object MDTextFieldDefaults {
    val shape: CornerBasedShape = RoundedCornerShape(16.dp)
    val textStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.bodyMedium
    val labelStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.labelLarge

    @Composable
    fun colors(
        focusedTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        unfocusedTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTextColor: Color = Color.Unspecified,
        errorTextColor: Color = Color.Unspecified,
        focusedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
        unfocusedContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
        disabledContainerColor: Color = Color.Unspecified,
        errorContainerColor: Color = Color.Unspecified,
        cursorColor: Color = Color.Unspecified,
        errorCursorColor: Color = Color.Unspecified,
        selectionColors: TextSelectionColors? = null,
        focusedIndicatorColor: Color = Color.Transparent,
        unfocusedIndicatorColor: Color = Color.Transparent,
        disabledIndicatorColor: Color = Color.Unspecified,
        errorIndicatorColor: Color = Color.Unspecified,
        focusedLeadingIconColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        unfocusedLeadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor: Color = Color.Unspecified,
        errorLeadingIconColor: Color = Color.Unspecified,
        focusedTrailingIconColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        unfocusedTrailingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor: Color = Color.Unspecified,
        errorTrailingIconColor: Color = Color.Unspecified,
        focusedLabelColor: Color = Color.Unspecified,
        unfocusedLabelColor: Color = Color.Unspecified,
        disabledLabelColor: Color = Color.Unspecified,
        errorLabelColor: Color = Color.Unspecified,
        focusedPlaceholderColor: Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
        unfocusedPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        disabledPlaceholderColor: Color = Color.Unspecified,
        errorPlaceholderColor: Color = Color.Unspecified,
        focusedSupportingTextColor: Color = Color.Unspecified,
        unfocusedSupportingTextColor: Color = Color.Unspecified,
        disabledSupportingTextColor: Color = Color.Unspecified,
        errorSupportingTextColor: Color = Color.Unspecified,
        focusedPrefixColor: Color = Color.Unspecified,
        unfocusedPrefixColor: Color = Color.Unspecified,
        disabledPrefixColor: Color = Color.Unspecified,
        errorPrefixColor: Color = Color.Unspecified,
        focusedSuffixColor: Color = Color.Unspecified,
        unfocusedSuffixColor: Color = Color.Unspecified,
        disabledSuffixColor: Color = Color.Unspecified,
        errorSuffixColor: Color = Color.Unspecified,
    ): TextFieldColors = TextFieldDefaults.colors(
        focusedTextColor = focusedTextColor,
        unfocusedTextColor = unfocusedTextColor,
        disabledTextColor = disabledTextColor,
        errorTextColor = errorTextColor,
        focusedContainerColor = focusedContainerColor,
        unfocusedContainerColor = unfocusedContainerColor,
        disabledContainerColor = disabledContainerColor,
        errorContainerColor = errorContainerColor,
        cursorColor = cursorColor,
        errorCursorColor = errorCursorColor,
        selectionColors = selectionColors,
        focusedIndicatorColor = focusedIndicatorColor,
        unfocusedIndicatorColor = unfocusedIndicatorColor,
        disabledIndicatorColor = disabledIndicatorColor,
        errorIndicatorColor = errorIndicatorColor,
        focusedLeadingIconColor = focusedLeadingIconColor,
        unfocusedLeadingIconColor = unfocusedLeadingIconColor,
        disabledLeadingIconColor = disabledLeadingIconColor,
        errorLeadingIconColor = errorLeadingIconColor,
        focusedTrailingIconColor = focusedTrailingIconColor,
        unfocusedTrailingIconColor = unfocusedTrailingIconColor,
        disabledTrailingIconColor = disabledTrailingIconColor,
        errorTrailingIconColor = errorTrailingIconColor,
        focusedLabelColor = focusedLabelColor,
        unfocusedLabelColor = unfocusedLabelColor,
        disabledLabelColor = disabledLabelColor,
        errorLabelColor = errorLabelColor,
        focusedPlaceholderColor = focusedPlaceholderColor,
        unfocusedPlaceholderColor = unfocusedPlaceholderColor,
        disabledPlaceholderColor = disabledPlaceholderColor,
        errorPlaceholderColor = errorPlaceholderColor,
        focusedSupportingTextColor = focusedSupportingTextColor,
        unfocusedSupportingTextColor = unfocusedSupportingTextColor,
        disabledSupportingTextColor = disabledSupportingTextColor,
        errorSupportingTextColor = errorSupportingTextColor,
        focusedPrefixColor = focusedPrefixColor,
        unfocusedPrefixColor = unfocusedPrefixColor,
        disabledPrefixColor = disabledPrefixColor,
        errorPrefixColor = errorPrefixColor,
        focusedSuffixColor = focusedSuffixColor,
        unfocusedSuffixColor = unfocusedSuffixColor,
        disabledSuffixColor = disabledSuffixColor,
        errorSuffixColor = errorSuffixColor
    )
}

@Composable
fun MDBasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String = "",
    minLines: Int = 1,
    maxLines: Int = 3,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    leadingIcons: (@Composable RowScope.() -> Unit)? = null,
    trailingIcons: (@Composable RowScope.() -> Unit)? = null,
    imeAction: MDImeAction = MDImeAction.Done,
    onKeyboardAction: KeyboardActionScope.() -> Unit = {},
    focusManager: FocusManager = LocalFocusManager.current,
    colors: TextFieldColors = MDTextFieldDefaults.colors(),
    shape: CornerBasedShape = MDTextFieldDefaults.shape,
    textStyle: TextStyle = MDTextFieldDefaults.textStyle,
    labelStyle: TextStyle = MDTextFieldDefaults.labelStyle,
    hasBottomHorizontalDivider: Boolean = false,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Min),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = labelStyle,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .basicMarquee(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        val keyboardActions by remember {
            derivedStateOf {
                imeAction.getKeyboardAction(
                    focusManager = focusManager,
                    onKeyboardAction = onKeyboardAction
                )
            }
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            minLines = minLines,
            maxLines = maxLines,
            readOnly = readOnly,
            enabled = enabled,
            placeholder = {
                Text(
                    text = placeholder,
                    maxLines = maxLines,
                    minLines = minLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee(),
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = imeAction.imeAction),
            keyboardActions = keyboardActions,
            colors = colors,
            textStyle = textStyle,
            prefix = prefix,
            suffix = suffix,
            leadingIcon = leadingIcons?.let {
                {
                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                    ) {
                        leadingIcons()
                    }
                }
            },
            trailingIcon = trailingIcons?.let {
                {
                    Row(
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        trailingIcons()
                    }
                }
            }
        )
        if (hasBottomHorizontalDivider) {
            HorizontalDivider()
        }
    }
}

@Preview
@Composable
private fun MDTextFieldPreview() {
    var value by remember {
        mutableStateOf("")
    }
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                MDBasicTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = "label",
                    placeholder = "Place holder",
                    hasBottomHorizontalDivider = true,
                    leadingIcons = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(36.dp)
                        ) {
                            MDIcon(MDIconsSet.Share) 
                        }
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(36.dp)
                        ) {
                            MDIcon(MDIconsSet.Share) 
                        }
                    },
                    trailingIcons = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(36.dp)
                        ) {
                            MDIcon(MDIconsSet.Share) 
                        }
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(36.dp)
                        ) {
                            MDIcon(MDIconsSet.Share) 
                        }
                    },
                )
            }
        }
    }
}