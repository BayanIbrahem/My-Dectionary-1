package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store

import android.content.Context
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.user.MDUserPreferences
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.user.MDUserPreferencesImpl
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListViewPreferences
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter.MDWordsListViewPreferencesImpl
import javax.inject.Inject

class MDPreferences @Inject constructor(
    context: Context,
): MDUserPreferences by MDUserPreferencesImpl(context), MDWordsListViewPreferences by MDWordsListViewPreferencesImpl(context)


