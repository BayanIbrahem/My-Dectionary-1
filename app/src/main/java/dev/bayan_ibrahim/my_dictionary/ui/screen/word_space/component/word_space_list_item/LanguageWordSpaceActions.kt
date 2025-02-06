package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableField
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableFieldStatus
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class LanguageWordSpaceActions(
    val state: LanguageWordSpaceMutableState,
    val scope: CoroutineScope,
    val onSubmitRequest: suspend (LanguageWordSpaceState) -> Unit,
    val onEditCapture: () -> Unit,
    val onEditRelease: () -> Unit,
) {

    fun onShowDialog() {
        state.isEditDialogShown = true
    }

    fun onHideDialog() {
        state.isEditDialogShown = false
    }

    fun onCancel() {
        state.reset()
        onDisableEditMode()
    }

    fun onSubmit() {
        scope.launch {
            state.isLoading = true
            onSubmitRequest(state)
            onDisableEditMode()
            state.isLoading = false
        }
    }

    fun onEnableEditMode() {
        state.isEditModeOn = true
        onEditCapture()
    }

    fun onDisableEditMode() {
        state.isEditModeOn = false
        onEditRelease()
    }


    fun onAddTag(tag: String) {
        state.tags.add(
            MDEditableField.new(
                WordClass(
                    id = INVALID_ID,
                    name = tag,
                    language = state,
                    relations = listOf()
                )
            )
        )
    }

    fun onEditTag(index: Int, newValue: String) {
        state.tags[index] = state.tags[index].edit {
            copy(name = newValue)
        }
    }

    fun onRemoveTag(index: Int) {
        state.tags.removeAt(index)
    }

    fun onResetTag(index: Int) {
        val newTag = state.tags[index].resetOrNull()
        if (newTag == null) {
            onRemoveTag(index)
        } else {
            state.tags[index] = newTag
        }
    }

    fun onAddTagRelation(tagIndex: Int, relation: String) {
        state.tags[tagIndex] = state.tags[tagIndex].edit {
            copy(relations = relations + WordClassRelation(label = relation))
        }
    }

    fun onEditTagRelation(tagIndex: Int, relationIndex: Int, newRelation: String) {
        state.tags[tagIndex] = state.tags[tagIndex].edit {
            val newRelations = relations.toMutableList()
            newRelations[relationIndex] = newRelations[relationIndex].copy(
                label = newRelation
            )
            copy(relations = newRelations)
        }
    }

    fun onRemoveTagRelation(tagIndex: Int, relationIndex: Int) {
        state.tags[tagIndex] = state.tags[tagIndex].edit {
            val newRelations = relations.toMutableList()
            newRelations.removeAt(relationIndex)
            copy(relations = newRelations)
        }
    }

    fun onResetTagRelation(tagIndex: Int, relationIndex: Int) {
        val relationId = state.tags[tagIndex].current.relations[relationIndex].id
        val originalRelationWithSameIdOrNull = state.tags[tagIndex].original?.relations?.firstOrNull {
            it.id == relationId
        }
        if (originalRelationWithSameIdOrNull == null) {
            onRemoveTagRelation(tagIndex, relationIndex)
        } else {
            onEditTagRelation(tagIndex, relationIndex, originalRelationWithSameIdOrNull.label)
        }
    }

    fun relationStatus(tagIndex: Int, relationIndex: Int): MDEditableFieldStatus {
        val relation = state.tags[tagIndex].current.relations[relationIndex]
        val originalRelationWithSameIdOrNull = state.tags[tagIndex].original?.relations?.firstOrNull {
            it.id == relation.id
        }
        return if (originalRelationWithSameIdOrNull == null) {
            MDEditableFieldStatus.NEW
        } else if (relation.label != originalRelationWithSameIdOrNull.label) {
            MDEditableFieldStatus.EDITED
        } else {
            MDEditableFieldStatus.NOT_CHANGED
        }
    }
}
