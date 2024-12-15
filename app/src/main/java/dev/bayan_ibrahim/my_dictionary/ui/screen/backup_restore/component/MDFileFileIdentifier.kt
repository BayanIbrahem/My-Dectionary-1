package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDropDownMenu
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDDropDownMenuDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.toAnnotatedString
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileType
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.endOnly
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.currentOutlinedPainter
import dev.bayan_ibrahim.my_dictionary.ui.theme.startOnly

@Composable
fun MDFileFileIdentifier(
    selectedFileName: String?,
    selectedFileType: MDFileType?,
    detectedFileType: MDFileType?,
    fileInputFieldClickable: Boolean,
    overrideFileTypeChecked: Boolean,
    overrideFileTypeEnabled: Boolean,
    onClickFileInputField: () -> Unit,
    onSelectFileType: (MDFileType?) -> Unit,
    onOverrideFileTypeCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    validFile: Boolean = false,
    fileValidationInProgress: Boolean = true,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MDTextFieldDefaults.labelStyle,
        )
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.Bottom
        ) {
            FileNameInputField(
                selectedFile = selectedFileName,
                onClick = onClickFileInputField,
                clickable = fileInputFieldClickable,
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
            )
            FileTypeDropDown(
                selectedType = selectedFileType,
                detectedType = detectedFileType,
                onSelectType = onSelectFileType,
                readOnly = !overrideFileTypeChecked
            )
        }
        Column(
            modifier = Modifier.padding(start = 16.dp),
        ) {
            HorizontalDivider()
            Row {
                FileOverrideFileTypeCheckbox(
                    checked = overrideFileTypeChecked,
                    enabled = overrideFileTypeEnabled,
                    onChickChange = onOverrideFileTypeCheckChange,
                )
                Spacer(modifier = Modifier.weight(1f))
                AnimatedVisibility(
                    fileValidationInProgress && !selectedFileName.isNullOrBlank()
                ) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 1.dp
                        )
                        Text(
                            text = "Validating...", // TODO, string res
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = !validFile && !selectedFileName.isNullOrBlank(),
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Text(
                    text = "This is invalid file, try changing the file type or choose another file",
                    color = MaterialTheme.colorScheme.error,
                    style = MDTextFieldDefaults.labelStyle
                )
            }
        }
    }
}

@Composable
private fun FileOverrideFileTypeCheckbox(
    checked: Boolean,
    enabled: Boolean,
    onChickChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = enabled,
                onClick = {
                    onChickChange(!checked)
                }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            modifier = Modifier,
            checked = checked,
            enabled = enabled,
            onCheckedChange = null,
        )
        Text(
            text = "Override type", // TODO, string res
            style = MaterialTheme.typography.bodyMedium,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun FileNameInputField(
    selectedFile: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    clickable: Boolean = false,
) {
    MDBasicTextField(
        value = selectedFile ?: "",
        label = label,
        onValueChange = {},
        modifier = modifier
            .clip(MDTextFieldDefaults.shape.startOnly)
            .clickable(
                enabled = clickable,
                onClick = onClick
            ),
        readOnly = true,
        enabled = false,
        placeholder = if (clickable) "Click to select a file" else "Selecting file not allowed", // TODO, string res
        shape = MaterialTheme.shapes.medium.startOnly,
        hasBottomHorizontalDivider = false,
    )
}

@Composable
private fun FileTypeDropDown(
    selectedType: MDFileType?,
    detectedType: MDFileType?,
    onSelectType: (MDFileType?) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
//    enabled: Boolean = true,
) {
    val suggestions by remember(selectedType, detectedType) {
        derivedStateOf {
            setOfNotNull(
                detectedType,
                *MDFileType.entries.toTypedArray(),
            ).toList()
        }
    }
    MDBasicDropDownMenu(
        value = selectedType,
        onValueChange = {},
        suggestions = suggestions,
        selectedSuggestionAnnotatedTitle = {
            typeExtension.toAnnotatedString()
        },
        suggestionAnnotatedTitle = {
            buildAnnotatedString {
                // TODO, reformat detected option
                append("$typeName ($typeExtension)")
                if (detectedType == this@MDBasicDropDownMenu) {
                    append(" ")
                    this.pushStyle(
                        style = SpanStyle(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    append("Detected")
                }
            }
        },
        allowCancelSelection = false,
        onSelectSuggestion = { i, type ->
            onSelectType(type)
        },
        modifier = modifier,
        fieldModifier = Modifier.requiredWidth(120.dp),
        trailingIcons = {
            Icon(
                MDIconsSet.ArrowDropdown.currentOutlinedPainter,
                contentDescription = null
            )
        },
        menuMatchFieldWidth = false,
        fieldReadOnly = true,
        menuReadOnly = readOnly,
//        enabled = enabled,
        fieldShape = MDDropDownMenuDefaults.fieldShape.endOnly
    )
}

@Preview
@Composable
private fun MDFileFileIdentifierPreview() {
    MyDictionaryTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDFileFileIdentifier(
                    label = "source file",
                    selectedFileName = "File name",
                    selectedFileType = MDFileType.CSV,
                    detectedFileType = MDFileType.CSV,
                    fileInputFieldClickable = true,
                    overrideFileTypeChecked = true,
                    overrideFileTypeEnabled = true,
                    onClickFileInputField = {},
                    onSelectFileType = {},
                    onOverrideFileTypeCheckChange = {},
                )
            }
        }
    }
}