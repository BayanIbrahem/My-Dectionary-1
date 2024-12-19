package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.compose.runtime.Composable
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum


enum class MDWordsListSearchTarget(
    override val strLabel: String,
    val includeMeaning: Boolean,
    val includeTranslation: Boolean,
) : LabeledEnum {
    Meaning(strLabel = "Meaning Only", includeMeaning = true, includeTranslation = false), // TODO, string res
    Translation(strLabel = "Translations Only", includeMeaning = false, includeTranslation = true),// TODO, string res
    All(strLabel = "Meaning and translations", includeMeaning = true, includeTranslation = true);// TODO, string res
}

enum class MDWordsListViewPreferencesSortBy(
    override val strLabel: String,
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    Meaning("Meaning", MDIconsSet.WordMeaning),// TODO, string res // checked
    Translation("Translation", MDIconsSet.WordTranslation),// TODO, string res // checked
    MemorizingProbability("Learning Progress", MDIconsSet.MemorizingProbability);// TODO, string res // checked
//    CreatedAt, // TODO, coming soon
//    UpdatedAt
}

enum class WordsListTrainPreferencesSortBy(
    override val strLabel: String,
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    MemorizingProbability("Memorizing Probability", MDIconsSet.MemorizingProbability), // checked
    TrainingTime("Training Time", MDIconsSet.TrainTime),// checked
    CreateTime("Create Time", MDIconsSet.CreateTime),// checked
    Random("Random", MDIconsSet.Random);// checked

    @Composable
    fun orderLabel(order: MDWordsListSortByOrder): String = when (order) {
        // TODO, string res
        MDWordsListSortByOrder.Asc -> when (this) {
            MemorizingProbability -> "Words with the fewest learning progress"
            TrainingTime -> "Words trained by long time ago"
            CreateTime -> "Words created long time ago"
            Random -> "Random order"
        }

        // TODO, string res
        MDWordsListSortByOrder.Desc -> when (this) {
            MemorizingProbability -> "Words with the largest learning progress"
            TrainingTime -> "Words trained by recently"
            CreateTime -> "Words created recently"
            Random -> "Random order"
        }
    }
}

enum class MDWordsListSortByOrder(
    override val strLabel: String,
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    Asc(
        strLabel = "Asc",
        icon = MDIconsSet.AscSort // checked
    ), // TODO, string res
    Desc(
        strLabel = "Desc",
        icon = MDIconsSet.DescSort // checked
    ), // TODO, string res
}


enum class MDWordsListMemorizingProbabilityGroup(override val strLabel: String, val learningRange: ClosedFloatingPointRange<Float>) : LabeledEnum {
    NotLearned("Not Learned", 0f..0.25f),
    Known("Known", 0.25f..0.5f),
    Acknowledged("Acknowledged", 0.5f..0.75f),
    Learned("Learned", 0.75f..1f);

    companion object {
        fun of(percent: Float): MDWordsListMemorizingProbabilityGroup {
            val coercedPercent = percent.coerceIn(0f..1f)
            return entries.first {
                coercedPercent in it.learningRange
            }
        }
    }
}
