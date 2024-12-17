package dev.bayan_ibrahim.my_dictionary.data_source.local.train

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.calculateOutput
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.inSeconds
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordResult
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class MDTrainMemoryDecayFactorAnswerDurationBasedDataSource(
    val defaultAnswerDuration: Duration = 30.seconds,
    val maxAnswerDuration: Duration = defaultAnswerDuration * 2,
    val rightAnswerBaseline: Float = 2f,
    val rightAnswerExtra: Float = 1f,
    val wrongAnswerBaseline: Float = 0.5f,
    val wrongAnswerExtra: Float = 0.1f,
    val passAnswerBaseline: Float = 0.75f,
    val passAnswerExtra: Float = 0.05f,
) : MDTrainMemoryDecayFactorDataSource {
    /**
     * calculate new decay factor by multiplying it with **DECAY FACTOR VARIANCE**
     * available options:
     *
     * **TIMEOUT**: no change effect
     *
     * **RIGHT ANSWER**:
     * - max multiplier ([rightAnswerBaseline] + [rightAnswerExtra])
     * - min multiplier 1
     * - for range 0s->[defaultAnswerDuration] multiplier would be max -> [rightAnswerBaseline]
     * - for range [defaultAnswerDuration] -> [maxAnswerDuration] multiplier would be [rightAnswerBaseline] -> min
     *
     * **WRONG ANSWER**:
     * - max multiplier 1
     * - min multiplier ([wrongAnswerBaseline] + [wrongAnswerExtra])
     * - for range 0s->[defaultAnswerDuration] multiplier would be min -> [wrongAnswerBaseline]
     * - for range [defaultAnswerDuration] -> [maxAnswerDuration] multiplier would be [wrongAnswerBaseline] -> max
     *
     * **PASS**:
     * same of wrong answer but with less effect
     */
    override fun calculateDecayOf(
        word: Word,
        answer: MDTrainWordResult,
    ): Float {
        val factorDiff = when (answer) {
            is MDTrainWordResult.Timeout -> 1f
            is MDTrainWordResult.Right -> {
                val baselineFactorValue = baselineOf(answer.consumedDuration, rightAnswerBaseline)
                val extraFactorValue = extraOf(answer.consumedDuration, rightAnswerExtra)
                baselineFactorValue + extraFactorValue
            }

            is MDTrainWordResult.Pass -> {
                val baselineFactorValue = baselineOf(answer.consumedDuration, passAnswerBaseline)
                val extraFactorValue = extraOf(answer.consumedDuration, passAnswerExtra)
                baselineFactorValue - extraFactorValue
            }

            is MDTrainWordResult.Wrong -> {
                val baselineFactorValue = baselineOf(answer.consumedDuration, wrongAnswerBaseline)
                val extraFactorValue = extraOf(answer.consumedDuration, wrongAnswerExtra)
                baselineFactorValue - extraFactorValue
            }
        }
        return word.memoryDecayFactor * factorDiff
    }

    private fun baselineOf(
        consumedDuration: Duration,
        baseline: Float,
    ) = calculateOutput(
        input = consumedDuration.coerceAtLeast(defaultAnswerDuration).inSeconds,
        inputRangeStart = defaultAnswerDuration.inSeconds,
        inputRangeEnd = maxAnswerDuration.inSeconds,
        outputRangeStart = baseline,
        outputRangeEnd = 1f
    )

    private fun extraOf(
        consumedDuration: Duration,
        extra: Float,
    ) = calculateOutput(
        input = consumedDuration.inSeconds,
        inputRangeStart = 0f,
        inputRangeEnd = defaultAnswerDuration.inSeconds,
        outputRangeStart = extra,
        outputRangeEnd = 0f
    ).coerceAtLeast(0f)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MDTrainMemoryDecayFactorAnswerDurationBasedDataSource) return false

        if (defaultAnswerDuration != other.defaultAnswerDuration) return false
        if (maxAnswerDuration != other.maxAnswerDuration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = defaultAnswerDuration.hashCode()
        result = 31 * result + maxAnswerDuration.hashCode()
        return result
    }

    override fun toString(): String {
        return "MDTrainMemoryDecayFactorAnswerDurationBasedDataSource(defaultAnswerDuration=$defaultAnswerDuration, maxAnswerDuration=$maxAnswerDuration)"
    }
}