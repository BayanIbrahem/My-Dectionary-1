package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum


enum class WordsListSearchTarget(
    override val strLabel: String,
    val includeMeaning: Boolean,
    val includeTranslation: Boolean,
) : LabeledEnum {
    Meaning(strLabel = "Meaning Only", includeMeaning = true, includeTranslation = false), // TODO, string res
    Translation(strLabel = "Translations Only", includeMeaning = false, includeTranslation = true),// TODO, string res
    All(strLabel = "Meaning and translations", includeMeaning = true, includeTranslation = true);// TODO, string res
}

enum class WordsListViewPreferencesSortBy(override val strLabel: String) : LabeledEnum {
    Meaning("Meaning"),// TODO, string res
    Translation("Translation"),// TODO, string res
    LearningProgress("Learning Progress");// TODO, string res
//    CreatedAt, // TODO, coming soon
//    UpdatedAt
}

enum class WordsListTrainPreferencesSortBy(
    override val strLabel: String,
    override val icon: ImageVector,
) : LabeledEnum, IconedEnum {
    LearningProgress("Learning Progress", Icons.Default.Star), // TODO, string res, TODO, icon res
    TrainingTime("Training Time", Icons.Default.Star),// TODO, string res, icon res
    CreateTime("Create Time", Icons.Default.Star),// TODO, string res, icon res
    Random("Random", Icons.Default.Star);// TODO, string res, icon res

    @Composable
    fun orderLabel(order: WordsListSortByOrder): String = when (order) {
        WordsListSortByOrder.Asc -> when (this) {
            LearningProgress -> "Words with the fewest learning progress"
            TrainingTime -> "Words trained by long time ago"
            CreateTime -> "Words created long time ago"
            Random -> "Random order"
        }

        WordsListSortByOrder.Desc -> when (this) {
            LearningProgress -> "Words with the largest learning progress"
            TrainingTime -> "Words trained by recently"
            CreateTime -> "Words created recently"
            Random -> "Random order"
        }
    }
}

@Suppress("EnumEntryName")
enum class WordsListTrainPreferencesLimit(val count: Int) : LabeledEnum {
    _5(5),
    _10(10),
    _20(20),
    _25(25),
    _50(50),
    _75(75),
    _100(100);

    override val strLabel = "$count Words" // TODO, string res

}

enum class WordsListSortByOrder(override val strLabel: String, override val icon: ImageVector) : LabeledEnum, IconedEnum {
    Asc(
        strLabel = "Asc",
        icon = Icons.Default.KeyboardArrowUp
    ), // TODO, string res, icon res
    Desc(
        strLabel = "Desc",
        icon = Icons.Default.KeyboardArrowUp
    ), // TODO, string res, icon res
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
