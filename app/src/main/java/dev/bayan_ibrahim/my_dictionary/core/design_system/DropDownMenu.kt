package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDImeAction
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDField
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDFieldsGroup
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDFieldsGroupColors
import dev.bayan_ibrahim.my_dictionary.core.design_system.group.MDFieldsGroupDefaults
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Stable
data object MDDropDownMenuDefaults {
    val fieldShape: CornerBasedShape
        @Composable
        get() = MDTextFieldDefaults.shape
    val fieldColors: TextFieldColors
        @Composable
        get() = MDTextFieldDefaults.colors()

    val menuShape: CornerBasedShape
        @Composable
        get() = MDFieldsGroupDefaults.shape
    val menuColors: MDFieldsGroupColors
        @Composable
        get() = MDFieldsGroupDefaults.colors()
}


@JvmName("MBBasicDropDownMenuString")
@Composable
fun <Data : Any> MDBasicDropDownMenu(
    value: Data?,
    onValueChange: (String) -> Unit,
    suggestions: List<Data>,
    suggestionTitle: @Composable Data.() -> String,
    selectedSuggestionTitle: (@Composable Data.() -> String)? = null,
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
    fieldShape: CornerBasedShape = MDDropDownMenuDefaults.fieldShape,
    menuColors: MDFieldsGroupColors = MDDropDownMenuDefaults.menuColors,
    textStyle: TextStyle = MDTextFieldDefaults.textStyle,
    labelStyle: TextStyle = MDTextFieldDefaults.labelStyle,
    hasBottomHorizontalDivider: Boolean = false,
    menuMatchFieldWidth: Boolean = true,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    allowCancelSelection: Boolean = true,
) {
    MDBasicDropDownMenu(
        value = value,
        onValueChange = onValueChange,
        suggestions = suggestions,
        suggestionAnnotatedTitle = { buildAnnotatedString { append(suggestionTitle()) } },
        selectedSuggestionAnnotatedTitle = selectedSuggestionTitle?.let {
            { buildAnnotatedString { append(selectedSuggestionTitle()) } }
        },
        onSelectSuggestion = onSelectSuggestion,
        modifier = modifier,
        suggestionAnnotatedSubtitle = { suggestionSubtitle()?.let { buildAnnotatedString { append(it) } } },
        fieldModifier = fieldModifier,
        placeholder = placeholder,
        label = label,
        minLines = minLines,
        maxLines = maxLines,
        fieldReadOnly = fieldReadOnly,
        menuReadOnly = menuReadOnly,
        enabled = enabled,
        leadingIcons = leadingIcons,
        trailingIcons = trailingIcons,
        imeAction = imeAction,
        onKeyboardAction = onKeyboardAction,
        focusManager = focusManager,
        fieldColors = fieldColors,
        fieldShape = fieldShape,
        menuColors = menuColors,
        textStyle = textStyle,
        labelStyle = labelStyle,
        hasBottomHorizontalDivider = hasBottomHorizontalDivider,
        menuMatchFieldWidth = menuMatchFieldWidth,
        prefix = prefix,
        suffix = suffix,
        allowCancelSelection = allowCancelSelection
    )
}

fun String.toAnnotatedString() = AnnotatedString(this)

@JvmName("MBBasicDropDownMenuAnnotatedString")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <Data : Any> MDBasicDropDownMenu(
    value: Data?,
    onValueChange: (String) -> Unit,
    suggestions: List<Data>,
    suggestionAnnotatedTitle: @Composable Data.() -> AnnotatedString,
    selectedSuggestionAnnotatedTitle: (@Composable Data.() -> AnnotatedString)? = null,
    onSelectSuggestion: (Int, Data?) -> Unit,
    modifier: Modifier = Modifier,
    suggestionAnnotatedSubtitle: @Composable Data.() -> AnnotatedString? = { null },
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
    fieldShape: CornerBasedShape = MDDropDownMenuDefaults.fieldShape,
    menuColors: MDFieldsGroupColors = MDDropDownMenuDefaults.menuColors,
    menuShape: CornerBasedShape = MDDropDownMenuDefaults.menuShape,
    textStyle: TextStyle = MDTextFieldDefaults.textStyle,
    labelStyle: TextStyle = MDTextFieldDefaults.labelStyle,
    hasBottomHorizontalDivider: Boolean = false,
    menuMatchFieldWidth: Boolean = true,
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
        val selectedSuggestionTitleBuilder by remember(selectedSuggestionAnnotatedTitle != null) {
            derivedStateOf {
                selectedSuggestionAnnotatedTitle ?: suggestionAnnotatedTitle
            }
        }
        val fieldValue = value?.selectedSuggestionTitleBuilder() ?: buildAnnotatedString { append("") }
        MDBasicTextField(
            value = fieldValue.text,
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
            shape = fieldShape,
            textStyle = textStyle,
            labelStyle = labelStyle,
            hasBottomHorizontalDivider = hasBottomHorizontalDivider,
            prefix = prefix,
            suffix = suffix,
        )
        ExposedDropdownMenu(
            expanded = showDropDownMenu,
            onDismissRequest = {
                showDropDownMenu = false
            },
            containerColor = Color.Transparent,
            matchTextFieldWidth = menuMatchFieldWidth,
        ) {
            Box {
                MDFieldsGroup(
                    modifier = modifier.width(IntrinsicSize.Max),
                    colors = menuColors,
                    shape = menuShape,
                ) {
                    if (allowCancelSelection) {
                        MDField(
                            trailingIcon = {},
                            onClick = {
                                onSelectSuggestion(0, null)
                                onValueChange(INVALID_TEXT)
                                showDropDownMenu = false
                            },
                        ) {
                            Text(
                                text = "Cancel selection",/* TODO, string res */
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                            )
                        }
                    }
                    suggestions.forEachIndexed { i, suggestion ->
                        val title = suggestion.suggestionAnnotatedTitle()
                        val subtitle = suggestion.suggestionAnnotatedSubtitle()
                        MDField(
                            onClick = {
                                onSelectSuggestion(i, suggestion)
                                onValueChange(title.text)
                                showDropDownMenu = false
                            }
                        ) {
                            Column {
                                Text(title)
                                subtitle?.let { Text(it) }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun MDMenuItem(
    title: AnnotatedString,
    subtitle: AnnotatedString?,
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
private fun MDBasicDropDownPreview() {
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
                    suggestions = List(3) { "item $it" },
                    suggestionTitle = { this },
                    fieldReadOnly = false,
                    placeholder = "placeholder, editable medium",
                    onSelectSuggestion = { i, suggestion -> },
                    suggestionSubtitle = { this }
                )
            }
        }
    }
}
