package dev.bayan_ibrahim.my_dictionary.ui.navigate

import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination.TopLevel.WordsList
import dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util.MDStatisticsViewPreferences
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed interface MDDestination {
    @Serializable
    sealed interface TopLevel {
        @Serializable
        data class WordsList(val languageCode: String? = null) : TopLevel

        @Serializable
        data object Statistics : TopLevel

        @Serializable
        data object Profile : TopLevel

        @Serializable
        data object WordSpace : TopLevel

        enum class Enum(
            override val icon: MDIconsSet,
        ) : IconedEnum {
            WordsList(MDIconsSet.WordsList), // checked

            /**
             * same of [MDDestination.Statistics] but this take the [MDStatisticsViewPreferences.Date] preferences
             */
            Statistics(MDIconsSet.Statistics), // checked
            Profile(MDIconsSet.Profile), // checked
            WordSpace(MDIconsSet.LanguageWordSpace); // checked

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
    data class WordDetails(
        val wordId: Long? = null,
        val languageCode: String,
    ) : MDDestination

    @Serializable
    data object ImportFromFile : MDDestination

    @Serializable
    data object ExportToFile : MDDestination

    @Serializable
    data object Sync : MDDestination

    @Serializable
    data object Train : MDDestination

    /**
     * this is the same destination in statistics enum for top level destinations but it specify the view preferences
     */
    @Serializable
    data class Statistics(
        val preferencesData: String,
//        val preferences: MDStatisticsViewPreferences = MDStatisticsViewPreferences.Date()
    ) : MDDestination {

        val preferences: MDStatisticsViewPreferences
            get() = Json.decodeFromString(
                MDStatisticsViewPreferences.serializer(),
                preferencesData,
            )

        constructor(
            preferences: MDStatisticsViewPreferences = MDStatisticsViewPreferences.Date(),
        ) : this(
            Json.encodeToString(
                MDStatisticsViewPreferences.serializer(),
                preferences,
            )
        )
    }

    @Serializable
    data object AppTheme : MDDestination
}

