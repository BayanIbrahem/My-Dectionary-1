package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.safeSubList
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.context_tag.ContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.TrainHistoryDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_context_tag.WordsCrossContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.toTrainHistoryModels
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDStatisticsRepo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class MDStatisticsRepoImpl(
    val trainHistoryDao: TrainHistoryDao,
    val wordDao: WordDao,
    val contextTagDao: ContextTagDao,
    val wordCrossContextTagDao: WordsCrossContextTagDao,
) : MDStatisticsRepo {
    override suspend fun getTrainHistoryInDateRange(
        start: Instant,
        end: Instant,
    ): List<TrainHistory> = trainHistoryDao.getTrainHistoryOf(
        includeTime = true,
        startTime = start.toEpochMilliseconds(),
        endTime = end.toEpochMilliseconds(),
    ).first().toTrainHistoryModels()

    override suspend fun getMostRecentTrainHistory(count: Int): List<TrainHistory> {
        return trainHistoryDao.getTrainHistoryOf().first().toTrainHistoryModels().sortedByDescending {
            it.time
        }.safeSubList(0, count)
    }

    override suspend fun getTrainHistoryOfWord(wordId: Long): List<TrainHistory> {
        return getTrainHistoryOfWordsSet(setOf(wordId))
    }

    override suspend fun getTrainHistoryOfLanguage(language: Language): List<TrainHistory> {
        val wordsIds = wordDao.getWordsIdsOfLanguage(language.code.code).first()
        if (wordsIds.isEmpty()) return emptyList()

        return getTrainHistoryOfWordsSet(wordsIds)
    }

    override suspend fun getTrainHistoryOfTag(tag: Long): List<TrainHistory> {
        val wordsIds = wordCrossContextTagDao.getWordsCrossContextTagsOfTag(tag).map {
            it.map { it.wordId }.toSet()
        }.first()

        if (wordsIds.isEmpty()) return emptyList()

        return getTrainHistoryOfWordsSet(wordsIds)
    }

    override suspend fun getTrainHistoryOfTypeTag(typeTagId: Long): List<TrainHistory> {
        val wordsIds = wordDao.getWordsIdsOfTypeTag(typeTagId).first()
        if (wordsIds.isEmpty()) return emptyList()

        return getTrainHistoryOfWordsSet(wordsIds)
    }

    private suspend fun getTrainHistoryOfWordsSet(wordsIds: Collection<Long>) = trainHistoryDao.getTrainHistoryOf(
        includeWordsIds = true,
        wordsIds = wordsIds.toSet()
    ).first().toTrainHistoryModels()

    override suspend fun getAllTrainsCount(): Int = trainHistoryDao.getTotalTrainHistoryCount()
}