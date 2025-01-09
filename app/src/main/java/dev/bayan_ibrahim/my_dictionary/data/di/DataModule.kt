package dev.bayan_ibrahim.my_dictionary.data.di

import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.data.MDDataStoreTrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.data.MDDataStoreUserPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.data.MDDataStoreViewPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.data.MDRoomContextTagRepo
import dev.bayan_ibrahim.my_dictionary.data.MDRoomExportToFileRepo
import dev.bayan_ibrahim.my_dictionary.data.MDRoomImportFromFileRepo
import dev.bayan_ibrahim.my_dictionary.data.MDRoomLanguageRepo
import dev.bayan_ibrahim.my_dictionary.data.MDRoomTrainHistoryRepo
import dev.bayan_ibrahim.my_dictionary.data.MDRoomTypeTagRepo
import dev.bayan_ibrahim.my_dictionary.data.MDRoomWordRepo
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.read.MDFileReaderAbstractFactory
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write.MDFileWriterFactory
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_manager.FileManager
import dev.bayan_ibrahim.my_dictionary.domain.repo.ContextTagRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.ExportToFileRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.ImportFromFileRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.LanguageRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.TrainHistoryRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.TrainPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.TypeTagRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.UserPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.ViewPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordRepo
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    fun provideUserPreferencesRepo(
        dataStore: MDPreferencesDataStore,
    ): UserPreferencesRepo = MDDataStoreUserPreferencesRepo(dataStore)

    @Provides
    @Singleton
    fun provideViewPreferencesRepo(
        dataStore: MDPreferencesDataStore,
    ): ViewPreferencesRepo = MDDataStoreViewPreferencesRepo(
        dataStore = dataStore
    )

    @Provides
    @Singleton
    fun provideTrainPreferencesRepo(
        dataStore: MDPreferencesDataStore,
    ): TrainPreferencesRepo = MDDataStoreTrainPreferencesRepo(
        dataStore = dataStore
    )

    @Provides
    @Singleton
    fun provideContextTagRepo(
        db: MDDataBase,
    ): ContextTagRepo = MDRoomContextTagRepo(
        contextTagDao = db.getContextTagDao(),
        wordsCrossContextTagDao = db.getWordsCrossTagsDao()
    )

    @Singleton
    @Provides
    fun provideImportFromFileRepo(
        db: MDDataBase,
        abstractFactory: MDFileReaderAbstractFactory,
    ): ImportFromFileRepo = MDRoomImportFromFileRepo(
        db = db,
        abstractFactory = abstractFactory,
        appVersion = Build.VERSION.RELEASE
    )

    @Singleton
    @Provides
    fun provideExportToFileRepo(
        fileWriterFactory: MDFileWriterFactory,
        wordRepo: WordRepo,
        typeTagRepo: TypeTagRepo,
        fileManager: FileManager,
    ): ExportToFileRepo = MDRoomExportToFileRepo(
        fileWriterFactory = fileWriterFactory,
        wordRepo = wordRepo,
        typeTagRepo = typeTagRepo,
        fileManager = fileManager
    )

    @Singleton
    @Provides
    fun provideLanguageRepo(
        db: MDDataBase,
    ): LanguageRepo = MDRoomLanguageRepo(
        languageDao = db.getLanguageDao(),
        wordSpaceDao = db.getLanguageWordSpaceDao()
    )

    @Singleton
    @Provides
    fun provideTrainHistoryRepo(db: MDDataBase): TrainHistoryRepo = MDRoomTrainHistoryRepo(db = db)

    @Singleton
    @Provides
    fun provideTypeTagRepo(db: MDDataBase): TypeTagRepo = MDRoomTypeTagRepo(db = db)

    @Singleton
    @Provides
    fun provideWordRepo(db: MDDataBase): WordRepo = MDRoomWordRepo(db = db)
}