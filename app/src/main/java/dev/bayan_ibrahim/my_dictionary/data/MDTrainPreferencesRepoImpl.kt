package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.core.util.invalidIfNull
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.StringListConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.WordsListLearningProgressGroup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MDTrainPreferencesRepoImpl(
    private val wordDao: WordDao,
): MDTrainPreferencesRepo {
    override suspend fun getWordsIdsOfTagsAndProgressRange(
        viewPreferences: WordsListViewPreferences,
    ): Set<Long> {
        val (minProgress, maxProgress) = progressRangeOf(viewPreferences.selectedLearningProgressGroups)
        val includeEmptyTags = viewPreferences.selectedTags.isEmpty()
        return wordDao.getWordsIdsWithTagsOfLearningProgressRange(
            includeEmptyTags = includeEmptyTags,
            minProgress = minProgress,
            maxProgress = maxProgress
        ).map {
            it.mapNotNull { (id, tags, progress) ->
                val matchTags = checkMatchTagsOf(
                    tags = StringListConverter.stringToListConverter(tags),
                    filterTags = viewPreferences.selectedTags,
                    includeFilterTags = viewPreferences.includeSelectedTags,
                )
                val matchProgress = checkMatchProgressOf(
                    progress,
                    viewPreferences.selectedLearningProgressGroups
                )
                if (matchTags && matchProgress) {
                    id
                } else {
                    null
                }
            }.toSet()
        }.first()
    }

    private fun progressRangeOf(collection: Collection<WordsListLearningProgressGroup>): Pair<Float, Float> = collection.minOfOrNull {
        it.learningRange.start
    }.invalidIfNull(0f) to collection.maxOfOrNull {
        it.learningRange.endInclusive
    }.invalidIfNull(1f)

    private fun checkMatchTagsOf(
        tags: List<String>,
        filterTags: Set<String>,
        includeFilterTags: Boolean,
    ): Boolean {
        if (filterTags.isEmpty()) return true

        return if (includeFilterTags) {
            tags.any { it in filterTags }
        } else {
            tags.none { it in filterTags }
        }
    }

    private fun checkMatchProgressOf(
        progress: Float,
        filterProgressGroups: Set<WordsListLearningProgressGroup>,
    ): Boolean = filterProgressGroups.isEmpty() || filterProgressGroups.any {
        progress in it.learningRange
    }
}