package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.user.UserPreferencesSerializer
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.WordsListPreferencesProto

val Context.wordsListDataStore: DataStore<WordsListPreferencesProto> by dataStore(
    fileName = "words_list_proto.pb",
    serializer = WordsListViewPreferencesSerializer
)
