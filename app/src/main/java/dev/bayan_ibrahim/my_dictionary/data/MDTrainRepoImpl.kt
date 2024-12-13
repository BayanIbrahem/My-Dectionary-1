package dev.bayan_ibrahim.my_dictionary.data


import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.TrainHistoryDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.toTrainHistoryEntities
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.toTrainHistoryModels
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.WordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class MDTrainRepoImpl(
    private val wordDao: WordDao,
    private val trainHistoryDao: TrainHistoryDao,
    private val preferences: MDPreferencesDataStore,
) : MDTrainRepo, MDTrainPreferencesRepo by MDTrainPreferencesRepoImpl(wordDao) {
    override suspend fun getTrainPreferences(): MDWordsListTrainPreferences = preferences.getWordsListTrainPreferences()
    override suspend fun getViewPreferences(): WordsListViewPreferences = preferences.getWordsListViewPreferences()
    override suspend fun getAllSelectedLanguageWords(): Sequence<Word> {
        val currentLanguage = preferences.getUserPreferences().selectedLanguagePage ?: return sequenceOf()
        return wordDao.getWordsOfLanguage(currentLanguage.code.code)
            .map { words ->
                words.asSequence().map(WordEntity::asWordModel)
            }.firstOrNull() ?: sequenceOf()
    }

    override fun getTrainHistoryOf(
        timeRange: LongRange?,
        trainTypes: Set<TrainWordType>,
        excludeSpecifiedWordsIds: Boolean,
        wordsIds: Set<Long>,
    ): Flow<List<TrainHistory>> = trainHistoryDao.getTrainHistoryOf(
        includeTime = timeRange != null,
        startTime = timeRange?.start ?: 0,
        endTime = timeRange?.endInclusive ?: Long.MAX_VALUE,
        includeTrainType = trainTypes.isNotEmpty(),
        trainTypes = trainTypes,
        includeWordsIds = !excludeSpecifiedWordsIds && wordsIds.isNotEmpty(),
        wordsIds = wordsIds,
        includeExcludedWordsIds = excludeSpecifiedWordsIds && wordsIds.isNotEmpty(),
        excludedWordsIds = wordsIds,
    ).map { it.toTrainHistoryModels() }

    override suspend fun submitTrainHistory(trainHistory: TrainHistory) {
        val entities = trainHistory.toTrainHistoryEntities()
        trainHistoryDao.insertTrainHistories(entities)
    }
}
