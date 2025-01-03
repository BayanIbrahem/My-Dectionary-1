package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.read.MDFileReaderAbstractFactory
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core.read.MDJsonFileReaderFactory
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {
    @Singleton
    @Provides
    fun providesJson(): Json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Singleton
    @Provides
    fun provideFileReaderAbstractFactory(
        json: Json,
        @ApplicationContext
        context: Context,
    ): MDFileReaderAbstractFactory = MDFileReaderAbstractFactory(
        factories = listOf(
            MDJsonFileReaderFactory(json) {
                context.contentResolver.openInputStream(
                    it.uri
                ) ?: throw IOException("can not open input stream for file ${it.name}, (${it.uri})")
            }
        )
    )
    @Singleton
    @Provides
    @Named("available_versions")
    fun provideAvailableVersions(
        abstractFactory: MDFileReaderAbstractFactory
    ): List<Int> = abstractFactory.availableVersions
}