package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.user

import android.content.Context
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.UserPreferencesProto
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.copy
import dev.bayan_ibrahim.my_dictionary.domain.model.UserPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface MDUserPreferences {
    fun getUserPreferencesStream(): Flow<UserPreferences>
    suspend fun getUserPreferences() = getUserPreferencesStream().first()
    suspend fun writeUserPreferences(getUser: (oldPreferences: UserPreferences) -> UserPreferences)
}

class MDUserPreferencesImpl(
    context: Context,
) : MDUserPreferences {
    private val proto = context.userDaterStore
    override fun getUserPreferencesStream(): Flow<UserPreferences> = proto.data.map { it: UserPreferencesProto ->
        UserPreferences(
            selectedLanguagePage = if (it.hasSelectedLanguagePage()) {
                it.selectedLanguagePage.code.language
            } else {
                null
            }
        )
    }

    override suspend fun writeUserPreferences(getUser: (oldPreferences: UserPreferences) -> UserPreferences) {
        proto.updateData {
            it.copy {
                val user = getUser(getUserPreferences())
                val code = user.selectedLanguagePage?.code
                if (code == null) {
                    this.clearSelectedLanguagePage()
                } else {
                    this.selectedLanguagePage = code.code
                }
            }
        }
    }
}