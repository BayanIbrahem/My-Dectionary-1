package dev.bayan_ibrahim.my_dictionary.data_source.local.text_to_speech.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.data_source.local.text_to_speech.MDTllTextToSpeechDataSource
import dev.bayan_ibrahim.my_dictionary.data_source.local.text_to_speech.TextToSpeechDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TextToSpeechModel {
    @Provides
    @Singleton
    fun provideTextToSpeech(
        @ApplicationContext
        context: Context,
    ): TextToSpeechDataSource = MDTllTextToSpeechDataSource(context)
}