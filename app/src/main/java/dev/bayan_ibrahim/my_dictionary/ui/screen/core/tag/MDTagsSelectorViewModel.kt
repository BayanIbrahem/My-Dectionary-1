package dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.TagsMutableTree
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.TagsTree
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.contains
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.isContained
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.parentOrNull
import dev.bayan_ibrahim.my_dictionary.domain.repo.TagRepo
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
    ) {
        this.selectedTagsMaxSize = selectedTagsMaxSize.coerceAtLeast(1)
        tagsStateAllTagsStreamCollectorJob?.cancel()
        tagsStateAllTagsStreamCollectorJob = viewModelScope.launch {
            repo.getTagsStream().collect { allTags ->
                _uiState.allTagsTree.setFrom(allTags)
                refreshCurrentTree()
            }
        }
        this.allowedFilter = allowedFilter
        this.forbiddenFilter = forbiddenFilter
    }

    private fun onAddTagToTree(tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addOrUpdateTag(tag)
        }
    }

    private fun onRemoveTagFromTree(tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeTag(tag)
        }
    }

    fun getUiActions(
        navActions: MDTagsSelectorNavigationUiActions,
    ): MDTagsSelectorUiActions = MDTagsSelectorUiActions(
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
        navActions: MDTagsSelectorNavigationUiActions,
    ): MDTagsSelectorBusinessUiActions = object : MDTagsSelectorBusinessUiActions {

        private fun onUpdateSelectedTags() {
            navActions.onUpdateSelectedTags(_uiState.selectedTags)
            onUpdateSelectEnableState()
            onUpdateDisabledTags()
        }

        override fun onClickTag(tag: Tag) {
            setCurrentTree(_uiState.allTagsTree[tag] ?: _uiState.allTagsTree)
            onSearchQueryChange(_uiState.searchQuery)
        }

        override fun onSearchQueryChange(query: String) {
            _uiState.searchQuery = query
            onUpdateDisabledTags()
        }


        override fun onSelectTag(tag: Tag) {
            val indexOfLastContainer = _uiState.selectedTags.indexOfLast { it.contains(tag) }
            val firstValidIndexAfterTrim = _uiState.selectedTags.size.inc().minus(selectedTagsMaxSize).coerceAtLeast(0)

            if (indexOfLastContainer >= firstValidIndexAfterTrim) {
                // if a parent of this tag already exists then we don't add this tag
                return
            }
            _uiState.selectedTags.removeIf {
                it.isContained(tag) // current param tag return true in isContained so we make adding after removing
            }
            _uiState.selectedTags.add(tag)
            if (firstValidIndexAfterTrim > 0) {
                _uiState.selectedTags.removeRange(0, firstValidIndexAfterTrim.dec())
            }
            onUpdateSelectedTags()
            onResetToRoot()
        }

        override fun onSelectCurrentTag() {
            _uiState.currentTagsTree.tag?.let { onSelectTag(it) }
        }

        override fun onUnSelectTag(tag: Tag) {
            _uiState.selectedTags.remove(tag)
            onUpdateSelectedTags()
        }

        override fun onSetInitialSelectedTags(tags: Collection<Tag>) {
            _uiState.selectedTags.setAll(tags)
            onUpdateSelectedTags()
        }

        override fun clearSelectedTags() {
            _uiState.selectedTags.clear()
            onUpdateSelectedTags()
        }

        override fun onAddNewTag(tag: Tag) {
            onAddTagToTree(tag)
            val mutableCurrent = TagsMutableTree(_uiState.currentTagsTree)
            mutableCurrent.addTag(tag)
            setCurrentTree(mutableCurrent)
        }

        override fun onAddNewTag(segment: String) {
            if (segment.isNotBlank()) {
                val tag = Tag((_uiState.currentTagsTree.tag?.segments ?: emptyList()) + segment)
                onAddNewTag(tag)
            }
        }

        override fun onDeleteTag(tag: Tag) {
            onRemoveTagFromTree(tag)
            val mutableCurrent = TagsMutableTree(_uiState.currentTagsTree)
            mutableCurrent.removeTag(tag)
            setCurrentTree(mutableCurrent)
        }

        override fun onNavigateUp() {
            _uiState.currentTagsTree.tag?.parentOrNull?.let(::onClickTag) ?: setCurrentTree(_uiState.allTagsTree)
        }

        override fun onResetToRoot() {
            setCurrentTree(_uiState.allTagsTree)
        }

        override fun onSetAllowedTagsFilter(filter: (Tag) -> Boolean) {
            allowedFilter = filter
            onUpdateSelectEnableState()
        }

        override fun onResetAllowedTagsFilter() {
            allowedFilter = { true }
            onUpdateSelectEnableState()
        }

        override fun onSetForbiddenTagsFilter(filter: (Tag) -> Boolean) {
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
            this@MDTagsSelectorViewModel.refreshCurrentTree()
        }
    }

    private fun refreshCurrentTree() {
        setCurrentTree(
            _uiState.currentTagsTree.tag?.let { tag ->
                _uiState.allTagsTree[tag]
            } ?: _uiState.allTagsTree
        )
    }

    private fun setCurrentTree(tree: TagsTree) {
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
