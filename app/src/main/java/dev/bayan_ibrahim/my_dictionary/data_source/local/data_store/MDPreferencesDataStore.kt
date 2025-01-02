package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.user.MDUserPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.user.MDUserPreferencesDataStoreImpl
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListTrainPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListDataStoreTrainPreferencesImpl
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListViewPreferencesDataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListDataStoreViewPreferencesImpl
import javax.inject.Inject

class MDPreferencesDataStore @Inject constructor(
    @ApplicationContext
    context: Context,
) : MDUserPreferencesDataStore by MDUserPreferencesDataStoreImpl(context),
    MDWordsListViewPreferencesDataStore by MDWordsListDataStoreViewPreferencesImpl(context),
    MDWordsListTrainPreferencesDataStore by MDWordsListDataStoreTrainPreferencesImpl(context)
