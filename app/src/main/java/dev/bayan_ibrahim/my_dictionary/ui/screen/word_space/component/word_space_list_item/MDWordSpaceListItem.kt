package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableField
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableFieldStatus
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapPluralsResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDVerticalCard
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MDWordSpaceListItem(
    state: LanguageWordSpaceState,
    actions: LanguageWordSpaceActions,
    currentEditableLanguageCode: LanguageCode?,
    navigateToStatistics: (Language) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEditable by remember(currentEditableLanguageCode) {
        derivedStateOf {
            currentEditableLanguageCode?.let {
                state.code == it.code
            } ?: true
        }
    }
    LaunchedEffect(isEditable) {
        if (!isEditable && state.isEditModeOn)
        // TODO, throw an exception or maybe close the other one
            actions.onCancel()
    }
    var onConfirmEditField: (newValue: String) -> Unit by remember {
        mutableStateOf({})
    }
    var editingTextFieldInitialValue by remember {
        mutableStateOf("")
    }
    var isTagEditField by remember {
        mutableStateOf(true)
    }
    CompositionLocalProvider(
        LocalLayoutDirection provides state.direction
    ) {
        WordsSpaceFieldEditDialog(
            isWordClass = isTagEditField,
            showDialog = state.isEditDialogShown,
            onDismiss = actions::onHideDialog,
            onConfirm = { newValue ->
                onConfirmEditField(newValue)
            },
            getInitialValue = {
                editingTextFieldInitialValue
            }
        )
        MDVerticalCard(
            modifier = modifier,
            contentModifier = MDCardDefaults.contentModifier.padding(vertical = 8.dp),
            headerClickable = false,
            cardClickable = false,
            header = {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Text(
                        text = state.uppercaseCode,
                        style = if (state.isLongCode) {
                            MaterialTheme.typography.titleSmall
                        } else {
                            MaterialTheme.typography.titleLarge
                        },
                    )
                    Text(
                        text = buildAnnotatedString {
                            pushStyle(MaterialTheme.typography.bodyLarge.toSpanStyle())
                            append(state.selfDisplayName)
                            pushStyle(MaterialTheme.typography.labelSmall.toSpanStyle())
                            append(" ")
                            append(firstCapPluralsResource(R.plurals.word, state.wordsCount))
                        },
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else if (state.isEditModeOn) {
                        Row {
                            IconButton(
                                onClick = actions::onCancel,
                                enabled = isEditable,
                            ) {
                                MDIcon(MDIconsSet.Close)
                            }

                            IconButton(
                                onClick = actions::onSubmit,
                                enabled = isEditable,
                            ) {
                                MDIcon(MDIconsSet.Check)
                            }
                        }
                    } else {
                        Row {
                            IconButton(
                                onClick = {
                                    navigateToStatistics(state)
                                }
                            ) {
                                MDIcon(MDIconsSet.Statistics)
                            }
                            IconButton(
                                onClick = actions::onEnableEditMode,
                                enabled = isEditable,
                            ) {
                                MDIcon(MDIconsSet.Edit)
                            }
                        }
                    }
                }
            }
        ) {
            if (state.tags.isEmpty() && !state.isEditModeOn) {
                Text(
                    text = firstCapStringResource(R.string.no_word_class_hint),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Column(
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                state.tags.forEachIndexed { tagIndex, tag ->
                    WordSpaceEditableTagListItem(
                        label = {
                            val value =
                                if (state.isEditModeOn) tag.current.name else "${tag.current.name} x${tag.current.wordsCount}"
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            )
                        },
                        editMode = state.isEditModeOn,
                        status = tag.status,
                        onClick = {
                            onConfirmEditField = { newValue ->
                                actions.onEditTag(tagIndex, newValue)
                            }
                            editingTextFieldInitialValue = tag.current.name
                            isTagEditField = true
                            actions.onShowDialog()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onDelete = {
                            actions.onRemoveTag(tagIndex)
                        },
                        onReset = {
                            actions.onResetTag(tagIndex)
                        }
                    )
                    FlowRow(
                        modifier = Modifier.padding(start = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        tag.current.relations.forEachIndexed { relationIndex, relation ->
                            WordSpaceEditableTagListItem(
                                label = {
                                    val value =
                                        if (state.isEditModeOn) relation.label else "${relation.label} x${relation.wordsCount}"
                                    Text(
                                        text = value,
                                        style = MaterialTheme.typography.bodyMedium,
//                                    fontWeight = FontWeight.Bold,
//                                    fontStyle = FontStyle.Italic
                                    )
                                },
                                editMode = state.isEditModeOn,
                                status = actions.relationStatus(tagIndex, relationIndex),
                                onClick = {
                                    onConfirmEditField = { newValue ->
                                        actions.onEditTagRelation(tagIndex, relationIndex, newValue)
                                    }
                                    isTagEditField = false
                                    editingTextFieldInitialValue = relation.label
                                    actions.onShowDialog()
                                },
                                onDelete = {
                                    actions.onRemoveTagRelation(tagIndex, relationIndex)
                                },
                                onReset = {
                                    actions.onResetTagRelation(tagIndex, relationIndex)
                                },
                            )
                        }
                    }
                    if (state.isEditModeOn) {
                        WordSpaceEditableTagListItem(
                            label = {
                                val value = firstCapStringResource(R.string.new_x, firstCapStringResource(R.string.relation))
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = value,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            },
                            editMode = true,
                            hasActions = false,
                            status = MDEditableFieldStatus.NEW,
                            onClick = {
                                onConfirmEditField = { newValue ->
                                    actions.onAddTagRelation(tagIndex, newValue)
                                }
                                editingTextFieldInitialValue = ""
                                isTagEditField = false
                                actions.onShowDialog()
                            },
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .fillMaxWidth(),
                        )
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
                if (state.isEditModeOn) {
                    WordSpaceEditableTagListItem(
                        label = {
                            val value = firstCapStringResource(R.string.new_x, firstCapStringResource(R.string.word_class))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center
                            )
                        },
                        editMode = true,
                        hasActions = false,
                        status = MDEditableFieldStatus.NEW,
                        onClick = {
                            onConfirmEditField = { newValue ->
                                actions.onAddTag(newValue)
                            }
                            isTagEditField = true
                            editingTextFieldInitialValue = ""
                            actions.onShowDialog()
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun WordSpaceEditableTagListItem(
    label: @Composable () -> Unit,
    status: MDEditableFieldStatus,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    onReset: () -> Unit = {},
    hasActions: Boolean = true,
    editMode: Boolean = true,
) {
    val animationSpec by remember {
        derivedStateOf {
            tween<Color>(300)
        }
    }
    val containerColor by animateColorAsState(
        targetValue = if (!editMode) {
            Color.Transparent
        } else if (status == MDEditableFieldStatus.NEW) {
            MaterialTheme.colorScheme.primaryContainer
        } else if (status == MDEditableFieldStatus.EDITED) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = animationSpec
    )

    val contentColor by animateColorAsState(
        targetValue = if (!editMode) {
            MaterialTheme.colorScheme.onSurface
        } else if (status == MDEditableFieldStatus.NEW) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else if (status == MDEditableFieldStatus.EDITED) {
            MaterialTheme.colorScheme.onErrorContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = animationSpec
    )
    val showOnDelete by remember(hasActions, editMode) {
        derivedStateOf { hasActions && editMode }
    }

    val showOnReset by remember(hasActions, editMode, status) {
        derivedStateOf {
            hasActions && editMode && status == MDEditableFieldStatus.EDITED
        }
    }
    AssistChip(
        modifier = modifier.height(36.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor,
            trailingIconContentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledLabelColor = contentColor,
            disabledTrailingIconContentColor = contentColor
        ),
//        border = null,
        label = label,
        onClick = onClick,
        enabled = editMode,
        trailingIcon = if (showOnDelete || showOnReset) {
            {
                Row {
                    if (showOnDelete) {
                        IconButton(onDelete, modifier = Modifier.size(36.dp)) {
                            MDIcon(MDIconsSet.Delete)
                        }
                    }
                    if (showOnReset) {
                        IconButton(onReset, modifier = Modifier.size(36.dp)) {
                            MDIcon(MDIconsSet.Reset)
                        }
                    }
                }
            }
        } else null
    )

}

@Composable
private fun WordsSpaceFieldEditDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (newValue: String) -> Unit,
    modifier: Modifier = Modifier,
    getInitialValue: () -> String = { "" },
    isWordClass: Boolean = true,
) {
    val isNew by remember {
        derivedStateOf { getInitialValue().isBlank() }
    }
    var value by remember(showDialog) {
        mutableStateOf(getInitialValue())
    }
    MDAlertDialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss,
        actions = {
            MDAlertDialogActions(
                onDismissRequest = onDismiss,
                onPrimaryClick = {
                    onConfirm(value)
                },
                onSecondaryClick = onDismiss,
                dismissOnPrimaryClick = true,
                dismissOnSecondaryClick = true,
            )
        },
        modifier = modifier.width(250.dp),
        title = {
            Text(
                text = if (isWordClass) {
                    firstCapStringResource(
                        if (isNew) R.string.add_x else R.string.edit_x,
                        firstCapStringResource(R.string.word_class)
                    )
                } else {
                    firstCapStringResource(
                        if (isNew) R.string.add_x else R.string.edit_x,
                        firstCapStringResource(R.string.word_class_relation)
                    )
                },
                style = MaterialTheme.typography.titleMedium
            )
        },
        contentModifier = MDCardDefaults.contentModifier.padding(16.dp)
    ) {
        WordSpaceTagInputField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onChangeValue = { value = it },
            leadingIcon = if (isWordClass) {
                MDIconsSet.WordClass
            } else {
                MDIconsSet.WordRelatedWords
            },
            placeholder = if (isWordClass) {
                firstCapStringResource(R.string.word_class_name)
            } else {
                firstCapStringResource(R.string.word_class_relation_label)
            },
        )
    }
}

@Composable
private fun WordSpaceTagInputField(
    value: String,
    onChangeValue: (String) -> Unit,
    leadingIcon: MDIconsSet,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    MDBasicTextField(
        value = value,
        onValueChange = onChangeValue,
        modifier = modifier,
        placeholder = placeholder,
        maxLines = 1,
        leadingIcons = {
            MDIcon(leadingIcon, contentDescription = null)
        }
    )
}

@Preview
@Composable
private fun MDWordSpaceListItemPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDWordSpaceListItem(
                    state = state,
                    actions = actions,
                    currentEditableLanguageCode = language,
                    navigateToStatistics = {}
                )
            }
        }
    }
}

private val language = Language(
    code = "de",
    selfDisplayName = "Deutsch",
    localDisplayName = "German"
)
private val state = LanguageWordSpaceMutableState(
    "ar",
    initialTags = listOf(
        WordClass(
            id = 0,
            name = "Tag Name 1",
            language = language,
            relations = listOf(
                WordClassRelation("Relation 1", 15),
                WordClassRelation("Relation 2", 10),
            ),
            wordsCount = 25
        ),
        WordClass(
            id = 0,
            name = "Tag Name 2",
            language = language,
            relations = listOf(
                WordClassRelation("Relation 3", 15),
                WordClassRelation("Relation 4", 10),
            ),
            wordsCount = 25
        )
    ).map { MDEditableField.of(it) }
)

private val actions = LanguageWordSpaceActions(
    state = state,
    scope = CoroutineScope(Dispatchers.Default),
    onSubmitRequest = {
        delay(500)
    },
    onEditCapture = {},
    onEditRelease = {}
)