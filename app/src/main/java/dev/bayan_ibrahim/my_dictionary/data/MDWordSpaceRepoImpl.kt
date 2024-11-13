package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagRelatedWordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asTagModelWithCount
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordSpaceModel
import dev.bayan_ibrahim.my_dictionary.domain.model.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordSpaceRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MDWordSpaceRepoImpl(
    private val wordDao: WordDao,
    private val tagDao: WordTypeTagDao,
    private val relatedWordsDao: WordTypeTagRelatedWordDao,
) : MDWordSpaceRepo {
    override suspend fun getLanguagesWordSpacesWithTags(): Map<LanguageWordSpace, List<WordTypeTag>> {
        val wordSpaces = getLanguagesWordSpaces().first()

        val relatedWordsCount = relatedWordsDao.getRelatedWordsCount()

        return wordSpaces.associateWith { wordSpace ->
            tagDao.getTagTypesOfLanguage(wordSpace.language.code).first().map { tagWithRelation ->
                tagWithRelation.asTagModelWithCount(relatedWordsCount)
            }
        }
    }

    override fun getLanguagesWordSpaces(): Flow<List<LanguageWordSpace>> = wordDao.getLanguagesWordSpaces().map { it.map { it.asWordSpaceModel() } }
}