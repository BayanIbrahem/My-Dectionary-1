package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.WordsListTrainPreferencesProto
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.WordsListViewPreferencesProto

val Context.wordsListViewPreferencesDataStore: DataStore<WordsListViewPreferencesProto> by dataStore(
    fileName = "words_list_view_preferences_proto.pb",
    serializer = WordsListViewPreferencesSerializer
)

val Context.wordsListTrainPreferencesDataStore: DataStore<WordsListTrainPreferencesProto> by dataStore(
    fileName = "words_list_train_preferences_proto.pb",
    serializer = WordsListTrainPreferencesSerializer
)
