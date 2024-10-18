package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.Word
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asRelatedWords
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordDetailsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WordDetailsRepoImpl(
    private val wordDao: WordDao,
    private val tagDao: WordTypeTagDao,
) : WordDetailsRepo {
    override suspend fun getWord(wordId: Long): Word {
        val word = wordDao.getWordWithRelatedWords(wordId)
        require(word != null) { "Invalid Word Id $wordId" }
        val typeTag = word.word.wordTypeTagId?.let { wordTypeTagId ->
            tagDao.getTagType(wordTypeTagId)
        }?.asTagModel()
        return word.asWordModel(typeTag)
    }

    override suspend fun saveNewWord(word: Word): Word {
        require(word.id == INVALID_ID) { "expected invalid id for new word but get ${word.id}, if you are trying to update a word try using saveExistedWord method" }
        val entity = word.asWordEntity()
        val relatedWords = word.asRelatedWords()
        val id = wordDao.insertWordWithRelations(entity, relatedWords)
        return getWord(id)
    }

    override suspend fun saveExistedWord(word: Word) {
        require(word.id != INVALID_ID) { "expected valid id for existed word but get ${word.id}, if you are trying to save a new word use saveNewWord method" }
        val entity = word.asWordEntity()
        val relatedWords = word.asRelatedWords()
        wordDao.updateWordWithRelations(entity, relatedWords)
    }

    override suspend fun getLanguageTags(language: String): List<WordTypeTag> = getLanguageTagsStream(language).first()

    override fun getLanguageTagsStream(language: String): Flow<List<WordTypeTag>> = tagDao.getTagTypesOfLanguage(language = language).map { tags ->
        tags.map { tagEntity ->
            tagEntity.asTagModel()
        }
    }

    override suspend fun deleteWord(id: Long) {
        wordDao.deleteWords(id)
    }
}