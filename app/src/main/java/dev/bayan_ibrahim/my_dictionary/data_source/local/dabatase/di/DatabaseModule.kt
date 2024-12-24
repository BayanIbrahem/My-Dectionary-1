package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun providesDatabase(
        @ApplicationContext
        context: Context,
    ): MDDataBase = Room.databaseBuilder(
        context = context,
        klass = MDDataBase::class.java,
        name = "my_dictionary_db"
    )
        .fallbackToDestructiveMigrationFrom(1, 2, 3, 4, 5, 6, 7, 8)
        .build()

}