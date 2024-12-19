package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.util.invalidIfNull
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.train.MDTrainDataSource
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class MDTrainPreferencesRepoImpl(
    private val wordDao: WordDao,
) : MDTrainPreferencesRepo {
    override suspend fun getWordsIdsOfTagsAndMemorizingProbability(
        viewPreferences: MDWordsListViewPreferences,
    ): Set<Long> {
        val includeEmptyTags = viewPreferences.selectedTags.isEmpty()
        return wordDao.getWordsWithTags(
            includeEmptyTags = includeEmptyTags,
        ).map {
            val now = Clock.System.now()
            it.mapNotNull {
                val word = it.asWordModel()
                val matchTags = checkMatchTagsOf(
                    tags = word.tags,
                    filterTags = viewPreferences.selectedTags,
                    includeFilterTags = viewPreferences.includeSelectedTags,
                )
                if (!matchTags) return@mapNotNull null

                val memorizingProbability = MDTrainDataSource.Default.memoryDecayFormula(word, now)
                val matchMemorizingProbability = checkMatchMemorizingProbabilityOf(
                    progress = memorizingProbability,
                    filterProgressGroups = viewPreferences.selectedMemorizingProbabilityGroups
                )
                return@mapNotNull if (matchMemorizingProbability) {
                    word.id
                } else {
                    null
                }
            }.toSet()
        }.first()
    }

    private fun progressRangeOf(collection: Collection<MDWordsListMemorizingProbabilityGroup>): Pair<Float, Float> = collection.minOfOrNull {
        it.learningRange.start
    }.invalidIfNull(0f) to collection.maxOfOrNull {
        it.learningRange.endInclusive
    }.invalidIfNull(1f)

    private fun checkMatchTagsOf(
        tags: Set<String>,
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

    private fun checkMatchMemorizingProbabilityOf(
        progress: Float,
        filterProgressGroups: Set<MDWordsListMemorizingProbabilityGroup>,
    ): Boolean = filterProgressGroups.isEmpty() || filterProgressGroups.any {
        progress in it.learningRange
    }
}