package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.read.MDFileReaderAbstractFactory
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.write.MDFileWriterFactory
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_manager.FileManager
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_manager.MDAndroidFileManager
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.read.MDJsonFileReaderFactory
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.write.MDJsonFileWriter
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {
    companion object {
        const val INPUT_JSON_NAME = "input"
        const val OUTPUT_JSON_NAME = "output"
    }

    @Singleton
    @Provides
    @Named(INPUT_JSON_NAME)
    fun providesInputJson(): Json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Singleton
    @Provides
    @Named(OUTPUT_JSON_NAME)
    fun providesOutputJson(): Json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    @Singleton
    @Provides
    fun provideFileReaderAbstractFactory(
        @Named(INPUT_JSON_NAME)
        json: Json,
        @ApplicationContext
        context: Context,
    ): MDFileReaderAbstractFactory = MDFileReaderAbstractFactory(
        factories = listOf(
            MDJsonFileReaderFactory(json) {
                with(context) {
                    it.open()
                }
            }
        )
    )

    @Singleton
    @Provides
    @Named("available_versions")
    fun provideAvailableVersions(
        abstractFactory: MDFileReaderAbstractFactory,
    ): List<Int> = abstractFactory.availableVersions


    @Singleton
    @Provides
    fun provideFileWriterAbstractFactory(
        @Named(OUTPUT_JSON_NAME)
        json: Json,
        @ApplicationContext
        context: Context,
    ): MDFileWriterFactory = MDFileWriterFactory(
        writers = listOf(
            MDJsonFileWriter(
                json = json,
                version = 1,
            ) {
                context.contentResolver.openOutputStream(
                    it.uri
                ) ?: throw IOException("Unable to open output stream from $it")
            }
        )
    )

    @Singleton
    @Provides
    fun providesFileManager(
        @ApplicationContext
        context: Context,
    ): FileManager = MDAndroidFileManager(context)
}