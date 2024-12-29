package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags


import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionActions
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionActionsImpl
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.ui.context_tag.MDContextTagsSelectionUiState
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.InheritedTagsComparable
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDMarkerTagsRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDMarkerTagsViewModel @Inject constructor(
    private val repo: MDMarkerTagsRepo,
) : ViewModel() {
    private val _uiState: MDMarkerTagsMutableUiState = MDMarkerTagsMutableUiState()
    val markerTags = repo.getMarkerTagsStream().map {
        it.sortedWith(InheritedTagsComparable).toPersistentList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = persistentListOf(),
    )

    private val _tagsState = MDContextTagsSelectionMutableUiState()
    val nonMarkerTagsExplorerState: MDContextTagsSelectionUiState = _tagsState
    val nonMarkerTagsExplorerActions: MDContextTagsSelectionActions = MDContextTagsSelectionActionsImpl(
        state = _tagsState,
        onAddNewTag = ::onAddTagToTree,
        onDeleteTag = ::onRemoveTagFromTree,
        onUpdateSelectedTags = ::onAddNewMarkerTag
    )

    private fun onAddNewMarkerTag(markerTags: List<ContextTag>) {
        val nonMarkedTags = markerTags.filter {
            it.color == null
        }
        _tagsState.selectedTags.clear()
        viewModelScope.launch(Dispatchers.IO) {
            nonMarkedTags.forEach { tag ->
                repo.updateMarkerTag(tag.copy(color = Color.Red, currentColorIsPassed = false))
            }
        }
    }

    private var tagsStateAllTagsStreamCollectorJob: Job? = null

    val uiState: MDMarkerTagsUiState = _uiState
    fun initWithNavArgs(args: MDDestination.MarkerTags) {
        viewModelScope.launch {
            _uiState.onExecute {
                nonMarkerTagsExplorerActions.onSetForbiddenTagsFilter { targetTag ->
                    markerTags.value.any { markerTag -> markerTag.id == targetTag.id }
                }
                tagsStateAllTagsStreamCollectorJob?.cancel()
                tagsStateAllTagsStreamCollectorJob = launch {
                    repo.getContextTagsStream().collect { allTags ->
                        _tagsState.allTagsTree.setFrom(allTags)
                        nonMarkerTagsExplorerActions.refreshCurrentTree()
                    }
                }
                true
            }
        }
    }

    fun getUiActions(
        navActions: MDMarkerTagsNavigationUiActions,
    ): MDMarkerTagsUiActions = MDMarkerTagsUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDMarkerTagsNavigationUiActions,
    ): MDMarkerTagsBusinessUiActions = object : MDMarkerTagsBusinessUiActions {
        override fun updateTag(tag: ContextTag) {
            viewModelScope.launch(Dispatchers.IO) {
                repo.updateMarkerTag(tag)
            }
        }

        override fun removeTag(tag: ContextTag) {
            onRemoveTagFromTree(tag)
        }
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

}
