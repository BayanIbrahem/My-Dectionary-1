package dev.bayan_ibrahim.my_dictionary.data_source.local.train

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.calculateOutput
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.inSeconds
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainSubmitOption
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordResult
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordResultType
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
        val newFactor = word.memoryDecayFactor * applySubmitOptionOnFactor(
            factorDiff = factorDiff,
            option = answer.submitOption,
            resultType = answer.type
        )
        return newFactor
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

/**
 * submit option affects the effect of the answer
 *
 * - [MDTrainSubmitOption.Pass]: no effect
 * - [MDTrainSubmitOption.Guess]: new factor effect = factor effect * 0.5
 * - [MDTrainSubmitOption.Answer]: no effect
 * - [MDTrainSubmitOption.Confident]: new factor effect = factor * 2
 *
 * we ane not multiplying the old factor but we change the value of ([factorDiff] - 1) by multiplying it by the calculated number
 * ```kotlin
val factorDiff = 1.5f
val newFactor = when(option) {
    MDTrainSubmitOption.Pass -> 1.5f
    MDTrainSubmitOption.Guess -> 1.25f
    MDTrainSubmitOption.Answer -> 1.5f
    MDTrainSubmitOption.Confident -> 2f
}
```
 *
 * @param resultType is never used for now but later it may be used to modify effect shifting
 */
private fun applySubmitOptionOnFactor(
    factorDiff: Float,
    option: MDTrainSubmitOption,
    resultType: TrainWordResultType,
): Float = when (option) {
    MDTrainSubmitOption.Pass -> 1f
    MDTrainSubmitOption.Guess -> 0.5f
    MDTrainSubmitOption.Answer -> 1f
    MDTrainSubmitOption.Confident -> 2f
}.let {
    1f + factorDiff.minus(1f).times(it)
}