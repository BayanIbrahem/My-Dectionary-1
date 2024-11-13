package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableField
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableFieldStatus
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDCard
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.domain.model.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpaceMutableState
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpaceState
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWorkSpaceActions
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MDWordSpaceListItem(
    state: LanguageWordSpaceState,
    actions: LanguageWorkSpaceActions,
    modifier: Modifier = Modifier,
) {
    var onConfirmEditField: (newValue: String) -> Unit by remember {
        mutableStateOf({})
    }
    var editingTextFieldInitialValue by remember {
        mutableStateOf("")
    }
    WordsSpaceFieldEditDialog(
        showDialog = state.showEditDialog,
        onDismiss = actions::onHideDialog,
        onConfirm = { newValue ->
            onConfirmEditField(newValue)
        },
        getInitialValue = {
            editingTextFieldInitialValue
        }
    )
    MDCard(
        modifier = modifier,
        contentModifier = MDCardDefaults.contentModifier.padding(vertical = 8.dp),
        header = {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Text(
                    text = state.wordSpace.language.code.uppercase(),
                    style = if (state.wordSpace.language.isLongCode) {
                        MaterialTheme.typography.titleSmall
                    } else {
                        MaterialTheme.typography.titleLarge
                    },
                )
                Text(
                    text = buildAnnotatedString {
                        pushStyle(MaterialTheme.typography.bodyLarge.toSpanStyle())
                        append(state.wordSpace.language.selfDisplayName)
                        pushStyle(MaterialTheme.typography.labelSmall.toSpanStyle())
                        append("  ${state.wordSpace.wordsCount} Words") // TODO, string res
                    },
                )
                Spacer(modifier = Modifier.weight(1f))
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else if (state.isEditModeOn) {
                    IconButton(
                        onClick = actions::onCancel
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }

                    IconButton(
                        onClick = actions::onSubmit
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null) // TODO, string res
                    }
                } else {
                    IconButton(
                        onClick = actions::onEnableEditMode
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            state.tags.forEachIndexed { tagIndex, tag ->
                WordSpaceEditableTagListItem(
                    label = {
                        val value = if (state.isEditModeOn) tag.current.name else "${tag.current.name} x${tag.current.wordsCount}" // TODO, string res
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
                                    if (state.isEditModeOn) relation.label else "${relation.label} x${relation.wordsCount}" // TODO, string res
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
                            val value = "New Relation" // TODO, string res
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
                        val value = "New Tag" // TODO, string res
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
                        editingTextFieldInitialValue = ""
                        actions.onShowDialog()
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
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
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null
                            ) // TODO, icon res
                        }
                    }
                    if (showOnReset) {
                        IconButton(onReset, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null
                            )
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
    isTag: Boolean = true,
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
        modifier = modifier,
        title = {
            Text(
                text = if (isTag) {
                    if (isNew) "Add Word Type Tag" /* TODO, string res */ else "Edit Word Type Tag" /* TODO, string res */
                } else {
                    if (isNew) "Add Word Type Tag Relation" /* TODO, string res */ else "Edit Word Type Tag Relation" /* TODO, string res */
                },
                style = MaterialTheme.typography.titleMedium
            )
        },
        contentModifier = MDCardDefaults.contentModifier.padding(16.dp)
    ) {
        WordSpaceTagInputField(
            value = value,
            onChangeValue = { value = it },
            leadingIcon = if (isTag) Icons.Default.ThumbUp else Icons.Default.PlayArrow, // TODO, icon res
            placeholder = if (isTag) "Word Type Tag name" else "Word Type Tag Relation label",
        )
    }
}

@Composable
private fun WordSpaceTagInputField(
    value: String,
    onChangeValue: (String) -> Unit,
    leadingIcon: ImageVector,
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
            Icon(imageVector = leadingIcon, contentDescription = null) // TODO, icon res
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
                MDWordSpaceListItem(state = state, actions = actions)
            }
        }
    }
}

private val language = Language("de", "Deutsch", "German")
private val state = LanguageWordSpaceMutableState(
    wordSpace = LanguageWordSpace(
        language = language,
        wordsCount = 100,
        averageLearningProgress = 0.55f
    ),
    initialTags = listOf(
        WordTypeTag(
            id = 0,
            name = "Tag Name 1",
            language = language,
            relations = listOf(
                WordTypeTagRelation("Relation 1", 15),
                WordTypeTagRelation("Relation 2", 10),
            ),
            wordsCount = 25
        ),
        WordTypeTag(
            id = 0,
            name = "Tag Name 2",
            language = language,
            relations = listOf(
                WordTypeTagRelation("Relation 3", 15),
                WordTypeTagRelation("Relation 4", 10),
            ),
            wordsCount = 25
        )
    ).map { MDEditableField.of(it) }
)

private val actions = LanguageWorkSpaceActions(
    state = state,
    scope = CoroutineScope(Dispatchers.Default),
    onSubmitRequest = {
        delay(500)
    },
)