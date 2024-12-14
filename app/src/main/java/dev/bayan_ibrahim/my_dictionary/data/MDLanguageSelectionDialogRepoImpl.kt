package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.LanguageWordSpaceDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table.LanguageWordSpaceEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asWordSpaceModel
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.user.MDUserPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.allLanguages
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDLanguageSelectionDialogRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class MDLanguageSelectionDialogRepoImpl(
    private val preferences: MDUserPreferencesDataStore,
    private val wordSpaceDao: LanguageWordSpaceDao,
) : MDLanguageSelectionDialogRepo {
    override suspend fun setSelectedLanguagePage(code: LanguageCode) {
        preferences.writeUserPreferences {
            it.copy(selectedLanguagePage = code.language)
        }
    }

    override suspend fun getSelectedLanguagePage(): Language? = getSelectedLanguagePageStream().firstOrNull()

    override fun getSelectedLanguagePageStream(): Flow<Language?> = preferences.getUserPreferencesStream().map {
        it.selectedLanguagePage ?: wordSpaceDao.getLanguagesWordSpaces().first().firstOrNull()?.languageCode?.code?.language
    }

    override fun getAllLanguagesWordSpaces(
        includeNotUsedLanguages: Boolean,
    ): Flow<List<LanguageWordSpace>> = if (includeNotUsedLanguages) {
        wordSpaceDao.getLanguagesWordSpaces().map { entities ->
            val dbModels = entities.map(LanguageWordSpaceEntity::asWordSpaceModel)
            (dbModels + allLanguages.map { (_, language) ->
                LanguageWordSpace(language = language)
            }).distinctBy {
                it.language.code
            }
        }
    } else {
        wordSpaceDao.getLanguagesWordSpaces().map { entities ->
            entities.map(LanguageWordSpaceEntity::asWordSpaceModel)
        }
    }

    override suspend fun getLanguagesWordSpaces(code: LanguageCode): LanguageWordSpace? =
        wordSpaceDao.getLanguagesWordSpace(code.code)?.asWordSpaceModel()
}