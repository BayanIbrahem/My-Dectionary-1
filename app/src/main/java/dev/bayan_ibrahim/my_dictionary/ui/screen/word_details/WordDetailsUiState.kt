package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.IncrementalIdGenerator
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDMutableUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDUiState
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_LANGUAGE
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.RelatedWord
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.Word
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.Language

interface WordDetailsUiState : MDUiState {
    val isEditModeOn: Boolean
    val id: Long
    val language: Language
    val meaning: String
    val transcription: String
    val translation: String
    val additionalTranslations: Map<Long, String>
    val tags: Map<Long, String>
    val typeTags: List<WordTypeTag>
    val selectedTypeTag: WordTypeTag?
    val relatedWords: Map<Long, Pair<String, String>> // Map<Label, List<Word>>, for each relation it may have more than one value
    val examples: Map<Long, String>
    val learningProgress: Float
    // todo add some statistics
}

class WordDetailsMutableUiState : WordDetailsUiState, MDMutableUiState() {
    override var isEditModeOn: Boolean by mutableStateOf(true)
    override var id: Long by mutableLongStateOf(INVALID_ID)
    override var language: Language by mutableStateOf(INVALID_LANGUAGE)
    override var meaning: String by mutableStateOf(INVALID_TEXT)
    override var transcription: String by mutableStateOf(INVALID_TEXT)
    override var translation: String by mutableStateOf(INVALID_TEXT)
    override val additionalTranslations: SnapshotStateMap<Long, String> = mutableStateMapOf()
    override val tags: SnapshotStateMap<Long, String> = mutableStateMapOf()
    override val typeTags: SnapshotStateList<WordTypeTag> = mutableStateListOf()
    override var selectedTypeTag: WordTypeTag? by mutableStateOf(null)
    override val relatedWords: SnapshotStateMap<Long, Pair<String, String>> = mutableStateMapOf()
    override val examples: SnapshotStateMap<Long, String> = mutableStateMapOf()
    override var learningProgress: Float by mutableFloatStateOf(0f)
        private set

    private val translationsIdsGenerator = IncrementalIdGenerator()
    private val tagsIdsGenerator = IncrementalIdGenerator()
    private val relatedWordsIdsGenerator = IncrementalIdGenerator()
    private val examplesIdsGenerator = IncrementalIdGenerator()

    fun addAdditionalTranslation(value: String) {
        additionalTranslations[translationsIdsGenerator.nextId()] = value
    }

    fun addTag(value: String) {
        tags[tagsIdsGenerator.nextId()] = value
    }

    fun addRelatedWord(relation: String, value: String) {
        relatedWords[relatedWordsIdsGenerator.nextId()] = relation to value
    }

    fun addExample(value: String) {
        examples[examplesIdsGenerator.nextId()] = value
    }

    fun loadWord(word: Word) {
        id = word.id
        meaning = word.meaning
        language = word.language
        transcription = word.transcription
        translation = word.translation
        additionalTranslations.setAll(word.additionalTranslations.associateBy { translationsIdsGenerator.nextId() })
        tags.setAll(word.tags.associateBy { tagsIdsGenerator.nextId() })
        selectedTypeTag = word.wordTypeTag
        relatedWords.setAll(
            word.relatedWords.associate {
                relatedWordsIdsGenerator.nextId() to (it.relationLabel to it.value)
            }
        )
        examples.setAll(word.examples.associateBy { examplesIdsGenerator.nextId() })
        learningProgress = word.learningProgress
    }

    fun toWord(): Word = Word(
        id = this.id,
        meaning = this.meaning,
        language = this.language,
        translation = this.translation,
        transcription = this.transcription,
        additionalTranslations = this.additionalTranslations.values.toList(),
        tags = this.tags.values.toList(),
        wordTypeTag = this.selectedTypeTag,
        relatedWords = this.relatedWords.map { (_, word) ->
            RelatedWord(
                id = INVALID_ID,
                baseWordId = this.id,
                relationLabel = word.first,
                value = word.second
            )
        },
        examples = this.examples.values.toList(),
        learningProgress = this.learningProgress
    )
}