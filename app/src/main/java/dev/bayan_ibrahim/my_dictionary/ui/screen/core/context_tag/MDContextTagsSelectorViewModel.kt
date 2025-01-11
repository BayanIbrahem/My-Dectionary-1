package dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTagsMutableTree
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTagsTree
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.contains
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.isContained
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.parentOrNull
import dev.bayan_ibrahim.my_dictionary.domain.repo.ContextTagRepo
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDContextTagsSelectorViewModel @Inject constructor(
    private val repo: ContextTagRepo,
) : ViewModel() {
    private val _uiState: MDContextTagsSelectorMutableUiState = MDContextTagsSelectorMutableUiState()
    val uiState: MDContextTagsSelectorUiState = _uiState
    private var tagsStateAllTagsStreamCollectorJob: Job? = null

    private var allowedFilter: (ContextTag) -> Boolean = { true }
    private var forbiddenFilter: (ContextTag) -> Boolean = { false }

    fun init(
        allowedFilter: (ContextTag) -> Boolean = { true },
        forbiddenFilter: (ContextTag) -> Boolean = { false },
    ) {
        tagsStateAllTagsStreamCollectorJob?.cancel()
        tagsStateAllTagsStreamCollectorJob = viewModelScope.launch {
            repo.getContextTagsStream().collect { allTags ->
                _uiState.allTagsTree.setFrom(allTags)
                refreshCurrentTree()
            }
        }
        this.allowedFilter = allowedFilter
        this.forbiddenFilter = forbiddenFilter
    }

    private fun onAddTagToTree(tag: ContextTag) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addOrUpdateContextTag(tag)
        }
    }

    private fun onRemoveTagFromTree(tag: ContextTag) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeContextTag(tag)
        }
    }

    fun getUiActions(
        navActions: MDContextTagsSelectorNavigationUiActions,
    ): MDContextTagsSelectorUiActions = MDContextTagsSelectorUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun onUpdateDisabledTags() {
        val query = _uiState.searchQuery
        _uiState.disabledTags = _uiState.currentTagsTree.nextLevel.mapNotNull {
            val matchQuery = it.key.tagMatchNormalize.startsWith(query.tagMatchNormalize)
            val isSelected = _uiState.selectedTags.contains(it.value.tag)
            if (!matchQuery || isSelected) {
                it.value.tag
            } else null
        }.toPersistentSet()
    }

    private fun getBusinessUiActions(
        navActions: MDContextTagsSelectorNavigationUiActions,
    ): MDContextTagsSelectorBusinessUiActions = object : MDContextTagsSelectorBusinessUiActions {

        private fun onUpdateSelectedTags() {
            navActions.onUpdateSelectedTags(_uiState.selectedTags)
            onUpdateSelectEnableState()
            onUpdateDisabledTags()
        }

        override fun onClickTag(tag: ContextTag) {
            setCurrentTree(_uiState.allTagsTree[tag] ?: _uiState.allTagsTree)
            onSearchQueryChange(_uiState.searchQuery)
        }

        override fun onSearchQueryChange(query: String) {
            _uiState.searchQuery = query
            onUpdateDisabledTags()
        }


        override fun onSelectTag(tag: ContextTag) {
            if (_uiState.selectedTags.any { it.contains(tag) }) {
                // if a parent of this tag already exists then we don't add this tag
                return
            }
            _uiState.selectedTags.removeIf {
                it.isContained(tag) // current param tag return true in isContained so we make adding after removing
            }
            _uiState.selectedTags.add(tag)
            onUpdateSelectedTags()
            onResetToRoot()
        }

        override fun onSelectCurrentTag() {
            _uiState.currentTagsTree.tag?.let { onSelectTag(it) }
        }

        override fun onUnSelectTag(tag: ContextTag) {
            _uiState.selectedTags.remove(tag)
            onUpdateSelectedTags()
        }

        override fun onSetInitialSelectedTags(tags: Collection<ContextTag>) {
            _uiState.selectedTags.setAll(tags)
            onUpdateSelectedTags()
        }

        override fun clearSelectedTags() {
            _uiState.selectedTags.clear()
            onUpdateSelectedTags()
        }

        override fun onAddNewContextTag(tag: ContextTag) {
            onAddTagToTree(tag)
            val mutableCurrent = ContextTagsMutableTree(_uiState.currentTagsTree)
            mutableCurrent.addTag(tag)
            setCurrentTree(mutableCurrent)
        }

        override fun onAddNewContextTag(segment: String) {
            if (segment.isNotBlank()) {
                val tag = ContextTag((_uiState.currentTagsTree.tag?.segments ?: emptyList()) + segment)
                onAddNewContextTag(tag)
            }
        }

        override fun onDeleteContextTag(tag: ContextTag) {
            onRemoveTagFromTree(tag)
            val mutableCurrent = ContextTagsMutableTree(_uiState.currentTagsTree)
            mutableCurrent.removeTag(tag)
            setCurrentTree(mutableCurrent)
        }

        override fun onNavigateUp() {
            _uiState.currentTagsTree.tag?.parentOrNull?.let(::onClickTag) ?: setCurrentTree(_uiState.allTagsTree)
        }

        override fun onResetToRoot() {
            setCurrentTree(_uiState.allTagsTree)
        }

        override fun onSetAllowedTagsFilter(filter: (ContextTag) -> Boolean) {
            allowedFilter = filter
            onUpdateSelectEnableState()
        }

        override fun onResetAllowedTagsFilter() {
            allowedFilter = { true }
            onUpdateSelectEnableState()
        }

        override fun onSetForbiddenTagsFilter(filter: (ContextTag) -> Boolean) {
            forbiddenFilter = filter
            onUpdateSelectEnableState()
        }

        override fun onResetForbiddenTagsFilter() {
            forbiddenFilter = { false }
            onUpdateSelectEnableState()
        }

        override fun onResetTagsFilter() {
            allowedFilter = { true }
            forbiddenFilter = { false }
            onUpdateSelectEnableState()
        }

        override fun refreshCurrentTree() {
            this@MDContextTagsSelectorViewModel.refreshCurrentTree()
        }
    }

    private fun refreshCurrentTree() {
        setCurrentTree(
            _uiState.currentTagsTree.tag?.let { tag ->
                _uiState.allTagsTree[tag]
            } ?: _uiState.allTagsTree
        )
    }

    private fun setCurrentTree(tree: ContextTagsTree) {
        _uiState.currentTagsTree.setFrom(tree)
        onUpdateSelectEnableState()
        onUpdateDisabledTags()
    }

    private fun onUpdateSelectEnableState() {
        _uiState.isSelectEnabled = _uiState.currentTagsTree.tag?.let {
            allowedFilter(it) && !forbiddenFilter(it)
        } == true
    }
}
