package dev.bayan_ibrahim.my_dictionary.data_source.local.train

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.asEpochMillisecondsInstant
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.ceil
import kotlin.math.roundToInt

interface MDTrainDataSource {
    /**
     * return a list of values of memory of the memorizing probability based on last train time
     */
    fun memoryDecayFunctionData(
        word: Word,
        startTimeMillis: Long,
        endTimeMillis: Long,
        stepSizeMillis: Long = 86_400_0,  // 1/100 day
    ): List<Pair<Instant, Float>> {
        val stepsCount = ceil(((endTimeMillis - startTimeMillis) / stepSizeMillis.toDouble())).roundToInt()
        val values = List(stepsCount) { i ->
            val time = i.times(stepSizeMillis).plus(startTimeMillis).coerceAtMost(endTimeMillis).asEpochMillisecondsInstant()
            time to memoryDecayFormula(word, time)
        }
        return values
    }

    // FIXME, there there is some positions that pass words with some missed data but at time of using it the missed data has no effect
    fun memoryDecayFormula(
        word: Word,
        currentTime: Instant = Clock.System.now(),
    ): Float

    companion object {
        val Default: MDTrainDataSource = MDTrainDataSourceHermannEbbinghaus
    }
}