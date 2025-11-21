package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags


import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.InheritedTagsComparable
import dev.bayan_ibrahim.my_dictionary.domain.repo.TagRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorMutableUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDMarkerTagsViewModel @Inject constructor(
    private val repo: TagRepo,
) : ViewModel() {
    private val _uiState: MDMarkerTagsMutableUiState = MDMarkerTagsMutableUiState()
    val markerTags = repo.getTagsStream(
        includeMarkerTags = true,
        includeNonMarkerTags = false
    ).map {
        it.sortedWith(InheritedTagsComparable).toPersistentList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = persistentListOf(),
    )

    private val _tagsState = MDTagsSelectorMutableUiState()

    private fun onAddNewMarkerTag(markerTags: List<Tag>) {
        val nonMarkedTags = markerTags.filter {
            it.color == null
        }
        _tagsState.selectedTags.clear()
        viewModelScope.launch(Dispatchers.IO) {
            nonMarkedTags.forEach { tag ->
                repo.addOrUpdateTag(tag.onCopy(color = Color.Red, currentColorIsPassed = false))
            }
        }
    }

    val uiState: MDMarkerTagsUiState = _uiState
    fun initWithNavArgs(args: MDDestination.TagsEditor) {
        viewModelScope.launch {
            _uiState.onExecute { true }
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
        override fun updateTag(tag: Tag) {
            viewModelScope.launch(Dispatchers.IO) {
                repo.addOrUpdateTag(tag)
            }
        }

        override fun onUpdateSelectedTags(selectedTags: List<Tag>) {
            val tag = selectedTags.lastOrNull() ?: return
            updateTag(tag.onCopy(color = Color.Red, currentColorIsPassed = false, passColorToChildren = true))
        }

        override fun removeTag(tag: Tag) {
            viewModelScope.launch(Dispatchers.IO) {
                repo.removeTag(tag)
            }
        }
    }
}
