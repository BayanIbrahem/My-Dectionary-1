package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part

import dev.bayan_ibrahim.my_dictionary.core.util.invalidIfNull
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.applyMergable
import dev.bayan_ibrahim.my_dictionary.domain.model.file.applyNonMergable
import dev.bayan_ibrahim.my_dictionary.domain.model.file.validateWith
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ParentedTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import kotlinx.datetime.Clock

// Word
/**
 * validate with database source and return a model but with related words with invalid ids
 */
inline fun Word.validateWithDatabase(
    /**
     * pass a word to be the first data source for fields here, used if it is not wanted to override all fields in the word
     */
    dbWord: Word? = null,
    conflictStrategy: MDPropertyConflictStrategy,
    corruptedStrategy: MDPropertyCorruptionStrategy,
    getDBTag: (data: ParentedTag) -> ParentedTag,
    getDBWordClass: (data: WordClass) -> WordClass?,
): Word {
    return Word(
        id = dbWord?.id.invalidIfNull(),
        meaning = conflictStrategy.applyString(dbWord?.meaning) { this.meaning.validateWith(corruptedStrategy) },
        translation = conflictStrategy.applyString(dbWord?.translation) { this.translation.validateWith(corruptedStrategy) },
        additionalTranslations = conflictStrategy.applyComputedCollection(dbWord?.additionalTranslations) {
            this.additionalTranslations
        }.toList(),
        language = this.language,
        tags = conflictStrategy.applyComputedCollection(dbWord?.tags) { this.tags.map(getDBTag) }.toSet(),
        transcription = conflictStrategy.applyString(dbWord?.transcription) { this.transcription },
        examples = conflictStrategy.applyComputedCollection(dbWord?.examples) { this.examples }.toList(),
        wordClass = conflictStrategy.applyComputedOld(dbWord?.wordClass) { this.wordClass?.let { getDBWordClass(it) } },
        relatedWords = conflictStrategy.applyComputedCollection(dbWord?.relatedWords) {
            this.relatedWords
        }.toList(),
        // no related words
        lexicalRelations = conflictStrategy.applyMergable(
            oldData = {
                dbWord?.lexicalRelations
            },
            newData = {
                this.lexicalRelations
            },
            merge = { map1, map2 ->
                WordLexicalRelationType.entries.associateWith {
                    (map1[it] ?: emptyList()) + (map2[it] ?: emptyList())
                }
            }
        ) ?: emptyMap(),
        createdAt = dbWord?.createdAt ?: Clock.System.now(),
        updatedAt = Clock.System.now()
    )
}

fun ParentedTag.validateWithDatabase(
    dbTag: ParentedTag? = null,
    conflictStrategy: MDPropertyConflictStrategy,
    corruptedStrategy: MDPropertyCorruptionStrategy,
): ParentedTag {
    return ParentedTag(
        id = dbTag?.id.invalidIfNull(),
        label = conflictStrategy.applyString(dbTag?.label) { this.label.validateWith(corruptedStrategy) },
        color = conflictStrategy.applyComputedOld(dbTag?.color) { this.color },
        parentId = conflictStrategy.applyComputedOld(dbTag?.parentId) { this.parentId },
        passColor = conflictStrategy.applyComputedOld(dbTag?.passColor) { this.passColor } == true,
    )
}

/** just take the old value as an object cause it is already computed */
inline fun <T : Any> MDPropertyConflictStrategy.applyComputedOld(old: T?, new: () -> T?): T? = applyNonMergable(
    oldData = { old },
    newData = new
)

inline fun MDPropertyConflictStrategy.applyString(
    old: String?,
    new: () -> String?,
): String = applyComputedOld(
    old = old?.nullIfInvalid()
) {
    new()?.nullIfInvalid()
} ?: ""

inline fun <T : Any> MDPropertyConflictStrategy.applyCollection(
    old: () -> Collection<T>?,
    new: () -> Collection<T>?,
): Collection<T> = applyMergable(
    oldData = { old()?.takeUnless { it.isEmpty() } },
    newData = {
        new()?.takeUnless { it.isEmpty() }
    },
    merge = { i, j ->
        (i + j).distinct()
    }
) ?: emptyList()

inline fun <T : Any> MDPropertyConflictStrategy.applyComputedCollection(
    old: Collection<T>?,
    new: () -> Collection<T>?,
): Collection<T> = applyCollection({ old }, new)

fun String.validateWith(strategy: MDPropertyCorruptionStrategy): String? = this.validateWith(strategy) {
    this.isNotBlank()
}
