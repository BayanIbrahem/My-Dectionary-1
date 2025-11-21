package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.MutableTagsTree
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.MutableTagsTreeDelegate
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

class MDTagsSelectorMutableUiState : MDTagsSelectorUiState {
    override var searchQuery: String by mutableStateOf("")
    override var disabledTags: PersistentSet<Long> by mutableStateOf(persistentSetOf())

    /**
     * we are using the delegate here instead of [MutableTagsTree], cause there are  methods in [MutableTagsTreeDelegate]
     * not existed in [MutableTagsTree]
     * like [MutableTagsTreeDelegate.setFrom]
     */
    override val tagsTree: MutableTagsTreeDelegate = MutableTagsTreeDelegate(emptyMap(), emptyMap())
    override val visibleTagsList: SnapshotStateList<Tag> = mutableStateListOf()
    override var selectedTagsIds: PersistentSet<Long> by mutableStateOf(persistentSetOf())
    override var isSelectEnabled by mutableStateOf(true)
    override var isAddEnabled: Boolean by mutableStateOf(false)
    override var isEditEnabled: Boolean by mutableStateOf(false)
    override var isDeleteEnabled: Boolean by mutableStateOf(false)
    override var isDeleteSubtreeEnabled: Boolean by mutableStateOf(false)
    override var currentEditTagId: Long? by mutableStateOf(null)
}
