package dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTagsMutableTree
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

class MDContextTagsSelectorMutableUiState : MDContextTagsSelectorUiState {
    override var searchQuery: String by mutableStateOf("")
    override var disabledTags: PersistentSet<ContextTag> by mutableStateOf(persistentSetOf())
    override val allTagsTree: ContextTagsMutableTree = ContextTagsMutableTree()
    override val currentTagsTree: ContextTagsMutableTree = ContextTagsMutableTree()
    override val selectedTags: SnapshotStateList<ContextTag> = mutableStateListOf()
    override var isSelectEnabled by mutableStateOf(true)
}
