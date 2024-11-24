package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.UserPreferencesProto

val Context.userDaterStore: DataStore<UserPreferencesProto> by dataStore(
    fileName = "user.pb",
    serializer = UserPreferencesSerializer
)
