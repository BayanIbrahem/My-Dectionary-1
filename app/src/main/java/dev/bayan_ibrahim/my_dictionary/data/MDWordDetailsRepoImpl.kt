package dev.bayan_ibrahim.my_dictionary.data

import androidx.room.withTransaction
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.context_tag.ContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.language.LanguageDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithContextTagsAndRelatedWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_context_tag.WordsCrossContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossContextTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asRelatedWords
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDContextTagsRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordDetailsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class MDWordDetailsRepoImpl(
    private val db: MDDataBase,
    private val contextTagDao: ContextTagDao = db.getContextTagDao(),
    private val wordDao: WordDao = db.getWordDao(),
    private val wordsCrossContextTagDao: WordsCrossContextTagDao = db.getWordsCrossTagsDao(),
    private val wordWithContextTagsAndRelatedWordsDao: WordWithContextTagsAndRelatedWordsDao = db.getWordsWithContextTagAndRelatedWordsDao(),
    private val typeTagDao: WordTypeTagDao = db.getWordTypeTagDao(),
    private val languageDao: LanguageDao = db.getLanguageDao(),
) : MDWordDetailsRepo, MDContextTagsRepo by MDContextTagsRepoImpl(contextTagDao, wordsCrossContextTagDao) {
    override suspend fun getWord(wordId: Long): Word {
        val word = wordWithContextTagsAndRelatedWordsDao.getWordWithContextTagsAndRelatedWordsRelation(wordId)
        require(word != null) { "Invalid Word Id $wordId" }
        val typeTag = word.word.wordTypeTagId?.let { wordTypeTagId ->
            typeTagDao.getTagType(wordTypeTagId)
        }?.asTagModel()
        return word.asWordModel(typeTag)
    }

    override suspend fun saveNewWord(word: Word): Word {
        require(word.id == INVALID_ID) { "expected invalid id for new word but get ${word.id}, if you are trying to update a word try using saveExistedWord method" }
        val entity = word.asWordEntity()
        val relatedWords = word.asRelatedWords()
        val id = db.withTransaction {
            languageDao.insertLanguage(LanguageEntity(word.language.code.code))
            val wordId = wordDao.insertWordWithRelations(
                word = entity,
                relatedWords = relatedWords,
            )
            val relations = getWordCrossContextTagRelationFromTags(word.tags, wordId)
            wordsCrossContextTagDao.insertWordCrossContextTagsList(relations)
            wordId
        }
        return getWord(id)
    }

    private suspend fun getWordCrossContextTagRelationFromTags(
        tags: Collection<ContextTag>,
        wordId: Long,
    ): List<WordCrossContextTagEntity> {
        val tagsIds = tags.map {
            it.id.nullIfInvalid() ?: contextTagDao.insertContextTag(it.asEntity())
        }

        val relations = tagsIds.map { tagId ->
            WordCrossContextTagEntity(
                wordId = wordId,
                tagId = tagId
            )
        }
        return relations
    }


    override suspend fun saveExistedWord(word: Word) {
        require(word.id != INVALID_ID) { "expected valid id for existed word but get ${word.id}, if you are trying to save a new word use saveNewWord method" }
        val entity = word.asWordEntity()
        val relatedWords = word.asRelatedWords()
        db.withTransaction {
            wordDao.updateWordWithRelations(
                word = entity,
                relatedWords = relatedWords,
            )
            val relations = getWordCrossContextTagRelationFromTags(word.tags, word.id)
            wordsCrossContextTagDao.deleteWordsCrossContextTagsOfWord(word.id)
            wordsCrossContextTagDao.insertWordCrossContextTagsList(relations)
        }
    }

    override suspend fun getLanguageTags(language: String): List<WordTypeTag> = getLanguageTagsStream(language).first()

    override fun getLanguageTagsStream(language: String): Flow<List<WordTypeTag>> =
        typeTagDao.getTagTypesOfLanguage(language = language).map { tags ->
            tags.map { tagEntity ->
                tagEntity.asTagModel()
            }
        }

    override suspend fun deleteWord(id: Long) {
        wordDao.deleteWords(id)
    }
}