package dev.bayan_ibrahim.my_dictionary.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.data.WordDetailsRepoImpl
import dev.bayan_ibrahim.my_dictionary.data.WordsListRepoImpl
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferences
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordDetailsRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordsListRepo
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Singleton
    @Provides
    fun providesWordDetailsRepo(
        db: MDDataBase,
    ): WordDetailsRepo = WordDetailsRepoImpl(
        wordDao = db.getWordDao(),
        tagDao = db.getWordTypeTagDao()
    )

    @Singleton
    @Provides
    fun providesWordsListRepo(
        db: MDDataBase,
        preferences: MDPreferences,
    ): WordsListRepo = WordsListRepoImpl(
        wordDao = db.getWordDao(),
        tagDao = db.getWordTypeTagDao(),
        preferences = preferences
    )
}