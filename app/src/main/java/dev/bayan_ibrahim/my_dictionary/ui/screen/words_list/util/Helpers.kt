package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

interface LabeledEnum {
    val strLabel: String

    val label: String
        @Composable
        @ReadOnlyComposable
        get() = strLabel //TODO,string res
}

enum class WordsListSearchTarget(
    override val strLabel: String,
    val includeMeaning: Boolean,
    val includeTranslation: Boolean,
) : LabeledEnum {
    Meaning(strLabel = "Meaning Only", includeMeaning = true, includeTranslation = false), // TODO, string res
    Translation(strLabel = "Translations Only", includeMeaning = false, includeTranslation = true),// TODO, string res
    All(strLabel = "Meaning and translations", includeMeaning = true, includeTranslation = true);// TODO, string res
}

enum class WordsListSortBy(override val strLabel: String) : LabeledEnum {
    Meaning("Meaning"),// TODO, string res
    Translation("Translation"),// TODO, string res
    LearningProgress("Learning Progress");// TODO, string res
//    CreatedAt, // TODO, coming soon
//    UpdatedAt
}

enum class WordsListSortByOrder(override val strLabel: String) : LabeledEnum {
    Asc("Asc"),
    Desc("Desc"),
}

enum class WordsListLearningProgressGroup(override val strLabel: String, val learningRange: ClosedFloatingPointRange<Float>) : LabeledEnum {
    NotLearned("Not Learned", 0f..0.25f),
    Known("Known", 0.25f..0.5f),
    Acknowledged("Acknowledged", 0.5f..0.75f),
    Learned("Learned", 0.75f..1f);

    companion object {
        fun of(percent: Float): WordsListLearningProgressGroup {
            val coercedPercent = percent.coerceIn(0f..1f)
            return entries.first {
                coercedPercent in it.learningRange
            }
        }
    }
}