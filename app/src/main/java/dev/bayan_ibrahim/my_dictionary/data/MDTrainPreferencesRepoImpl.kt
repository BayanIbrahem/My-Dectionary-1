package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.core.util.invalidIfNull
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.train.MDTrainDataSource
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class MDTrainPreferencesRepoImpl(
    private val wordWithTagsDao: WordWithContextTagDao,
) : MDTrainPreferencesRepo {
    override suspend fun getWordsIdsOfTagsAndMemorizingProbability(
        viewPreferences: MDWordsListViewPreferences,
    ): Set<Long> {
        val words = wordWithTagsDao.getAllWordsWithTagsRelations().first().map {
            it.asWordModel()
        }
        val now = Clock.System.now()
        return words.mapNotNull { word ->
            val matchTags = viewPreferences.matchesTags(tags = word.tags)
            if (!matchTags) return@mapNotNull null

            val memorizingProbability = MDTrainDataSource.Default.memoryDecayFormula(word, now)
            val matchMemorizingProbability = checkMatchMemorizingProbabilityOf(
                progress = memorizingProbability,
                filterProgressGroups = viewPreferences.selectedMemorizingProbabilityGroups
            )
            if (matchMemorizingProbability) {
                word.id
            } else {
                null
            }
        }.toSet()
    }

    private fun progressRangeOf(collection: Collection<MDWordsListMemorizingProbabilityGroup>): Pair<Float, Float> = collection.minOfOrNull {
        it.learningRange.start
    }.invalidIfNull(0f) to collection.maxOfOrNull {
        it.learningRange.endInclusive
    }.invalidIfNull(1f)


    private fun checkMatchMemorizingProbabilityOf(
        progress: Float,
        filterProgressGroups: Set<MDWordsListMemorizingProbabilityGroup>,
    ): Boolean = filterProgressGroups.isEmpty() || filterProgressGroups.any {
        progress in it.learningRange
    }
}