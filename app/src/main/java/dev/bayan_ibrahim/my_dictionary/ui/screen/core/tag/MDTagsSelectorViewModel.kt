package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag


import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ParentedTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.repo.TagRepo
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MDTagsSelectorViewModel @Inject constructor(
    private val repo: TagRepo,
) : ViewModel() {
    private val _uiState: MDTagsSelectorMutableUiState = MDTagsSelectorMutableUiState()
    val uiState: MDTagsSelectorUiState = _uiState
    private var tagsStateAllTagsStreamCollectorJob: Job? = null

    private var allowedFilter: (Tag) -> Boolean = { true }
    private var forbiddenFilter: (Tag) -> Boolean = { false }

    private var selectedTagsMaxSize: Int = Int.MAX_VALUE
    fun init(
        allowedFilter: (Tag) -> Boolean = { true },
        forbiddenFilter: (Tag) -> Boolean = { false },
        selectedTagsMaxSize: Int = Int.MAX_VALUE,
        isSelectEnabled: Boolean = false,
        isAddEnabled: Boolean = false,
        isEditEnabled: Boolean = false,
        isDeleteEnabled: Boolean = false,
        isDeleteSubtreeEnabled: Boolean = false,
    ) {
        this.selectedTagsMaxSize = selectedTagsMaxSize.coerceAtLeast(1)
        tagsStateAllTagsStreamCollectorJob?.cancel()
        tagsStateAllTagsStreamCollectorJob = viewModelScope.launch {
            // no need to listen to stream cause the tree is mutable and change itself after each edit
            repo.getTagsStream().first().let { allTags ->
                _uiState.tagsTree.setFrom(allTags)
            }
        }
        this.allowedFilter = allowedFilter
        this.forbiddenFilter = forbiddenFilter
        _uiState.isSelectEnabled = isSelectEnabled
        _uiState.isAddEnabled = isAddEnabled
        _uiState.isEditEnabled = isEditEnabled
        _uiState.isDeleteEnabled = isDeleteEnabled
        _uiState.isDeleteSubtreeEnabled = isDeleteSubtreeEnabled
    }

    /**
     * store the selected tags in order, the top of the queue is the list start
     */
    private val selectionQueue = mutableListOf<Long>()

    private fun onRemoveTag(tag: Tag) {
        if (!uiState.isDeleteEnabled) return
        viewModelScope.launch(Dispatchers.IO) {
            if (tag.id >= 0) { // valid stored tag
                repo.removeTag(tag)
            }
            withContext(Dispatchers.Main.immediate) {
                _uiState.tagsTree.onDelete(tag.id)
                _uiState.selectedTagsIds = _uiState.selectedTagsIds.remove(tag.id)
            }
        }
    }

    fun getUiActions(
        navActions: MDTagsSelectorNavigationUiActions,
    ): MDTagsSelectorUiActions = MDTagsSelectorUiActions(
        navigationActions = navActions, businessActions = getBusinessUiActions(navActions)
    )

    private fun onUpdateDisabledTags() {
        _uiState.disabledTags = _uiState.tagsTree.tags.mapNotNull { tag ->
            val forbidden = !allowedFilter(tag) || forbiddenFilter(tag)
            if (forbidden) {
                tag.id
            } else {
                null
            }
        }.toPersistentSet()
    }

    private fun getBusinessUiActions(
        navActions: MDTagsSelectorNavigationUiActions,
    ): MDTagsSelectorBusinessUiActions = object : MDTagsSelectorBusinessUiActions {
        private suspend fun onUpdateVisibleTagsList() {
            val query = _uiState.searchQuery
            val newVisibleTags = if (query.isBlank()) {
                uiState.tagsTree.tags
            } else {
                uiState.tagsTree.tags.filter { tag ->
                    val matchQuery = tag.label.tagMatchNormalize.startsWith(query.tagMatchNormalize)
                    matchQuery
                }
            }

            _uiState.visibleTagsList.setAll(newVisibleTags)
            onSortVisibleTagsList()

        }

        override fun onClickTag(tag: Tag, index: Int) {
            onToggleSelectTag(tag, tag.id !in uiState.selectedTagsIds)
        }

        override fun onToggleSelectTag(tag: Tag, select: Boolean) {
            _uiState.selectedTagsIds = if (select) {
                if (selectionQueue.count() >= selectedTagsMaxSize && selectionQueue.isNotEmpty()) {
                    selectionQueue.removeAt(0)
                }
                selectionQueue.add(tag.id)
                _uiState.selectedTagsIds.add(tag.id)
            } else {
                selectionQueue.remove(tag.id)
                _uiState.selectedTagsIds.remove(tag.id)
            }
        }

        override fun onSearchQueryChange(query: String) {
            _uiState.searchQuery = query
            onUpdateDisabledTags()
        }

        override fun onClearSearchQuery() {
            _uiState.searchQuery = ""
            viewModelScope.launch {
                onUpdateVisibleTagsList()
            }
        }

        override fun onToggleSelectEnabled(selectEnabled: Boolean) {
            _uiState.isSelectEnabled = selectEnabled
        }

        override fun onToggleAddEnabled(addEnabled: Boolean) {
            _uiState.isAddEnabled = addEnabled
        }

        override fun onToggleEditEnabled(editEnabled: Boolean) {
            _uiState.isEditEnabled = editEnabled
        }

        override fun onToggleDeleteEnabled(deleteEnabled: Boolean) {
            _uiState.isDeleteEnabled = deleteEnabled
        }

        override fun onToggleDeleteSubtreeEnabled(deleteSubtreeEnabled: Boolean) {
            _uiState.isDeleteSubtreeEnabled = deleteSubtreeEnabled
        }

        override fun onConfirmSelectedTags() {
            val selectedTags = uiState.selectedTagsIds.map {
                uiState.tagsTree[it]
            }
            _uiState.selectedTagsIds.clear()
            navActions.onNotifyConfirmSelectedTags(selectedTags = selectedTags)
            navActions.onPop()
        }

        override fun onChangeParent(
            tag: Tag,
            parent: Tag?,
        ) {
            val oldParent = uiState.tagsTree.parent(tag.id)
            if (oldParent != parent) {
                if (parent == null) {
                    _uiState.tagsTree.removeParent(tag.id)
                } else {
                    _uiState.tagsTree.setParent(parentId = parent.id, childId = tag.id)
                }
                onSortVisibleTagsList()
            }
        }

        private fun onSortVisibleTagsList() {
            _uiState.visibleTagsList.sortBy { tag -> _uiState.tagsTree.tagsOrderKeys[tag.id] }
        }


        override fun onAddChild(parent: Tag?) {
            viewModelScope.launch {
                _uiState.currentEditTagId = MDTagsSelectorUiState.NEW_TAG_ID
                val tag = Tag(
                    label = "", id = _uiState.currentEditTagId!!
                )
                _uiState.tagsTree.onUpdateOrInsert(tag)
                if (parent != null) {
                    _uiState.tagsTree.setParent(parent.id, tag.id)
                }
                onUpdateVisibleTagsList()
                val index = uiState.visibleTagsList.indexOfFirst { it.id == tag.id }
                if (index >= 0) {
                    withContext(Dispatchers.Main.immediate) {
                        navActions.onAnimateScrollToIndex(index)
                    }
                }
            }
        }

        override fun onRequestEditTagLabel(tag: Tag) {
            viewModelScope.launch {
                _uiState.currentEditTagId = tag.id
                val index = uiState.visibleTagsList.indexOfFirst { it.id == tag.id }
                if (index >= 0) {
                    withContext(Dispatchers.Main.immediate) {
                        navActions.onAnimateScrollToIndex(index)
                    }
                }
            }
        }

        override fun onConfirmEditTagLabel(tag: Tag, newLabel: String) {
            viewModelScope.launch {
                val newId = _uiState.tagsTree.run {
                    val newTagValue = tag.onCopy(label = newLabel)
                    val newTag = repo.addOrUpdateTag(
                        tag = ParentedTag(tag = newTagValue, parentId = parent(tag.id)?.id)
                    )
                    onUpdate(newTag)
                    newTag.id
                }
                _uiState.currentEditTagId = null
                onSortVisibleTagsList()
                val index = uiState.visibleTagsList.indexOfFirst { it.id == tag.id }
                if (index >= 0) {
                    withContext(Dispatchers.Main.immediate) {
                        navActions.onAnimateScrollToIndex(index)
                    }
                }
            }
        }

        override fun onEditColor(id: Long, color: Color?, passColor: Boolean) {
            viewModelScope.launch {
                val tag = uiState.tagsTree[id]
                val newTag = tag.onCopy(color = color, passColor = passColor || color == null)
                repo.addOrUpdateTag(newTag)
                _uiState.tagsTree.onUpdate(newTag)
            }
        }

        override fun onCancelEditTagLabel() {
            _uiState.currentEditTagId = null
        }

        override fun onDeleteTag(tag: Tag) {
            viewModelScope.launch {
                onRemoveTag(tag)
                onUpdateVisibleTagsList()
            }
        }

        override fun onDeleteTagSubtree(tag: Tag) {
            viewModelScope.launch {
                _uiState.tagsTree.onDeleteSubtree(tag.id)
                onUpdateVisibleTagsList()
            }
        }

        override fun onSetAllowedTagsFilter(filter: (Tag) -> Boolean) {
            viewModelScope.launch {
                allowedFilter = filter
                onUpdateVisibleTagsList()
            }
        }

        override fun onResetAllowedTagsFilter() {
            viewModelScope.launch {
                allowedFilter = { true }
                onUpdateVisibleTagsList()
            }
        }

        override fun onSetForbiddenTagsFilter(filter: (Tag) -> Boolean) {
            viewModelScope.launch {
                forbiddenFilter = filter
                onUpdateVisibleTagsList()
            }
        }

        override fun onResetForbiddenTagsFilter() {
            viewModelScope.launch {
                forbiddenFilter = { false }
                onUpdateVisibleTagsList()
            }
        }
    }
}
