package dev.bayan_ibrahim.my_dictionary.data_source.local.timer

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MDTimerDataSourceModule {
    @Provides
    @Singleton
    fun provideTimerDataSource(): MDTimerDataSource = MDTimerDataSourceImpl()
}