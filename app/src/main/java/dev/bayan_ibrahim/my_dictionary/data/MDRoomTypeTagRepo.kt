package dev.bayan_ibrahim.my_dictionary.data

import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagRelationWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModel
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.repo.TypeTagRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MDRoomTypeTagRepo(
    private val db: MDDataBase,
) : TypeTagRepo {
    private val typeTagDao: WordTypeTagDao = db.getWordTypeTagDao()
    private val typeRelationDao: WordTypeTagRelationWordsDao = db.getWordTypeTagRelationDao()

    override fun getTypeTagsOfLanguage(
        code: LanguageCode,
    ): Flow<List<WordTypeTag>> = typeTagDao.getTagTypesOfLanguage(code.code).map {
        it.map { it.asTagModel() }
    }

    override fun getAllTypeTags(): Flow<Map<LanguageCode, List<WordTypeTag>>> = typeTagDao.getAllTagTypes().map {
        it.map {
            it.asTagModel()
        }.groupBy {
            it.language
        }
    }

    override suspend fun getTypeTag(
        id: Long,
    ): WordTypeTag? = typeTagDao.getTagType(id)?.asTagModel()

    private suspend fun insertTypeTagsWithRelationsTransaction(
        tags: List<WordTypeTag>,
        deleteOthers: Boolean,
    ) {
        db.withTransaction {
            tags.groupBy { it.language }.forEach { (code, tags) ->
                if (tags.isNotEmpty()) {
                    insertTypeTagsWithRelationsOfLanguage(
                        languageCode = code,
                        tags = tags,
                        deleteOthers = deleteOthers,
                    )
                }
            }
        }
    }

    private suspend fun insertTypeTagsWithRelationsOfLanguage(
        languageCode: LanguageCode,
        tags: List<WordTypeTag>,
        deleteOthers: Boolean,
    ) {
        val newRelationsEntities = mutableListOf<WordTypeTagRelationEntity>()
        val existedRelationsEntities = mutableListOf<WordTypeTagRelationEntity>()
        val existedTagsEntities = mutableListOf<WordTypeTagEntity>()
        val allNewTagsIds = mutableSetOf<Long>()

        tags.forEach { tag ->
            val tagId = if (tag.id == INVALID_ID) {
                typeTagDao.insertTagType(tag.asTagEntity())
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
        typeTagDao.updateTagTypes(existedTagsEntities)
        typeRelationDao.insertRelations(newRelationsEntities)
        typeRelationDao.updateRelations(existedRelationsEntities)
        if (deleteOthers) {
            typeTagDao.deleteTypeTagsExclude(languageCode.code, allNewTagsIds)
        }
    }

    override suspend fun setLanguageTypeTags(
        code: LanguageCode,
        tags: List<WordTypeTag>,
    ) {
        insertTypeTagsWithRelationsTransaction(tags, true)
    }
}