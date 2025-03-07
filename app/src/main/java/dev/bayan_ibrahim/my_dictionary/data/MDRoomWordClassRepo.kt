package dev.bayan_ibrahim.my_dictionary.data

import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassRelatedWordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassRelationWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordClassModel
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordClassRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class MDRoomWordClassRepo(
    private val db: MDDataBase,
) : WordClassRepo {
    private val wordDao: WordDao = db.getWordDao()
    private val wordClassDao: WordClassDao = db.getWordClassDao()
    private val wordClassRelationDao: WordClassRelationWordsDao = db.getWordClassRelationDao()
    private val wordClassRelatedDao: WordClassRelatedWordDao = db.getWordClassRelatedWordDao()

    override fun getWordsClassesOfLanguage(
        code: LanguageCode,
    ): Flow<List<WordClass>> = wordClassDao.getTagTypesOfLanguage(code.code).map {
        it.map { it.asWordClassModel() }
    }

    override fun getAllWordsClasses(): Flow<Map<LanguageCode, List<WordClass>>> =
        combine(
            wordClassDao.getAllWordClasses(),
            wordDao.getWordsCountOfWordClasses(),
            wordClassRelatedDao.getRelatedWordsCount(),
        ) { classes, classesCount, relationsCount ->
            classes.map {
                it.asWordClassModel(classesCount[it.wordClass.id] ?: 0, relationsCount)
            }.groupBy {
                it.language
            }
        }

    override suspend fun getWordClass(
        id: Long,
    ): WordClass? = wordClassDao.getTagType(id)?.asWordClassModel()

    private suspend fun insertWordsClassesWithRelationsTransaction(
        tags: List<WordClass>,
        deleteOthers: Boolean,
    ) {
        db.withTransaction {
            tags.groupBy { it.language }.forEach { (code, tags) ->
                if (tags.isNotEmpty()) {
                    insertWordsClassesWithRelationsOfLanguage(
                        languageCode = code,
                        tags = tags,
                        deleteOthers = deleteOthers,
                    )
                }
            }
        }
    }

    private suspend fun insertWordsClassesWithRelationsOfLanguage(
        languageCode: LanguageCode,
        tags: List<WordClass>,
        deleteOthers: Boolean,
    ) {
        val newRelationsEntities = mutableListOf<WordClassRelationEntity>()
        val existedRelationsEntities = mutableListOf<WordClassRelationEntity>()
        val existedTagsEntities = mutableListOf<WordClassEntity>()
        val allNewTagsIds = mutableSetOf<Long>()

        tags.forEach { tag ->
            val tagId = if (tag.id == INVALID_ID) {
                wordClassDao.insertTagType(tag.asTagEntity())
            } else {
                existedTagsEntities.add(tag.asTagEntity())
                tag.id
            }
            allNewTagsIds.add(tagId)
            tag.relations.forEach { relation ->
                if (relation.id == INVALID_ID) {
                    newRelationsEntities.add(relation.asRelationEntity(tagId))
                } else {
                    existedRelationsEntities.add(relation.asRelationEntity(tagId))
                }
            }
        }
        wordClassDao.updateTagTypes(existedTagsEntities)
        wordClassRelationDao.insertRelations(newRelationsEntities)
        wordClassRelationDao.updateRelations(existedRelationsEntities)
        if (deleteOthers) {
            wordClassDao.deleteWordsClassesExclude(languageCode.code, allNewTagsIds)
        }
    }

    override suspend fun setLanguageWordsClasses(
        code: LanguageCode,
        tags: List<WordClass>,
    ) {
        insertWordsClassesWithRelationsTransaction(tags, true)
    }
}