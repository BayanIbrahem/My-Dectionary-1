package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part

import androidx.compose.ui.graphics.Color
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDEditableField
import dev.bayan_ibrahim.my_dictionary.core.util.invalidIfNull
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.applyMergable
import dev.bayan_ibrahim.my_dictionary.domain.model.file.applyNonMergable
import dev.bayan_ibrahim.my_dictionary.domain.model.file.validateWith
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceMutableState
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_space.component.word_space_list_item.LanguageWordSpaceState
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.strHex
import kotlinx.datetime.Clock

// language
fun MDFileLanguagePart.toLanguage(): Language = this.code.code.getLanguage()
fun MDFileLanguagePart.toLanguageWordSpaceState(): LanguageWordSpaceState {
    val language = this.toLanguage()
    return LanguageWordSpaceMutableState(
        code = this.code,
        initialTags = this.typeTags.map { typeTag ->
            MDEditableField.of(
                WordTypeTag(
                    id = typeTag.id.invalidIfNull(),
                    name = typeTag.name,
                    language = language,
                    relations = typeTag.relations.map { relation ->
                        WordTypeTagRelation(
                            id = relation.id.invalidIfNull(),
                            label = relation.name,
                            wordsCount = 0
                        )
                    },
                    wordsCount = 0,
                )
            )
        },
    )
}

// context tag:
fun MDFileTagPart.toContextTag() = ContextTag(
    value = this.name,
    id = this.id.invalidIfNull(),
    color = this.color?.let { Color.strHex(it) },
    passColorToChildren = this.passColorToChildren,
)

// Word
/**
 * return an empty related word
 */
inline fun MDFileWordPart.toWord(
    /**
     * pass a word to be the first data source for fields here, used if it is not wanted to override all fields in the word
     */
    dbWordSource: Word? = null,
    dbWordStrategy: MDPropertyConflictStrategy,
    corruptedStrategy: MDPropertyCorruptionStrategy,
    getContextTagOfFilePart: (MDFileTagPart) -> ContextTag,
    getTypeTag: (MDNameWithOptionalId) -> WordTypeTag?,
): Word {
    return Word(
        id = dbWordSource?.id.invalidIfNull(),
        meaning = dbWordStrategy.applyString(dbWordSource?.meaning) { this.meaning.validateWith(corruptedStrategy) },
        translation = dbWordStrategy.applyString(dbWordSource?.translation) { this.translation.validateWith(corruptedStrategy) },
        additionalTranslations = dbWordStrategy.applyComputedCollection(dbWordSource?.additionalTranslations) { this.additionalTranslations }
            .toList(),
        language = this.language.code.getLanguage(),
        tags = dbWordStrategy.applyComputedCollection(dbWordSource?.tags) { this.tags.map(getContextTagOfFilePart) }.toSet(),
        transcription = dbWordStrategy.applyString(dbWordSource?.transcription) { this.transcription },
        examples = dbWordStrategy.applyComputedCollection(dbWordSource?.examples) { this.examples }.toList(),
        wordTypeTag = dbWordStrategy.applyComputedOld(dbWordSource?.wordTypeTag) { this.typeTag?.let { getTypeTag(it) } },
        // no related words
        createdAt = dbWordSource?.createdAt ?: Clock.System.now(),
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
