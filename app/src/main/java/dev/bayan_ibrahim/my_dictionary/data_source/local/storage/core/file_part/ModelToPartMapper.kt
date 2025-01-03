package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part

import dev.bayan_ibrahim.my_dictionary.core.util.invalidIfNull
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.applyMergable
import dev.bayan_ibrahim.my_dictionary.domain.model.file.applyNonMergable
import dev.bayan_ibrahim.my_dictionary.domain.model.file.validateWith
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
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
    dbWordStrategy: MDPropertyConflictStrategy,
    corruptedStrategy: MDPropertyCorruptionStrategy,
    getDBContextTag: (data: ContextTag) -> ContextTag,
    getDBTypeTag: (data: WordTypeTag) -> WordTypeTag?,
): Word {
    return Word(
        id = dbWord?.id.invalidIfNull(),
        meaning = dbWordStrategy.applyString(dbWord?.meaning) { this.meaning.validateWith(corruptedStrategy) },
        translation = dbWordStrategy.applyString(dbWord?.translation) { this.translation.validateWith(corruptedStrategy) },
        additionalTranslations = dbWordStrategy.applyComputedCollection(dbWord?.additionalTranslations) {
            this.additionalTranslations
        }.toList(),
        language = this.language,
        tags = dbWordStrategy.applyComputedCollection(dbWord?.tags) { this.tags.map(getDBContextTag) }.toSet(),
        transcription = dbWordStrategy.applyString(dbWord?.transcription) { this.transcription },
        examples = dbWordStrategy.applyComputedCollection(dbWord?.examples) { this.examples }.toList(),
        wordTypeTag = dbWordStrategy.applyComputedOld(dbWord?.wordTypeTag) { this.wordTypeTag?.let { getDBTypeTag(it) } },
        relatedWords = dbWordStrategy.applyComputedCollection(dbWord?.relatedWords) {
            this.relatedWords
        }.toList(),
        // no related words
        createdAt = dbWord?.createdAt ?: Clock.System.now(),
        updatedAt = Clock.System.now()
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
