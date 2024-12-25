package dev.bayan_ibrahim.my_dictionary.data


import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.TrainHistoryDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.context_tag.ContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithContextTagsAndRelatedWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_context_tag.WordsCrossContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.toTrainHistoryEntities
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.toTrainHistoryModels
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListTrainPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class MDTrainRepoImpl(
    private val db: MDDataBase,
    private val wordDao: WordDao = db.getWordDao(),
    private val wordWithTagsAndRelatedWordsDao: WordWithContextTagsAndRelatedWordsDao = db.getWordsWithContextTagAndRelatedWordsDao(),
    private val trainHistoryDao: TrainHistoryDao = db.getWordTrainDao(),
    private val typeTagDao: WordTypeTagDao = db.getWordTypeTagDao(),
    private val preferences: MDPreferencesDataStore,
) : MDTrainRepo, MDTrainPreferencesRepo by MDTrainPreferencesRepoImpl(db.getWordWithContextTagDao()) {

    override suspend fun getTrainPreferences(): MDWordsListTrainPreferences = preferences.getWordsListTrainPreferences()
    override suspend fun getViewPreferences(): MDWordsListViewPreferences = preferences.getWordsListViewPreferences()
    override suspend fun getAllSelectedLanguageWords(): Sequence<Word> {
        val currentLanguage = preferences.getUserPreferences().selectedLanguagePage ?: return sequenceOf()

        val typeTags = typeTagDao.getTagTypesOfLanguage(currentLanguage.code.code).first().map {
            it.asTagModel()
        }.associateBy {
            it.id
        }

        return wordWithTagsAndRelatedWordsDao.getWordsWithContextTagsAndRelatedWordsRelations(currentLanguage.code.code).map {
            it.asSequence().map { relation ->
                relation.asWordModel(typeTags[relation.word.wordTypeTagId])
            }
        }.firstOrNull() ?: sequenceOf()
    }

    override suspend fun getSelectedLanguage(): Language? {
        return preferences.getUserPreferences().selectedLanguagePage
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

    override suspend fun submitTrainHistory(trainHistory: TrainHistory, wordsNewMemoryDecay: Map<Long, Float>) {
        val entities = trainHistory.toTrainHistoryEntities()
        db.withTransaction {
            trainHistoryDao.insertTrainHistories(entities)
            val newLastTrainTime = trainHistory.time.toEpochMilliseconds()
            wordsNewMemoryDecay.forEach { (id, decay) ->
                wordDao.updateLastTrainHistoryAndMemoryDecay(id, newLastTrainTime, decay)
            }
        }
    }
}
