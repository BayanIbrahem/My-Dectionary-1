package dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.MDJson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream

private val cacheMap: MutableMap<String, MDColorScheme> = mutableMapOf()

@Stable
sealed interface MDThemeContrast {
    val fileName: String
    val type: MDThemeContrastType
    val isDark: Boolean?
    val safeIsDark: Boolean
    @Composable
    get() = isDark ?: isSystemInDarkTheme()
    val variant: MDThemeVariant
        get() = when(isDark) {
            true ->  MDThemeVariant.Dark
            false -> MDThemeVariant.Light
            null -> MDThemeVariant.System
        }

    /**
     * return true if same [type] and same [isDark]
     */
    fun isSimilar(other: MDThemeContrast?): Boolean {
        other ?: return false
        val similar = type == other.type && (
                isDark == null || other.isDark == null || isDark == other.isDark
                )
        return similar
    }

    @Stable
    data class Normal(
        override val fileName: String,
        override val isDark: Boolean?,
    ) : MDThemeContrast {
        override val type: MDThemeContrastType = MDThemeContrastType.Normal

    }

    @Stable
    data class Medium(
        override val fileName: String,
        override val isDark: Boolean?,
    ) : MDThemeContrast {
        override val type: MDThemeContrastType = MDThemeContrastType.Medium
    }

    @Stable
    data class High(
        override val fileName: String,
        override val isDark: Boolean?,
    ) : MDThemeContrast {
        override val type: MDThemeContrastType = MDThemeContrastType.High
    }

    fun copy2(
        fileName: String = this.fileName,
        isDark: Boolean? = this.isDark,
    ) = when(this) {
        is High -> copy(fileName = fileName, isDark = isDark)
        is Medium -> copy(fileName = fileName, isDark = isDark)
        is Normal -> copy(fileName = fileName, isDark = isDark)
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun buildColorScheme(context: Context): MDColorScheme {
        return cacheMap.getOrPut(fileName) {
            context.assets.open(fileName).use { stream ->
                MDJson.decodeFromStream(MDColorScheme.serializer(), stream)
            }
        }
    }
}