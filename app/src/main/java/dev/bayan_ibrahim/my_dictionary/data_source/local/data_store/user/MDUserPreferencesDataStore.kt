package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.user

import android.content.Context
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.UserPreferencesProto
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.UserPreferencesTheme
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.UserPreferencesThemeContrast
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.copy
import dev.bayan_ibrahim.my_dictionary.domain.model.MDUserPreferences
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsPack
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeContrastType
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MDThemeVariant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface MDUserPreferencesDataStore {
    fun getUserPreferencesStream(): Flow<MDUserPreferences>
    suspend fun getUserPreferences() = getUserPreferencesStream().first()
    suspend fun writeUserPreferences(getUser: (oldPreferences: MDUserPreferences) -> MDUserPreferences)
}

class MDUserPreferencesDataStoreImpl(
    context: Context,
) : MDUserPreferencesDataStore {
    private val proto = context.userDaterStore
    override fun getUserPreferencesStream(): Flow<MDUserPreferences> = proto.data.map { it: UserPreferencesProto ->
        MDUserPreferences(
            selectedLanguagePage = if (it.hasSelectedLanguagePage()) {
                it.selectedLanguagePage.code.getLanguage()
            } else {
                null
            },
            theme = MDTheme.of(it.themeKey),
            themeVariant = when (it.theme) {
                UserPreferencesTheme.Light -> MDThemeVariant.Light
                UserPreferencesTheme.Dark -> MDThemeVariant.Dark
                null, UserPreferencesTheme.UNRECOGNIZED, UserPreferencesTheme.System -> MDThemeVariant.System
            },
            themeContrastType = when (it.contrast) {
                UserPreferencesThemeContrast.Medium -> MDThemeContrastType.Medium
                UserPreferencesThemeContrast.High -> MDThemeContrastType.High
                null, UserPreferencesThemeContrast.UNRECOGNIZED, UserPreferencesThemeContrast.Normal -> MDThemeContrastType.Normal
            },
            iconsPack = it.iconsSet.takeIf {
                it in 0..<MDIconsPack.entries.count()
            }?.let {
                MDIconsPack.entries[it]
            } ?: MDIconsPack.Default,
            liveMemorizingProbability = it.liveMemorizingProbability
        )
    }

    override suspend fun writeUserPreferences(getUser: (oldPreferences: MDUserPreferences) -> MDUserPreferences) {
        proto.updateData {
            it.copy {
                val user = getUser(getUserPreferences())
                val code = user.selectedLanguagePage?.code
                if (code == null) {
                    this.clearSelectedLanguagePage()
                } else {
                    this.selectedLanguagePage = code
                }
                this.themeKey = user.theme.key
                this.theme = when (user.themeVariant) {
                    MDThemeVariant.System -> UserPreferencesTheme.System
                    MDThemeVariant.Light -> UserPreferencesTheme.Light
                    MDThemeVariant.Dark -> UserPreferencesTheme.Dark
                }
                this.contrast = when (user.themeContrastType) {
                    MDThemeContrastType.Normal -> UserPreferencesThemeContrast.Normal
                    MDThemeContrastType.Medium -> UserPreferencesThemeContrast.Medium
                    MDThemeContrastType.High -> UserPreferencesThemeContrast.High
                }
                this.iconsSet = user.iconsPack.ordinal
                this.liveMemorizingProbability = user.liveMemorizingProbability
            }
        }
    }
}