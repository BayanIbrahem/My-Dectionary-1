package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.MDPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideMDDataStore(
        @ApplicationContext
        context: Context,
    ): MDPreferences = MDPreferences(context)
}