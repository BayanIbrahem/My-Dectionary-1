package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.view_preferences_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagRegexNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagViewNormalize
import dev.bayan_ibrahim.my_dictionary.domain.model.defaultWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListViewPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MDWordsListViewPreferencesViewModel @Inject constructor(
    private val repo: MDWordsListViewPreferencesRepo,
) : ViewModel() {

    private val _uiState: MDWordsListViewPreferencesMutableUiState = MDWordsListViewPreferencesMutableUiState()
    val uiState: MDWordsListViewPreferencesUiState = _uiState

    fun initWithNavArgs() {
        viewModelScope.launch {
            _uiState.onExecute {
                val data = repo.getViewPreferences()
                _uiState.onApplyPreferences(data)
                true
            }
        }
    }

    fun getUiActions(
        navActions: MDWordsListViewPreferencesNavigationUiActions,
    ): MDWordsListViewPreferencesUiActions = MDWordsListViewPreferencesUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDWordsListViewPreferencesNavigationUiActions,
    ): MDWordsListViewPreferencesBusinessUiActions = object : MDWordsListViewPreferencesBusinessUiActions {
        override fun onClearViewPreferences() = editViewByPreferences {
            this.onApplyPreferences(defaultWordsListViewPreferences)
        }

        override fun onSearchQueryChange(query: String) = editViewByPreferences {
            this.searchQuery = query
        }

        override fun onSelectSearchTarget(searchTarget: MDWordsListSearchTarget) = editViewByPreferences {
            this.searchTarget = searchTarget
        }

        override fun onTagSearchQueryChange(query: String) {
            _uiState.tagSearchQuery = query
            viewModelScope.launch {
                val queryRegex = query.tagRegexNormalize.toRegex()
                val newTags = repo.getSelectedLanguageTags().mapNotNull { tag ->
                    if (tag !in uiState.selectedTags && queryRegex.matches(tag.lowercase())) {
                        tag.tagViewNormalize
                    } else {
                        null
                    }
                }
                _uiState.tagsSuggestions.clear()
                _uiState.tagsSuggestions.addAll(newTags)
            }
        }

        override fun onSelectTag(tag: String) = editViewByPreferences {
            this.selectedTags = this.selectedTags.add(tag)
            _uiState.tagSearchQuery = ""
        }

        override fun onRemoveTag(tag: String) = editViewByPreferences {
            this.selectedTags = this.selectedTags.remove(tag)
        }

        override fun onToggleIncludeSelectedTags(includeSelectedTags: Boolean) = editViewByPreferences {
            this.includeSelectedTags = includeSelectedTags
        }

        override fun onSelectLearningGroup(
            group: MDWordsListMemorizingProbabilityGroup,
        ) = editViewByPreferences {
            if (group in selectedMemorizingProbabilityGroups) {
                this.selectedMemorizingProbabilityGroups = this.selectedMemorizingProbabilityGroups.remove(group)
            } else {
                this.selectedMemorizingProbabilityGroups = this.selectedMemorizingProbabilityGroups.add(group)
            }
        }

        override fun onToggleAllMemorizingProbabilityGroups(selected: Boolean) = editViewByPreferences {
            if (selected) {
                this.selectedMemorizingProbabilityGroups = MDWordsListMemorizingProbabilityGroup.entries.toPersistentSet()
            } else {
                this.selectedMemorizingProbabilityGroups = persistentSetOf()
            }
        }

        override fun onSelectWordsSortBy(sortBy: MDWordsListViewPreferencesSortBy) = editViewByPreferences {
            this.sortBy = sortBy
        }

        override fun onSelectWordsSortByOrder(order: MDWordsListSortByOrder) = editViewByPreferences {
            this.sortByOrder = order
        }
    }

    private fun editViewByPreferences(body: MDWordsListViewPreferencesMutableUiState.() -> Unit) {
        _uiState.body()
        viewModelScope.launch {
            launch {
                repo.setViewPreferences(uiState)
            }
        }
    }
}
