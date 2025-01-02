package dev.bayan_ibrahim.my_dictionary.domain.model

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningSearchNormalize
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.searchQueryRegexNormalize
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSearchTarget
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy


interface MDWordsListViewPreferences {
    val searchQuery: String
    val searchTarget: MDWordsListSearchTarget
    val selectedTags: Set<ContextTag>
    val includeSelectedTags: Boolean
    val selectedMemorizingProbabilityGroups: Set<MDWordsListMemorizingProbabilityGroup>
    val sortBy: MDWordsListViewPreferencesSortBy
    val sortByOrder: MDWordsListSortByOrder

    val effectiveFilter: Boolean
        get() = searchQuery.isNotBlank()
                || selectedTags.isNotEmpty()
                || (selectedMemorizingProbabilityGroups.count() in 1..(MDWordsListMemorizingProbabilityGroup.entries.count()))

    fun matches(word: Word): Boolean = matchesSearch(word)
            && matchesMemorizingProbabilityGroup(word)

    private fun matchesSearch(word: Word): Boolean {
        if (searchQuery.isBlank()) return true
        val normalizedSearchQuery = searchQuery.searchQueryRegexNormalize.toRegex()

        val matchMeaning = searchTarget.includeMeaning && normalizedSearchQuery.matches(word.meaning.meaningSearchNormalize)
        val matchTranslation = searchTarget.includeTranslation && normalizedSearchQuery.matches(word.translation.meaningSearchNormalize)

        return matchMeaning || matchTranslation
    }


    private fun matchesMemorizingProbabilityGroup(word: Word): Boolean {
        if (selectedMemorizingProbabilityGroups.isEmpty()) return true
        if (selectedMemorizingProbabilityGroups.count() == MDWordsListMemorizingProbabilityGroup.entries.count()) return true
        return selectedMemorizingProbabilityGroups.any { group ->
            word.memoryDecayFactor in group.probabilityRange
        }
    }
}

data class WordsListViewPreferencesBuilder(
    override val searchQuery: String,
    override val searchTarget: MDWordsListSearchTarget,
    override val selectedTags: Set<ContextTag>,
    override val includeSelectedTags: Boolean,
    override val selectedMemorizingProbabilityGroups: Set<MDWordsListMemorizingProbabilityGroup>,
    override val sortBy: MDWordsListViewPreferencesSortBy,
    override val sortByOrder: MDWordsListSortByOrder,
) : MDWordsListViewPreferences

val defaultWordsListViewPreferences by lazy {
    WordsListViewPreferencesBuilder(
        searchQuery = INVALID_TEXT,
        searchTarget = MDWordsListSearchTarget.All,
        selectedTags = emptySet(),
        includeSelectedTags = true,
        selectedMemorizingProbabilityGroups = emptySet(),
        sortBy = MDWordsListViewPreferencesSortBy.Meaning,
        sortByOrder = MDWordsListSortByOrder.Asc,
    )
}