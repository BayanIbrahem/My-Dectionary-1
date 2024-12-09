package dev.bayan_ibrahim.my_dictionary.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWord
import dev.bayan_ibrahim.my_dictionary.data.MDImportFromFileRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordDetailsRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordSpaceRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.MDWordsListRepoImpl
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferences
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.MDFileReaderDecorator
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.MDCSVFileSplitter
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDImportFromFileRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordDetailsRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordSpaceRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDWordsListRepo
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
        preferences: MDPreferences,
    ): MDWordsListRepo = MDWordsListRepoImpl(
        wordDao = db.getWordDao(),
        tagDao = db.getWordTypeTagDao(),
        wordSpaceDao = db.getLanguageWordSpaceDao(),
        preferences = preferences,
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
        rawWordCSVFileSplitter: MDCSVFileSplitter<MDRawWord>
    ): MDImportFromFileRepo = MDImportFromFileRepoImpl(
        db = db,
        rawWordReader = rawWordReader,
        rawWordCSVFileSplitter = rawWordCSVFileSplitter
    )
}