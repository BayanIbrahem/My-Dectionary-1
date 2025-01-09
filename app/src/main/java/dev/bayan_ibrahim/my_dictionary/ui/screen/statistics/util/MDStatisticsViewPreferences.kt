package dev.bayan_ibrahim.my_dictionary.ui.screen.statistics.util

import dev.bayan_ibrahim.my_dictionary.domain.model.count_enum.MDStatisticsMostResentHistoryCount
import dev.bayan_ibrahim.my_dictionary.domain.model.date.MDDateUnit
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import kotlinx.serialization.Serializable

private typealias MDLanguage = Language

@Serializable
sealed interface MDStatisticsViewPreferences {
    val dateUnit: MDDateUnit

    /**
     * show history of the latest period
     */
    fun copyWith(
        dateUnit: MDDateUnit,
    ): MDStatisticsViewPreferences = when (this) {
        is Date -> copy(dateUnit)
        is Language -> copy(dateUnit = dateUnit)
        is Tag -> copy(dateUnit = dateUnit)
        is Train -> copy(dateUnit = dateUnit)
        is TypeTag -> copy(dateUnit = dateUnit)
        is Word -> copy(dateUnit = dateUnit)
    }

    @Serializable
    data class Date(
        override val dateUnit: MDDateUnit = DEFAULT_DATE_UNIT,
    ) : MDStatisticsViewPreferences

    /**
     * show most recent train history
     */
    @Serializable
    data class Train(
        val count: MDStatisticsMostResentHistoryCount = MDStatisticsMostResentHistoryCount._1,
        override val dateUnit: MDDateUnit = DEFAULT_DATE_UNIT,
    ) : MDStatisticsViewPreferences

    /**
     * show history of word
     */
    @Serializable
    data class Word(
        val wordId: Long,
        override val dateUnit: MDDateUnit = DEFAULT_DATE_UNIT,
    ) : MDStatisticsViewPreferences

    /**
     * show history of language
     */
    @Serializable
    data class Language(
        val language: MDLanguage,
        override val dateUnit: MDDateUnit = DEFAULT_DATE_UNIT,
    ) : MDStatisticsViewPreferences

    /**
     * show history of tag (not type tag but the normal tag in the word
     */
    @Serializable
    data class Tag(
        val tagId: Long,
        override val dateUnit: MDDateUnit = DEFAULT_DATE_UNIT,
    ) : MDStatisticsViewPreferences

    /**
     * show history of type tag
     */
    @Serializable
    data class TypeTag(
        val typeTagId: Long,
        override val dateUnit: MDDateUnit = DEFAULT_DATE_UNIT,
    ) : MDStatisticsViewPreferences

    companion object {
        val DEFAULT_DATE_UNIT: MDDateUnit = MDDateUnit.Day
        val Default: MDStatisticsViewPreferences
            get() = Date()
    }
}