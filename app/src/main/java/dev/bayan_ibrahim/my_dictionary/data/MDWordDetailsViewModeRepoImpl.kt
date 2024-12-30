package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithContextTagsAndRelatedWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordModel
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordDetailsViewModeRepo

class MDWordDetailsViewModeRepoImpl(
    private val wordWithContextTagsAndRelatedWordsDao: WordWithContextTagsAndRelatedWordsDao,
    private val typeTagDao: WordTypeTagDao,
) : MDWordDetailsViewModeRepo {
    override suspend fun getWord(wordId: Long): Word? {
        val word = wordWithContextTagsAndRelatedWordsDao.getWordWithContextTagsAndRelatedWordsRelation(wordId) ?: return null
        val typeTag = word.word.wordTypeTagId?.let { wordTypeTagId ->
            typeTagDao.getTagType(wordTypeTagId)
        }?.asTagModel()
        return word.asWordModel(typeTag)
    }
}