package dev.bayan_ibrahim.my_dictionary.ui.navigate

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.TopLevel.WordsList
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.export_to_file.util.MDExportToFilePreferences
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.MDStatisticsViewPreferences
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

private val destinationsList = mutableListOf<KClass<out MDDestination>>()
private val topLevelDestinationsList = mutableListOf<KClass<out MDDestination.TopLevel>>()
private val lockDrawerGestureDestinationsList = mutableListOf<KClass<out MDDestination>>()

@Serializable
sealed class MDDestination {
    init {
        destinationsList.add(this::class)
    }

    @Serializable
    sealed class TopLevel : MDDestination() {
        init {
            topLevelDestinationsList.add(this::class)
        }

        @Serializable
        data class WordsList(val languageCode: String? = null) : TopLevel()

        @Serializable
        data object Statistics : TopLevel()

        @Serializable
        data object Profile : TopLevel()

        @Serializable
        data object WordSpace : TopLevel()

        enum class Enum(
            override val icon: MDIconsSet,
            override val strLabel: String,
        ) : IconedEnum, LabeledEnum {
            WordsList(MDIconsSet.WordsList, "Words List"),

            /**
             * same of [MDDestination.Statistics] but this take the [MDStatisticsViewPreferences.Date] preferences
             */
            Statistics(MDIconsSet.Statistics, "Statistics"),
            Profile(MDIconsSet.Profile, "Profile"),
            WordSpace(MDIconsSet.LanguageWordSpace, "Word Spaces");

            val route
                get() = when (this) {
                    WordsList -> WordsList()
                    Statistics -> TopLevel.Statistics
                    Profile -> TopLevel.Profile
                    WordSpace -> TopLevel.WordSpace
                }
        }
    }

    @Serializable
    data class WordDetailsViewMode(
        val wordId: Long,
        val languageCode: String,
    ) : MDDestination()

    @Serializable
    data class WordDetailsEditMode(
        val wordId: Long? = null,
        val languageCode: String,
    ) : MDDestination()

    @Serializable
    data object ImportFromFile : MDDestination()

    @Serializable
    data class ExportToFile(
        val preferencesData: String? = null,
    ) : MDDestination() {
        val preferences: MDExportToFilePreferences
            get() = preferencesData?.let {
                Json.decodeFromString(
                    deserializer = MDExportToFilePreferences.serializer(),
                    string = preferencesData
                )
            } ?: MDExportToFilePreferences.Default

        constructor(preferences: MDExportToFilePreferences) : this(
            Json.encodeToString(
                serializer = MDExportToFilePreferences.serializer(),
                value = preferences
            )
        )

    }

    @Serializable
    data object Sync : MDDestination()

    @Serializable
    data object Train : MDDestination() {
        init {
            lockDrawerGestureDestinationsList.add(this::class)
        }
    }

    /**
     * this is the same destination in statistics enum for top level destinations but it specify the view preferences
     */
    @Serializable
    data class Statistics(
        val preferencesData: String ? = null,
    ) : MDDestination() {

        val preferences: MDStatisticsViewPreferences
            get() = preferencesData?.let {
                Json.decodeFromString(
                    MDStatisticsViewPreferences.serializer(),
                    preferencesData,
                )
            } ?: MDStatisticsViewPreferences.Default

        constructor(
            preferences: MDStatisticsViewPreferences = MDStatisticsViewPreferences.Date(),
        ) : this(
            Json.encodeToString(
                serializer = MDStatisticsViewPreferences.serializer(),
                value = preferences,
            )
        )
    }

    @Serializable
    data object AppTheme : MDDestination()

    @Serializable
    data object MarkerTags : MDDestination()

    @Serializable
    data object MigrateTags : MDDestination()

    @Serializable
    data object MigrateSimilarWords : MDDestination()
}

/**
 * return what route is it or null can not be specified
 */
fun NavDestination.getCurrentDestination(): KClass<out MDDestination>? {
    val d = destinationsList.firstNotNullOfOrNull {
        if (hasRoute(it)) it else null
    }
    return d
}

fun KClass<out MDDestination>.allowDrawerGestures(): Boolean = this !in lockDrawerGestureDestinationsList
fun KClass<out MDDestination>.isTopLevel(): Boolean = this in topLevelDestinationsList
fun KClass<out MDDestination>.getTopLevelEnum(): MDDestination.TopLevel.Enum? = MDDestination.TopLevel.Enum.entries.firstOrNull {
    this == it.route::class
}

