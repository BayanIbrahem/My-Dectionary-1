package dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableField
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import kotlinx.coroutines.CoroutineScope

data class LanguageWordSpaceMutableState(
    override val wordSpace: LanguageWordSpace,
    private val initialTags: List<MDEditableField<WordTypeTag>>,
) : LanguageWordSpaceState {
    override var isLoading: Boolean by mutableStateOf(false)
    override var isEditModeOn: Boolean by mutableStateOf(false)
    override var isEditDialogShown: Boolean by mutableStateOf(false)

    override val tags: SnapshotStateList<MDEditableField<WordTypeTag>> = mutableStateListOf()

    fun reset() {
        tags.clear()
        tags.addAll(initialTags)
    }

    init {
        reset()
    }

    constructor(state: LanguageWordSpaceState) : this(state.wordSpace, state.tags)

    fun getActions(
        scope: CoroutineScope,
        onSubmitRequest: suspend (LanguageWordSpaceState) -> Unit,
        onEditCapture: () -> Unit,
        onEditRelease: () -> Unit,
    ): LanguageWordSpaceActions = LanguageWordSpaceActions(
        state = this,
        scope = scope,
        onSubmitRequest = onSubmitRequest,
        onEditCapture = onEditCapture,
        onEditRelease = onEditRelease
    )
}
