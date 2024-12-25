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
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface WordDetailsUiState : MDUiState {
    val isEditModeOn: Boolean
    val valid: Boolean
    val id: Long
    val language: Language
    val meaning: String
    val transcription: String
    val translation: String
    val additionalTranslations: Map<Long, String>
    val typeTags: List<WordTypeTag>
    val selectedTypeTag: WordTypeTag?
    val relatedWords: Map<Long, Pair<WordTypeTagRelation, String>> // Map<Label, List<Word>>, for each relation it may have more than one value
    val examples: Map<Long, String>
    val memorizingProbability: Float
    val createdAt: Instant?

    // todo add some statistics
}

class WordDetailsMutableUiState : WordDetailsUiState, MDMutableUiState() {
    override var isEditModeOn: Boolean by mutableStateOf(true)
    override var id: Long by mutableLongStateOf(INVALID_ID)
    override var createdAt: Instant? by mutableStateOf(null)
    override var language: Language by mutableStateOf(INVALID_LANGUAGE)
    override var meaning: String by mutableStateOf(INVALID_TEXT)
    override var transcription: String by mutableStateOf(INVALID_TEXT)
    override var translation: String by mutableStateOf(INVALID_TEXT)
    override val additionalTranslations: SnapshotStateMap<Long, String> = mutableStateMapOf()
    override val typeTags: SnapshotStateList<WordTypeTag> = mutableStateListOf()
    override var selectedTypeTag: WordTypeTag? by mutableStateOf(null)
    override val relatedWords: SnapshotStateMap<Long, Pair<WordTypeTagRelation, String>> = mutableStateMapOf()
    override val examples: SnapshotStateMap<Long, String> = mutableStateMapOf()
    override var memorizingProbability: Float by mutableFloatStateOf(0f)
        private set

    private val idGenerator = IncrementalIdGenerator()

    override val valid: Boolean
        get() = validateWord()

    fun validateWord(): Boolean {
        return meaning.isNotBlank() && translation.isNotBlank()
    }

    fun addAdditionalTranslation(value: String) {
        additionalTranslations[idGenerator.nextId()] = value
    }

    fun addRelatedWord(relation: WordTypeTagRelation, value: String) {
        relatedWords[idGenerator.nextId()] = relation to value
    }

    fun addExample(value: String) {
        examples[idGenerator.nextId()] = value
    }

    fun ensureOnTrailingBlankItemAdditionalTranslation() = additionalTranslations.ensureOneTrailingBlankItem()

    fun ensureOnTrailingBlankItemExample() = examples.ensureOneTrailingBlankItem()

    fun ensureOnTrailingBlankItemRelatedWord() {
        val selectedTypeRelations = this.selectedTypeTag?.relations?.toSet()
        if (selectedTypeRelations.isNullOrEmpty()) {
            this.relatedWords.clear()
        } else {
            this.relatedWords.run {
                val maxId = maxOfOrNull { it.key } // latest value
                val blankValues = filterValues { (r, w) ->
                    r.label.isBlank() || w.isBlank()
                }
                blankValues.forEach { (id, _) ->
                    if (id != maxId) {
                        remove(id)
                    }
                }
                if (this[maxId]?.let { it.first !in selectedTypeRelations || it.second.isBlank() } != false) {
                    this[idGenerator.nextId()] = selectedTypeRelations.first() to INVALID_TEXT
                }
            }
        }
    }

    private fun SnapshotStateMap<Long, String>.ensureOneTrailingBlankItem() {
        val maxId = maxOfOrNull { it.key } // latest value
        val blankValues = filterValues { it.isBlank() }
        blankValues.forEach { (id, _) ->
            if (id != maxId) {
                remove(id)
            }
        }
        if (this[maxId]?.isNotBlank() != false) {
            this[idGenerator.nextId()] = INVALID_TEXT
        }
    }

    fun loadWord(word: Word) {
        id = word.id
        createdAt = word.createdAt
        meaning = word.meaning
        language = word.language
        transcription = word.transcription
        translation = word.translation
        additionalTranslations.setAll(word.additionalTranslations.associateBy { idGenerator.nextId() })
        selectedTypeTag = word.wordTypeTag
        selectedTypeTag?.relations?.associateBy { it.id }?.let { relations ->
            relatedWords.setAll(
                word.relatedWords.associate {
                    idGenerator.nextId() to (relations[it.relationId]!! to it.value)
                }
            )
        }
        examples.setAll(word.examples.associateBy { idGenerator.nextId() })
        memorizingProbability = word.memoryDecayFactor
    }

    fun toWord(
        contextTags: Set<ContextTag>,
    ): Word {
        val now = Clock.System.now()
        return Word(
            id = this.id,
            meaning = this.meaning,
            language = this.language,
            translation = this.translation,
            transcription = this.transcription,
            additionalTranslations = this.additionalTranslations.values.mapNotNull { it.ifBlank { null } }.distinct(),
            wordTypeTag = this.selectedTypeTag,
            relatedWords = this.relatedWords.mapNotNull { (_, word) ->
                if (word.second.isBlank()) {
                    null
                } else {
                    RelatedWord(
                        id = INVALID_ID,
                        baseWordId = this.id,
                        relationLabel = word.first.label,
                        value = word.second,
                        relationId = word.first.id,
                    )
                }
            }.distinctBy {
                Pair(it.relationId, it.value)
            },
            tags = contextTags,
            examples = this.examples.values.toSet().mapNotNull { it.ifBlank { null } },
            memoryDecayFactor = this.memorizingProbability,
            createdAt = this.createdAt ?: now,
            updatedAt = now
        )
    }
}