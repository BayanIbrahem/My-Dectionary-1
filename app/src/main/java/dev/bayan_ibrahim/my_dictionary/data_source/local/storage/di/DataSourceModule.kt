package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWord
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.MDFileReaderDecorator
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.MDFileReaderWrapper
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.MDCSVFileReader
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.MDRawWordCSVSerializer
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer.MDCSVSerializer
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.MDJsonFileReader
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Singleton
    @Provides
    fun providesRawWordFileReader(
        @ApplicationContext
        context: Context,
        json: Json,
        csvRawWordSerializer: MDCSVSerializer<MDRawWord>,
    ): MDFileReaderDecorator<MDRawWord> = MDFileReaderWrapper(
        openInputStream = {
            context.contentResolver.openInputStream(it.uri)
        },
        children = listOf(
            MDJsonFileReader.build(
                json = json,
                openInputStream = {
                    context.contentResolver.openInputStream(it.uri)
                }
            ),
            MDCSVFileReader(
                serializer = csvRawWordSerializer,
                openInputStream = {
                    context.contentResolver.openInputStream(it.uri)
                }
            )
        )
    )

    @Singleton
    @Provides
    fun providesJson(): Json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Singleton
    @Provides
    fun providesCsvRawWordSerializer(): MDCSVSerializer<MDRawWord> = MDRawWordCSVSerializer()
}