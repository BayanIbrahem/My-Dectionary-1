package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.util.invalidIfNull
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter.StringListConverter
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListLearningProgressGroup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MDTrainPreferencesRepoImpl(
    private val wordDao: WordDao,
): MDTrainPreferencesRepo {
    override suspend fun getWordsIdsOfTagsAndProgressRange(
        viewPreferences: MDWordsListViewPreferences,
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

    private fun progressRangeOf(collection: Collection<MDWordsListLearningProgressGroup>): Pair<Float, Float> = collection.minOfOrNull {
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
        val normalizedFilterTags = filterTags.map { it.tagMatchNormalize }.toSet()

        return if (includeFilterTags) {
            tags.any {
                it.tagMatchNormalize in normalizedFilterTags
            }
        } else {
            tags.none { it.tagMatchNormalize in normalizedFilterTags }
        }
    }

    private fun checkMatchProgressOf(
        progress: Float,
        filterProgressGroups: Set<MDWordsListLearningProgressGroup>,
    ): Boolean = filterProgressGroups.isEmpty() || filterProgressGroups.any {
        progress in it.learningRange
    }
}