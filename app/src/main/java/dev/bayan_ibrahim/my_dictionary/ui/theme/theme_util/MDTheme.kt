package dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

private const val BASE_DIRECTORY = ""

private const val LIGHT_PART = "light"
private const val DARK_PART = "dark"

private const val NORMAL_CONTRAST_PART = ""
private const val MEDIUM_CONTRAST_PART = "medium-contrast"
private const val HIGH_CONTRAST_PART = "high-contrast"

private const val PARTS_SEPARATOR = "-"

private const val SUFFIX = ".json"

private fun mdJoinToString(vararg parts: String) = parts.filterNot {
    it.isEmpty()
}.joinToString(PARTS_SEPARATOR, BASE_DIRECTORY, SUFFIX)

enum class MDTheme(
    val key: String,
    @StringRes
    val labelRes: Int,

    val standardLightFileName: String = key + "/" + mdJoinToString(key, NORMAL_CONTRAST_PART, LIGHT_PART),
    val standardDarkFileName: String = key + "/" + mdJoinToString(key, NORMAL_CONTRAST_PART, DARK_PART),

    val mediumConstrainsLightFileName: String? = key + "/" + mdJoinToString(key, LIGHT_PART, MEDIUM_CONTRAST_PART),
    val mediumConstrainsDarkFileName: String? = key + "/" + mdJoinToString(key, DARK_PART, MEDIUM_CONTRAST_PART),

    val heightConstrainsLightFileName: String? = key + "/" + mdJoinToString(key, LIGHT_PART, HIGH_CONTRAST_PART),
    val heightConstrainsDarkFileName: String? = key + "/" + mdJoinToString(key, DARK_PART, HIGH_CONTRAST_PART),

    ) : LabeledEnum {
    Blue("blue", R.string.blue),
    Green("green", R.string.green),
    Yellow("yellow", R.string.yellow),
    Red("red", R.string.red);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(labelRes)

    /**
     * light and dark theme of contrast
     */
    fun getContrast(contrast: MDThemeContrastType, isDark: Boolean): MDThemeContrast {
        val normal = if (isDark) {
            standardDarkFileName.let { MDThemeContrast.Normal(it, true) }
        } else {
            standardLightFileName.let { MDThemeContrast.Normal(it, false) }
        }
        return when (contrast) {
            MDThemeContrastType.High -> if (isDark) {
                heightConstrainsDarkFileName?.let { MDThemeContrast.High(it, true) } ?: normal
            } else {
                heightConstrainsLightFileName?.let { MDThemeContrast.High(it, false) } ?: normal
            }

            MDThemeContrastType.Medium -> if (isDark) {
                mediumConstrainsDarkFileName?.let { MDThemeContrast.Medium(it, true) } ?: normal
            } else {
                mediumConstrainsLightFileName?.let { MDThemeContrast.Medium(it, false) } ?: normal
            }

            MDThemeContrastType.Normal -> normal
        }
    }

    fun getContrastVariance(contrastType: MDThemeContrastType): Pair<MDThemeContrast, MDThemeContrast> {
        val normal = Pair(
            standardLightFileName.let { MDThemeContrast.Normal(it, false) },
            standardDarkFileName.let { MDThemeContrast.Normal(it, true) },
        )
        return when (contrastType) {
            MDThemeContrastType.High -> Pair(
                heightConstrainsLightFileName?.let { MDThemeContrast.High(it, false) } ?: normal.first,
                heightConstrainsDarkFileName?.let { MDThemeContrast.High(it, true) } ?: normal.second,
            )

            MDThemeContrastType.Medium -> Pair(
                mediumConstrainsLightFileName?.let { MDThemeContrast.Medium(it, false) } ?: normal.first,
                mediumConstrainsDarkFileName?.let { MDThemeContrast.Medium(it, true) } ?: normal.second,
            )

            MDThemeContrastType.Normal -> normal
        }
    }

    fun getVarianceContrasts(isDark: Boolean = false): List<MDThemeContrast> = if (isDark) {
        listOfNotNull(
            MDThemeContrast.Normal(standardDarkFileName, true),
            mediumConstrainsDarkFileName?.let { MDThemeContrast.Medium(it, true) },
            heightConstrainsDarkFileName?.let { MDThemeContrast.High(it, true) },
        )
    } else {
        listOfNotNull(
            MDThemeContrast.Normal(standardLightFileName, false),
            mediumConstrainsLightFileName?.let { MDThemeContrast.Medium(it, false) },
            heightConstrainsLightFileName?.let { MDThemeContrast.High(it, false) },
        )
    }

    companion object {
        fun of(
            themeKey: String?,
            default: MDTheme = Default,
        ): MDTheme = entries.firstOrNull {
            it.key == themeKey
        } ?: default

        val Default: MDTheme = Blue
    }

    fun identifierOf(
        variant: MDThemeVariant,
        contrast: MDThemeContrastType,
        isSystemDarkTheme: Boolean,
    ): String {
        return if (variant.isDarkTheme(isSystemDarkTheme)) {
            when (contrast) {
                MDThemeContrastType.Normal -> standardDarkFileName
                MDThemeContrastType.Medium -> mediumConstrainsDarkFileName
                MDThemeContrastType.High -> heightConstrainsDarkFileName
            } ?: standardDarkFileName
        } else {
            when (contrast) {
                MDThemeContrastType.Normal -> standardLightFileName
                MDThemeContrastType.Medium -> mediumConstrainsLightFileName
                MDThemeContrastType.High -> heightConstrainsLightFileName
            } ?: standardLightFileName
        }
    }
}
