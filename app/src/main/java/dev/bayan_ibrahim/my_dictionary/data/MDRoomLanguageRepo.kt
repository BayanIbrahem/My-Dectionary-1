package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.language.LanguageDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.language.LanguageWordSpaceDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asLanguageModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordSpaceModel
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.allLanguages
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.repo.LanguageRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MDRoomLanguageRepo(
    private val languageDao: LanguageDao,
    private val wordSpaceDao: LanguageWordSpaceDao,
) : LanguageRepo {
    override suspend fun insertLanguages(languages: Collection<LanguageCode>): Boolean {
        val exists = languageDao.hasLanguages(code = languages.map { it.code })
        if (!exists) {
            languageDao.insertAllLanguages(languages.map { LanguageEntity(it.code) })
        }
        return !exists
    }

    override fun getAllLanguages(): Flow<List<Language>> = languageDao.getAllLanguages().map {
        it.map {
            it.asLanguageModel()
        }
    }

    override suspend fun getLanguageWordSpace(
        code: LanguageCode,
    ): LanguageWordSpace? = wordSpaceDao.getLanguagesWordSpace(
        code.code
    )?.asWordSpaceModel()

    override fun getAllLanguagesWordSpaces(
        includeNotUsedLanguages: Boolean,
    ): Flow<List<LanguageWordSpace>> = wordSpaceDao.getLanguagesWordSpaces().map {
        it.map { entity ->
            entity.asWordSpaceModel()
        }
    }.let {
        if (includeNotUsedLanguages) {
            it.flatMapLatest { spaces ->
                flow {
                    val allSpaces = spaces.plus(allLanguages.values.map { language ->
                        language.getLanguageWordSpace()
                    }).distinctBy {
                        it.code
                    }
                    emit(allSpaces)
                }
            }
        } else {
            it
        }
    }

    override suspend fun deleteWordSpace(languageCode: LanguageCode) {
        languageDao.deleteLanguage(languageCode.code)
    }
}