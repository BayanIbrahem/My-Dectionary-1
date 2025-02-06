package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.TagsMutableTree
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

class MDTagsSelectorMutableUiState : MDTagsSelectorUiState {
    override var searchQuery: String by mutableStateOf("")
    override var disabledTags: PersistentSet<Tag> by mutableStateOf(persistentSetOf())
    override val allTagsTree: TagsMutableTree = TagsMutableTree()
    override val currentTagsTree: TagsMutableTree = TagsMutableTree()
    override val selectedTags: SnapshotStateList<Tag> = mutableStateListOf()
    override var isSelectEnabled by mutableStateOf(true)
}
