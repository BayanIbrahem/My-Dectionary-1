package dev.bayan_ibrahim.my_dictionary.data.di

import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.data.MDImportFromFileRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDLanguageSelectionDialogRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDMarkerTagsRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDStatisticsRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDTrainRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordDetailsRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordSpaceRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordsListRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordsListTrainDialogRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordsListViewPreferencesRepoImpl
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.MDFileReaderAbstractFactory
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDImportFromFileRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDLanguageSelectionDialogRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDMarkerTagsRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDStatisticsRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordDetailsRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordSpaceRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListTrainDialogRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListViewPreferencesRepo
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Singleton
    @Provides
    fun providesWordDetailsRepo(
        db: MDDataBase,
    ): MDWordDetailsRepo = MDWordDetailsRepoImpl(db = db)

    @Singleton
    @Provides
    fun providesLanguageSelectionDialogRepo(
        db: MDDataBase,
        preferences: MDPreferencesDataStore,
    ): MDLanguageSelectionDialogRepo = MDLanguageSelectionDialogRepoImpl(
        preferences = preferences,
        wordSpaceDao = db.getLanguageWordSpaceDao(),
    )

    @Singleton
    @Provides
    fun providesWordsListRepo(
        db: MDDataBase,
        preferences: MDPreferencesDataStore,
        languageRepo: MDLanguageSelectionDialogRepo,
    ): MDWordsListRepo = MDWordsListRepoImpl(
        wordDao = db.getWordDao(),
        wordsPaginatedDao = db.getWordsPaginatedDao(),
        wordWithTagsDao = db.getWordWithContextTagDao(),
        wordWithTagAndRelatedWordsDao = db.getWordsWithContextTagAndRelatedWordsDao(),
        tagDao = db.getWordTypeTagDao(),
        preferences = preferences,
        languageDao = db.getLanguageDao(),
        contextTagDao = db.getContextTagDao(),
        languageRepo = languageRepo,
        wordsCrossTagDao = db.getWordsCrossTagsDao(),
    )

    @Singleton
    @Provides
    fun providesWordsListTrainDialogRepo(
        preferences: MDPreferencesDataStore,
    ): MDWordsListTrainDialogRepo = MDWordsListTrainDialogRepoImpl(
        dataStore = preferences
    )

    @Singleton
    @Provides
    fun providesWordsListViewDialogRepo(
        db: MDDataBase,
        preferences: MDPreferencesDataStore,
    ): MDWordsListViewPreferencesRepo = MDWordsListViewPreferencesRepoImpl(
        wordDao = db.getWordDao(),
        wordWithTagsDao = db.getWordWithContextTagDao(),
        dataStore = preferences,
        userPreferences = preferences,
    )

    @Singleton
    @Provides
    fun providesWordsSpacesRepo(
        db: MDDataBase,
    ): MDWordSpaceRepo = MDWordSpaceRepoImpl(db)

    @Singleton
    @Provides
    fun provideImportFromFileRepo(
        db: MDDataBase,
        abstractFactory: MDFileReaderAbstractFactory,
//        rawWordReader: MDFileReaderDecorator<MDRawWord>,
//        rawWordCSVFileSplitter: MDCSVFileSplitter<MDRawWord>,
    ): MDImportFromFileRepo = MDImportFromFileRepoImpl(
        db = db,
        abstractFactory = abstractFactory,
        appVersion = Build.VERSION.RELEASE
//        rawWordReader = rawWordReader,
//        rawWordCSVFileSplitter = rawWordCSVFileSplitter
    )

    @Singleton
    @Provides
    fun providesTrainRepo(
        db: MDDataBase,
        preferences: MDPreferencesDataStore,
    ): MDTrainRepo = MDTrainRepoImpl(
        db = db,
        preferences = preferences,
    )

    @Singleton
    @Provides
    fun provideStatisticsRepo(
        db: MDDataBase,
    ): MDStatisticsRepo = MDStatisticsRepoImpl(
        trainHistoryDao = db.getWordTrainDao(),
        wordDao = db.getWordDao(),
        contextTagDao = db.getContextTagDao(),
        wordCrossContextTagDao = db.getWordsCrossTagsDao(),
    )

    @Singleton
    @Provides
    fun provideMarkerTagsRepo(
        db: MDDataBase,
    ): MDMarkerTagsRepo = MDMarkerTagsRepoImpl(
        contextTagDao = db.getContextTagDao(),
        wordsCrossContextTagDao = db.getWordsCrossTagsDao(),
    )
}