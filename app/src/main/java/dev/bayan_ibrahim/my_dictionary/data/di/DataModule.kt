package dev.bayan_ibrahim.my_dictionary.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWord
import dev.bayan_ibrahim.my_dictionary.data.MDImportFromFileRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDStatisticsRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDTrainRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordDetailsRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordSpaceRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordsListRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordsListTrainDialogRepoImpl
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListTrainPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.MDFileReaderDecorator
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.MDCSVFileSplitter
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDImportFromFileRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDStatisticsRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDTrainRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordDetailsRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordSpaceRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListTrainDialogRepo
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Singleton
    @Provides
    fun providesWordDetailsRepo(
        db: MDDataBase,
    ): MDWordDetailsRepo = MDWordDetailsRepoImpl(
        wordDao = db.getWordDao(),
        tagDao = db.getWordTypeTagDao(),
        languageDao = db.getLanguageDao()
    )

    @Singleton
    @Provides
    fun providesWordsListRepo(
        db: MDDataBase,
        preferences: MDPreferencesDataStore,
    ): MDWordsListRepo = MDWordsListRepoImpl(
        wordDao = db.getWordDao(),
        tagDao = db.getWordTypeTagDao(),
        wordSpaceDao = db.getLanguageWordSpaceDao(),
        preferences = preferences,
    )

    @Singleton
    @Provides
    fun providesWordsListTrainDialogRepo(
        preferences: MDPreferencesDataStore,
    ): MDWordsListTrainDialogRepo= MDWordsListTrainDialogRepoImpl(
        dataStore = preferences
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
        rawWordReader: MDFileReaderDecorator<MDRawWord>,
        rawWordCSVFileSplitter: MDCSVFileSplitter<MDRawWord>,
    ): MDImportFromFileRepo = MDImportFromFileRepoImpl(
        db = db,
        rawWordReader = rawWordReader,
        rawWordCSVFileSplitter = rawWordCSVFileSplitter
    )

    @Singleton
    @Provides
    fun providesTrainRepo(
        db: MDDataBase,
        preferences: MDPreferencesDataStore,
    ): MDTrainRepo = MDTrainRepoImpl(
        wordDao = db.getWordDao(),
        trainHistoryDao = db.getWordTrainDao(),
        preferences = preferences,
    )

    @Singleton
    @Provides
    fun provideStatisticsRepo(
        db: MDDataBase,
    ): MDStatisticsRepo = MDStatisticsRepoImpl(
        wordDao = db.getWordDao(),
        trainHistoryDao = db.getWordTrainDao(),
    )
}