package dev.bayan_ibrahim.my_dictionary.data_source.local.train

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordResult
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class MDTrainMemoryDecayFactorAnswerDurationBasedDataSourceTest {

    private lateinit var dataSource: MDTrainMemoryDecayFactorAnswerDurationBasedDataSource
    private val defaultAnswerDuration = 30.seconds
    private val maxAnswerDuration = defaultAnswerDuration * 2
    private val rightAnswerBaseline = 2f
    private val rightAnswerExtra = 1f
    private val wrongAnswerBaseline = 0.5f
    private val wrongAnswerExtra = 0.1f

    @Before
    fun setup() {
        dataSource = MDTrainMemoryDecayFactorAnswerDurationBasedDataSource(
            defaultAnswerDuration = defaultAnswerDuration,
            maxAnswerDuration = maxAnswerDuration,
            rightAnswerBaseline = rightAnswerBaseline,
            rightAnswerExtra = rightAnswerExtra,
            wrongAnswerBaseline = wrongAnswerBaseline,
            wrongAnswerExtra = wrongAnswerExtra,
        )
    }

    @After
    fun tearDown() {
        // No resources to release in this test
    }

    @Test
    fun `calculateDecayOf - Timeout`() {
    }

    @Test
    fun `calculateDecayOf - Right - Less Than Default Duration`() {
        val word = word(memoryDecayFactor = 2f)
        val f = 0.8f
        val answer = right(consumedDuration = defaultAnswerDuration * f.toDouble())
        val baselineChange = rightAnswerBaseline
        val extraDecay = rightAnswerExtra.times(1 - f)
        val decayFactorChange = baselineChange + extraDecay
        val expectedDecay = word.memoryDecayFactor * decayFactorChange
        val actualDecay = dataSource.calculateDecayOf(word, answer)
        assertEquals(expectedDecay, actualDecay, 0.001f)
    }

    @Test
    fun `calculateDecayOf - Right - At Default Duration`() {
        val word = word(memoryDecayFactor = 2f)
        val answer = right(consumedDuration = defaultAnswerDuration)

        val baselineChange = rightAnswerBaseline
        val extraChange = 0f
        val decayFactorChange = baselineChange + extraChange
        val expectedDecay = word.memoryDecayFactor * decayFactorChange
        val actualDecay = dataSource.calculateDecayOf(word, answer)
        assertEquals(expectedDecay, actualDecay, 0.001f)
    }

    @Test
    fun `calculateDecayOf - Right - Between Default and Max Duration`() {
        val word = word(memoryDecayFactor = 2f)
        val f = 0.5f
        val extraTime = maxAnswerDuration - defaultAnswerDuration
        val answer = right(consumedDuration = defaultAnswerDuration + extraTime.times(f.toDouble()))

        val baselineChange = 1 + rightAnswerBaseline.minus(1).times(f)
        val extraChange = 0f
        val decayFactorChange = baselineChange + extraChange
        val expectedDecay = word.memoryDecayFactor * decayFactorChange
        val actualDecay = dataSource.calculateDecayOf(word, answer)
        assertEquals(expectedDecay, actualDecay, 0.001f)
    }

    @Test
    fun `calculateDecayOf - Right - At Max Duration`() {
        val word = word(memoryDecayFactor = 2f)
        val answer = right(consumedDuration = maxAnswerDuration)

        val baselineChange = 1f
        val extraChange = 0f
        val decayFactorChange = baselineChange + extraChange
        val expectedDecay = word.memoryDecayFactor * decayFactorChange
        val actualDecay = dataSource.calculateDecayOf(word, answer)
        assertEquals(expectedDecay, actualDecay, 0.001f)
    }

    @Test
    fun `calculateDecayOf - Wrong - Less Than Default Duration`() {
        val word = word(memoryDecayFactor = 2f)
        val f = 0.8f
        val answer = wrong(consumedDuration = defaultAnswerDuration * f.toDouble())
        val baselineChange = wrongAnswerBaseline
        val extraDecay = wrongAnswerExtra.times(1 - f)
        val decayFactorChange = baselineChange - extraDecay
        val expectedDecay = word.memoryDecayFactor * decayFactorChange
        val actualDecay = dataSource.calculateDecayOf(word, answer)
        assertEquals(expectedDecay, actualDecay, 0.001f)
    }

    @Test
    fun `calculateDecayOf - Wrong - At Default Duration`() {
        val word = word(memoryDecayFactor = 2f)
        val answer = wrong(consumedDuration = defaultAnswerDuration)

        val baselineChange = wrongAnswerBaseline
        val extraChange = 0f
        val decayFactorChange = baselineChange - extraChange
        val expectedDecay = word.memoryDecayFactor * decayFactorChange
        val actualDecay = dataSource.calculateDecayOf(word, answer)
        assertEquals(expectedDecay, actualDecay, 0.001f)
    }

    @Test
    fun `calculateDecayOf - Wrong - Between Default and Max Duration`() {
        val word = word(memoryDecayFactor = 2f)
        val f = 0.5f
        val extraTime = maxAnswerDuration - defaultAnswerDuration
        val answer = wrong(consumedDuration = defaultAnswerDuration + extraTime.times(f.toDouble()))

        val baselineChange = 1 + wrongAnswerBaseline.minus(1).times(f)
        val extraChange = 0f
        val decayFactorChange = baselineChange - extraChange
        val expectedDecay = word.memoryDecayFactor * decayFactorChange
        val actualDecay = dataSource.calculateDecayOf(word, answer)
        assertEquals(expectedDecay, actualDecay, 0.001f)
    }

    @Test
    fun `calculateDecayOf - Wrong - At Max Duration`() {
        val word = word(memoryDecayFactor = 2f)
        val answer = wrong(consumedDuration = maxAnswerDuration)

        val baselineChange = 1f
        val extraChange = 0f
        val decayFactorChange = baselineChange - extraChange
        val expectedDecay = word.memoryDecayFactor * decayFactorChange
        val actualDecay = dataSource.calculateDecayOf(word, answer)
        assertEquals(expectedDecay, actualDecay, 0.001f)
    }

    private fun word(
        id: Long = 0,
        meaning: String = "",
        translation: String = "",
        additionalTranslations: List<String> = emptyList(),
        language: Language = Language("en".code, "English", "English"),
        tags: Set<String> = emptySet(),
        transcription: String = INVALID_TEXT,
        examples: List<String> = emptyList(),
        wordTypeTag: WordTypeTag? = null,
        relatedWords: List<RelatedWord> = emptyList(),
        memoryDecayFactor: Float = 1f,
        lastTrainTime: Instant? = null,
        createdAt: Instant = Instant.fromEpochSeconds(1000),
        updatedAt: Instant = Instant.fromEpochSeconds(1000),
    ): Word = Word(
        id = id,
        meaning = meaning,
        translation = translation,
        additionalTranslations = additionalTranslations,
        language = language,
        tags = tags,
        transcription = transcription,
        examples = examples,
        wordTypeTag = wordTypeTag,
        relatedWords = relatedWords,
        memoryDecayFactor = memoryDecayFactor,
        lastTrainTime = lastTrainTime,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun wrong(
        selectedAnswer: String = "",
        correctAnswer: String = "",
        consumedDuration: Duration = 0.seconds,
    ): MDTrainWordResult.Wrong = MDTrainWordResult.Wrong(
        selectedAnswer = selectedAnswer,
        correctAnswer = correctAnswer,
        consumedDuration = consumedDuration
    )

    private fun right(
        consumedDuration: Duration = 0.seconds,
        correctAnswer: String = "",
    ): MDTrainWordResult.Right = MDTrainWordResult.Right(
        consumedDuration = consumedDuration,
        correctAnswer = correctAnswer
    )

    private fun pass(
        consumedDuration: Duration = 0.seconds,
    ): MDTrainWordResult.Pass = MDTrainWordResult.Pass(
        consumedDuration = consumedDuration
    )

    private fun timeout(
        consumedDuration: Duration = 0.seconds,
    ): MDTrainWordResult.Timeout = MDTrainWordResult.Timeout(
        consumedDuration = consumedDuration
    )
}