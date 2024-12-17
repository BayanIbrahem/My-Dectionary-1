package dev.bayan_ibrahim.my_dictionary.data_source.local.train

import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import kotlinx.datetime.Instant
import kotlin.math.log10
import kotlin.math.pow

private const val k = 1.84f
private const val c = 1.25f

object MDTrainDataSourceHermannEbbinghaus : MDTrainDataSource {
    /**
     * this formula is hermann ebbinghaus forgetting curve of minutes time unit
     */
    override fun memoryDecayFormula(
        word: Word,
        currentTime: Instant,
    ): Float {
        word.lastTrainTime ?: return 0f
        val f = word.memoryDecayFactor
        // t in minutes
        val t = (currentTime - word.lastTrainTime).inWholeMilliseconds / 60_000f
        // less than one minute means full remember
        if (t < 1f) return 1f
        return k / ((f * log10(t).pow(c)) + k)
    }
}