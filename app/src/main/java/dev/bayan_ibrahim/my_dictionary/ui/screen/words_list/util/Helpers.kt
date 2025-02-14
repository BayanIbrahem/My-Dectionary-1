package dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum


enum class MDWordsListSearchTarget(
    val includeMeaning: Boolean,
    val includeTranslation: Boolean,
) : LabeledEnum {
    Meaning(includeMeaning = true, includeTranslation = false),
    Translation(includeMeaning = false, includeTranslation = true),
    All(includeMeaning = true, includeTranslation = true);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Meaning -> stringResource(
                R.string.x_only,
                firstCapStringResource(R.string.meaning)
            )

            Translation -> stringResource(
                R.string.x_only,
                firstCapStringResource(R.string.translation)
            )

            All -> stringResource(
                R.string.x_and_y,
                firstCapStringResource(R.string.meaning),
                firstCapStringResource(R.string.translation)
            )
        }
}

enum class MDWordsListViewPreferencesSortBy(
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    Meaning(MDIconsSet.WordMeaning),
    Translation(MDIconsSet.WordTranslation),
    CreatedAt(MDIconsSet.CreateTime),
    UpdatedAt(MDIconsSet.Edit); // TODO, icon res

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Meaning -> firstCapStringResource(R.string.meaning)
            Translation -> firstCapStringResource(R.string.translation)
            CreatedAt -> firstCapStringResource(R.string.created_at)
            UpdatedAt -> firstCapStringResource(R.string.updated_at)
        }
}

enum class WordsListTrainPreferencesSortBy(
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    MemorizingProbability(MDIconsSet.MemorizingProbability),
    TrainingTime(MDIconsSet.TrainTime),
    CreateTime(MDIconsSet.CreateTime),
    Random(MDIconsSet.Random);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            MemorizingProbability -> firstCapStringResource(R.string.memorizing_probability)
            TrainingTime -> firstCapStringResource(R.string.train_time)
            CreateTime -> firstCapStringResource(R.string.create_time)
            Random -> firstCapStringResource(R.string.random)
        }

    @Composable
    fun orderLabel(order: MDWordsListSortByOrder): String = when (order) {
        MDWordsListSortByOrder.Asc -> when (this) {
            MemorizingProbability -> firstCapStringResource(R.string.memorizing_probability_asc_order)
            TrainingTime -> firstCapStringResource(R.string.train_time_asc_order)
            CreateTime -> firstCapStringResource(R.string.create_time_asc_order)
            Random -> firstCapStringResource(R.string.random_order)
        }

        MDWordsListSortByOrder.Desc -> when (this) {
            MemorizingProbability -> firstCapStringResource(R.string.memorizing_probability_desc_order)
            TrainingTime -> firstCapStringResource(R.string.train_time_desc_order)
            CreateTime -> firstCapStringResource(R.string.create_time_desc_order)
            Random -> firstCapStringResource(R.string.random_order)
        }
    }
}

enum class MDWordsListSortByOrder(
    @StringRes val labelRes: Int,
    override val icon: MDIconsSet,
) : LabeledEnum, IconedEnum {
    Asc(
        labelRes = R.string.asc,
        icon = MDIconsSet.AscSort
    ),
    Desc(
        labelRes = R.string.desc,
        icon = MDIconsSet.DescSort
    );

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(labelRes)
}


enum class MDWordsListMemorizingProbabilityGroup(@StringRes val labelRes: Int, val probabilityRange: ClosedFloatingPointRange<Float>) : LabeledEnum {
    NotLearned(R.string.not_learned, 0f..0.25f),
    Known(R.string.known, 0.25f..0.5f),
    Acknowledged(R.string.acknowledged, 0.5f..0.75f),
    Learned(R.string.learned, 0.75f..1f);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(labelRes)

    companion object {
        fun of(percent: Float): MDWordsListMemorizingProbabilityGroup {
            val coercedPercent = percent.coerceIn(0f..1f)
            return entries.first {
                coercedPercent in it.probabilityRange
            }
        }
    }
}
