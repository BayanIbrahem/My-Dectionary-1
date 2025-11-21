package dev.bayan_ibrahim.my_dictionary.data

import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningViewNormalize
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassRelatedWordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassRelationWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordClassModel
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
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
    ): WordClass? = wordClassDao.getWordClass(id)?.asWordClassModel()

    override suspend fun getWordClass(
        label: String,
    ): WordClass? = wordClassDao.getWordClass(label)?.asWordClassModel()

    override suspend fun getWordClassRelation(id: Long): WordClassRelation? {
        return wordClassRelationDao.getRelation(id)?.toModel()

    }

    override suspend fun getWordClassRelation(wordClassId: Long, label: String): WordClassRelation? {
        return wordClassRelationDao.getRelation(wordClassId, label.lowercase())?.toModel()
    }

    private suspend fun insertWordsClassesWithRelationsTransaction(
        wordsClasses: List<WordClass>,
        deleteOthers: Boolean,
    ) {
        db.withTransaction {
            wordsClasses.groupBy { it.language }.forEach { (code, classes) ->
                if (classes.isNotEmpty()) {
                    insertWordsClassesWithRelationsOfLanguage(
                        languageCode = code,
                        classes = classes,
                        deleteOthers = deleteOthers,
                    )
                }
            }
        }
    }

    private suspend fun insertWordsClassesWithRelationsOfLanguage(
        languageCode: LanguageCode,
        classes: List<WordClass>,
        deleteOthers: Boolean,
    ) {
        val newRelationsEntities = mutableListOf<WordClassRelationEntity>()
        val existedRelationsEntities = mutableListOf<WordClassRelationEntity>()
        val existedTagsEntities = mutableListOf<WordClassEntity>()
        val allNewTagsIds = mutableSetOf<Long>()

        classes.forEach { tag ->
            val tagId = if (tag.id == INVALID_ID) {
                wordClassDao.insertWordClass(tag.asEntity())
            } else {
                existedTagsEntities.add(tag.asEntity())
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
        wordsClasses: List<WordClass>,
    ) {
        insertWordsClassesWithRelationsTransaction(wordsClasses, true)
    }

    override suspend fun addWordClass(wordClass: WordClass): WordClass {
        val id = if (wordClass.id == INVALID_ID) {
            wordClassDao.getWordClass(
                wordClass.name.meaningViewNormalize
            )?.wordClass?.id ?: wordClassDao.insertWordClass(
                wordClass.asEntity()
            )
        } else {
            wordClass.id
        }
        val existedRelationsNames = wordClassRelationDao.getWordClassRelations(id).map { it.label }.toSet()
        // add all relations:
        val relationsEntities = wordClass.relations.mapNotNull {
            if (it.label in existedRelationsNames) {
                null
            } else {
                it.asRelationEntity(id)
            }
        }
        wordClassRelationDao.insertRelations(relationsEntities)
        return wordClassDao.getWordClass(id)!!.asWordClassModel()
    }

    override suspend fun addWordClassRelation(wordClassId: Long, label: String): WordClassRelation {
        val id = wordClassRelationDao.getRelation(
            wordClassId = wordClassId,
            label = label
        )?.id ?: wordClassRelationDao.insertRelation(
            relation = WordClassRelation(label = label)
                .asRelationEntity(wordClassId)
        )
        return wordClassRelationDao.getRelation(id)!!.toModel()
    }
}

private fun WordClassRelationEntity.toModel(): WordClassRelation = WordClassRelation(
    label = label,
    id = id!!,
)

