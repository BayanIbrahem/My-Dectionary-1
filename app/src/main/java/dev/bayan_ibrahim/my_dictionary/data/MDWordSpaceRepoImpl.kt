package dev.bayan_ibrahim.my_dictionary.data

import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.LanguageDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.LanguageWordSpaceDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagRelatedWordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagRelationWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModelWithCount
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordSpaceModel
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordSpaceRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MDWordSpaceRepoImpl(
    private val db: MDDataBase,
) : MDWordSpaceRepo {
    private val languageDao: LanguageDao = db.getLanguageDao()
    private val languageWordSpaceDao: LanguageWordSpaceDao = db.getLanguageWordSpaceDao()
    private val tagDao: WordTypeTagDao = db.getWordTypeTagDao()
    private val relatedWordsDao: WordTypeTagRelatedWordDao = db.getWordTypeTagRelatedWordDao()
    private val relationDao: WordTypeTagRelationWordsDao = db.getWordTypeTagRelationDao()

    override suspend fun addNewWordSpace(languageCode: String): Boolean {
        return if (languageDao.hasLanguage(languageCode)) {
            false
        } else {
            languageDao.insertLanguage(LanguageEntity(languageCode))
            true
        }
    }

    override suspend fun editLanguageTags(
        code: LanguageCode,
        tags: List<WordTypeTag>,
    ) {
        db.withTransaction {

            val newRelationsEntities = mutableListOf<WordTypeTagRelationEntity>()
            val existedRelationsEntities = mutableListOf<WordTypeTagRelationEntity>()
            val existedTagsEntities = mutableListOf<WordTypeTagEntity>()

            tags.forEach { tag ->
                val tagId =
                    if (tag.id == INVALID_ID) {
                        tagDao.insertTagType(tag.asTagEntity())
                    } else {
                        existedTagsEntities.add(tag.asTagEntity())
                        tag.id
                    }
                tag.relations.forEach { relation ->
                    if (relation.id == INVALID_ID) {
                        newRelationsEntities.add(relation.asRelationEntity(tagId))
                    } else {
                        existedRelationsEntities.add(relation.asRelationEntity(tagId))
                    }
                }
            }
            tagDao.updateTagTypes(existedTagsEntities)
            relationDao.insertRelations(newRelationsEntities)
            relationDao.updateRelations(existedRelationsEntities)
        }
    }

    override suspend fun getLanguagesWordSpacesWithTags(): Map<LanguageWordSpace, List<WordTypeTag>> {
        val wordSpaces = getLanguagesWordSpaces().first()

        val relatedWordsCount = relatedWordsDao.getRelatedWordsCount()

        return wordSpaces.associateWith { wordSpace ->
            // this may empty list and we are fine with it
            tagDao.getTagTypesOfLanguage(wordSpace.language.code.code).first().map { tagWithRelation ->
                tagWithRelation.asTagModelWithCount(relatedWordsCount)
            }
        }
    }

    override fun getLanguagesWordSpaces(): Flow<List<LanguageWordSpace>> = languageWordSpaceDao.getLanguagesWordSpaces().map { entites ->
        entites.map { entity ->
            entity.asWordSpaceModel()
        }
    }
}