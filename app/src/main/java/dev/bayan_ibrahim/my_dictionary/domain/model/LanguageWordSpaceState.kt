package dev.bayan_ibrahim.my_dictionary.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableField
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableFieldStatus
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface LanguageWordSpaceState {
    val isEditModeOn: Boolean
    val showEditDialog: Boolean
    val isLoading: Boolean

    val wordSpace: LanguageWordSpace
    val tags: List<MDEditableField<WordTypeTag>>

    fun toMutableLanguageWordSpaceWithTags(): LanguageWordSpaceMutableState = LanguageWordSpaceMutableState(this)
}

data class LanguageWordSpaceMutableState(
    override val wordSpace: LanguageWordSpace,
    private val initialTags: List<MDEditableField<WordTypeTag>>,
) : LanguageWordSpaceState {
    override var isLoading: Boolean by mutableStateOf(false)
    override var isEditModeOn: Boolean by mutableStateOf(false)
    override var showEditDialog: Boolean by mutableStateOf(false)

    override val tags: SnapshotStateList<MDEditableField<WordTypeTag>> = mutableStateListOf()

    fun reset() {
        tags.clear()
        tags.addAll(initialTags)
    }

    init {
        reset()
    }

    constructor(space: LanguageWordSpaceState) : this(space.wordSpace, space.tags)
}

data class LanguageWorkSpaceActions(
    val state: LanguageWordSpaceMutableState,
    val scope: CoroutineScope,
    val onSubmitRequest: suspend () -> Unit,
) {
    fun onShowDialog() {
        state.showEditDialog = true
    }

    fun onHideDialog() {
        state.showEditDialog = false
    }

    fun onCancel() {
        state.reset()
        state.isEditModeOn = false
    }
    fun onSubmit() {
        scope.launch {
            state.isLoading = true
            onSubmitRequest()
            state.isEditModeOn = false
            state.isLoading = false
        }
    }

    fun onEnableEditMode() {
        state.isEditModeOn = true
    }

    fun onDisableEditMode() {
        state.isEditModeOn = false
    }


    fun onAddTag(tag: String) {
        state.tags.add(
            MDEditableField.new(
                WordTypeTag(
                    id = INVALID_ID,
                    name = tag,
                    language = state.wordSpace.language,
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
            copy(relations = relations + WordTypeTagRelation(label = relation))
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
