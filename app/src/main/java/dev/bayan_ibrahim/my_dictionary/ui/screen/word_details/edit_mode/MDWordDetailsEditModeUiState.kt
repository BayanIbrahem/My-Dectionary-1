package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import kotlinx.coroutines.flow.StateFlow


interface MDWordDetailsEditModeUiState : MDUiState {
    val valid: Boolean
    val id: Long

    // basic:
    val meaning: String
    val translation: String
    val note: String
    val language: Language

    // phonetic:
    val transcription: String

    // additional:
    val tags: List<Tag>
    val additionalTranslations: Map<Long, String>
    val examples: Map<Long, String>

    // type relations:
    val selectedWordClass: WordClass?
    val availableWordsClasses: StateFlow<List<WordClass>>
    val relatedWords: Map<Long, Pair<WordClassRelation, String>>

    // lexical relations:
    val lexicalRelations: Map<WordLexicalRelationType, Map<Long, String>>
}

class MDWordDetailsEditModeMutableUiState(
    override val tags: SnapshotStateList<Tag>,
    override val availableWordsClasses: StateFlow<List<WordClass>>,
) : MDWordDetailsEditModeUiState, MDMutableUiState() {
    private val idGenerator = IncrementalIdGenerator()
    override val valid: Boolean
        get() = validateWord()

    private fun validateWord(): Boolean {
        return meaning.isNotBlank() && translation.isNotBlank()
    }

    override var id: Long by mutableLongStateOf(INVALID_ID)

    // basic:
    override var meaning: String by mutableStateOf(INVALID_TEXT)
    override var translation: String by mutableStateOf(INVALID_TEXT)
    override var note: String by mutableStateOf(INVALID_TEXT)
    override var language: Language by mutableStateOf(INVALID_LANGUAGE)

    // additional:
    override var transcription: String by mutableStateOf(INVALID_TEXT)
    override val additionalTranslations: SnapshotStateMap<Long, String> = mutableStateMapOf()
    override val examples: SnapshotStateMap<Long, String> = mutableStateMapOf()

    // type relations:
    override var selectedWordClass: WordClass? by mutableStateOf(null)
    override val relatedWords: SnapshotStateMap<Long, Pair<WordClassRelation, String>> = mutableStateMapOf()

    // lexical relations:
    override val lexicalRelations: SnapshotStateMap<WordLexicalRelationType, SnapshotStateMap<Long, String>> = mutableStateMapOf()

    fun loadWord(word: Word) {
        onExecute {
            idGenerator.reset()

            id = word.id

            // basic:
            meaning = word.meaning
            translation = word.translation
            note = word.note
            language = word.language

            // additional:
            transcription = word.transcription
            additionalTranslations.setAll(word.additionalTranslations.associateByIdGenerator())
            examples.setAll(word.examples.associateByIdGenerator())

            // type relations:
            selectedWordClass = word.wordClass
            val newRelatedWords = selectedWordClass?.relations?.ifEmpty {
                null
            }?.associateBy {
                it.id
            }?.let { typeRelations ->
                word.relatedWords.mapNotNull { related ->
                    typeRelations[related.relationId]?.let { relation ->
                        relation to related.value
                    }
                }.associateByIdGenerator()
            } ?: emptyMap()
            relatedWords.setAll(newRelatedWords)

            // lexical relations:
            val newLexicalRelations = word.lexicalRelations.mapValues { (_, relations) ->
                val values = relations.associateByIdGenerator { it.relatedWord }
                mutableStateMapOf<Long, String>().apply {
                    putAll(values)
                }
            }
            lexicalRelations.setAll(newLexicalRelations)
            // append not existed lexical relations:
            WordLexicalRelationType.entries.forEach {
                lexicalRelations.putIfAbsent(it, mutableStateMapOf())
            }

            validateWord()
        }
    }

    fun reset() {
        onExecute {
            id = INVALID_ID

            // basic:
            meaning = INVALID_TEXT
            translation = INVALID_TEXT
            language = INVALID_LANGUAGE

            // additional:
            transcription = INVALID_TEXT
            additionalTranslations.clear()
            examples.clear()

            // type relations:
            selectedWordClass = null
            relatedWords.clear()

            // lexical relations:
            lexicalRelations.clear()

            // append not existed lexical relations:
            lexicalRelations.setAll(WordLexicalRelationType.entries.associateWith { mutableStateMapOf() })

            true
        }
    }

    private fun <T> MutableMap<Long, T>.add(value: T) {
        this[idGenerator.nextId()] = value
    }


    // ensure blank trailing field: 👉
    fun ensureBlankTrailingField() {
        ensureBlankAdditionalTranslationsTrailingField()
        ensureBlankExamplesTrailingField()
        ensureBlankTypeRelationsTrailingField()
        ensureBlankLexicalRelationsTrailingField()
    }

    fun ensureBlankAdditionalTranslationsTrailingField() {
        additionalTranslations.ensureBlankTrailingField()
    }

    fun ensureBlankExamplesTrailingField() {
        examples.ensureBlankTrailingField()
    }

    fun ensureBlankTypeRelationsTrailingField() {
        val availableRelations = selectedWordClass?.relations?.ifEmpty { null }

        if (availableRelations == null) {
            relatedWords.clear()
        } else {
            relatedWords.ensureBlankTrailingField(
                isNotBlank = { (_, value) ->
                    value.isNotBlank()
                },
                invalidValue = {
                    Pair(availableRelations.first(), INVALID_TEXT)
                }
            )
        }
    }

    fun ensureBlankLexicalRelationsTrailingField() {
        lexicalRelations.toList().forEach { (_, relations) ->
            relations.ensureBlankTrailingField()
        }
    }

    private fun MutableMap<Long, String>.ensureBlankTrailingField() = ensureBlankTrailingField(
        isNotBlank = { it.isNotBlank() },
        invalidValue = { INVALID_TEXT }
    )

    private fun <T> MutableMap<Long, T>.ensureBlankTrailingField(isNotBlank: (T) -> Boolean, invalidValue: () -> T) {
        val lastItem = keys.maxOrNull()?.let { this[it] }
        if (lastItem == null || isNotBlank(lastItem)) {
            this.add(invalidValue())
        }
    }
    // ensure blank trailing field: 👈

    // filter blank fields: 👉
    fun filterBlankFields(focusedFieldId: Long? = null) {
        filterBlankAdditionalTranslationsFields(focusedFieldId)
        filterBlankExamplesFields(focusedFieldId)
        filterBlankRelatedWordsFields(focusedFieldId)
        filterBlankLexicalRelationsFields(focusedFieldId)
    }

    fun filterBlankLexicalRelationsFields(focusedFieldId: Long? = null) {
        lexicalRelations.toList().forEach { (_, relations) ->
            relations.filterBlankFields(focusedFieldId)
        }
    }

    fun filterBlankRelatedWordsFields(focusedFieldId: Long? = null) {
        relatedWords.filterBlankFields(focusedFieldId) { it.second.isBlank() }
    }

    fun filterBlankExamplesFields(focusedFieldId: Long? = null) {
        examples.filterBlankFields(focusedFieldId)
    }

    fun filterBlankAdditionalTranslationsFields(focusedFieldId: Long? = null) {
        additionalTranslations.filterBlankFields(focusedFieldId)
    }

    private fun MutableMap<Long, String>.filterBlankFields(focusedFieldId: Long? = null) = filterBlankFields(focusedFieldId) { it.isBlank() }
    private fun <T> MutableMap<Long, T>.filterBlankFields(
        focusedFieldId: Long? = null,
        isBlank: (T) -> Boolean,
    ) {
        toList().forEach { (id, value) ->
            if (id != focusedFieldId && isBlank(value)) {
                this.remove(id)
            }
        }
    }
    // filter blank fields: 👈

    private fun <T> List<T>.associateByIdGenerator(): Map<Long, T> = associateByIdGenerator { it }
    private inline fun <T, R> List<T>.associateByIdGenerator(mapper: (T) -> R): Map<Long, R> = associate { idGenerator.nextId() to mapper(it) }
}
