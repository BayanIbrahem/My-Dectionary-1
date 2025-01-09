package dev.bayan_ibrahim.my_dictionary.domain.model.file

import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

/**
 * this strategy is used when both old and new values are not null, if any of them is null (or invalid like a blank string or list) then
 * it would try to get other value if also not null
 */
enum class MDPropertyConflictStrategy(override val strLabel: String) : LabeledEnum {
    /**
     * abort all transaction if there is a conflict in words
     */
    AbortTransaction("Abort All"),

    /**
     * ignore only the new word if old word exists
     */
    IgnoreWord("Ignore Word"),

    /**
     * firstNotNullOfOrNull(old, new)
     */
    IgnoreProperty("Ignore new value"),

    /**
     * firstNotNullOfOrNull(new, old)
     */
    Override("Override old value"),

    /**
     * firstNotNullOfOrNull(old + new, old, new)
     */
    MergeOrIgnore("Merge if possible or ignore new"),

    /**
     * firstNotNullOfOrNull(old + new, new, old)
     */
    MergeOrOverride("Merge if possible or override old");
}

/**
 * this strategy is used when trying to get new word value but it appears that it is corrupted
 */
enum class MDPropertyCorruptionStrategy(override val strLabel: String) : LabeledEnum {
    /**
     * abort all transaction if there is a conflict in words
     */
    AbortTransaction("Abort All"),

    /**
     * ignore only the new word if old word exists
     */
    IgnoreWord("Ignore Word"),

    /**
     * just ignore the new property
     */
    IgnoreProperty("Ignore new value"),
}

sealed class MDPropertyConflictException(
    override val message: String,
) : IllegalArgumentException() {
    data object AbortTransaction : MDPropertyConflictException("Property Conflict Strategy, abort transaction") {
        private fun readResolve(): Any = AbortTransaction
    }

    data object AbortWord : MDPropertyConflictException("Property Conflict Strategy, abort word") {
        private fun readResolve(): Any = AbortWord
    }
}

sealed class MDPropertyCorruptionException(
    override val message: String,
) : IllegalArgumentException() {
    data object AbortTransaction : MDPropertyCorruptionException("Property Corruption Strategy, abort transaction") {
        private fun readResolve(): Any = AbortTransaction
    }

    data object AbortWord : MDPropertyCorruptionException("Property Corruption Strategy, abort word") {
        private fun readResolve(): Any = AbortWord
    }
}

/**
 * @throws MDPropertyConflictException
 */
inline fun <T : Any> MDPropertyConflictStrategy.applyMergable(
    oldData: () -> T?,
    newData: () -> T?,
    merge: (old: T, new: T) -> T,
): T? = when (this) {
    MDPropertyConflictStrategy.AbortTransaction -> throw MDPropertyConflictException.AbortTransaction
    MDPropertyConflictStrategy.IgnoreWord -> throw MDPropertyConflictException.AbortWord
    MDPropertyConflictStrategy.IgnoreProperty -> oldData() ?: newData()
    MDPropertyConflictStrategy.Override -> newData() ?: oldData()
    MDPropertyConflictStrategy.MergeOrIgnore -> oldData()?.let { old ->
        newData()?.let { new ->
            merge(old, new)
        } ?: old
    } ?: newData()

    MDPropertyConflictStrategy.MergeOrOverride -> newData()?.let { new ->
        oldData()?.let { old ->
            merge(old, new)
        } ?: new
    } ?: oldData()
}

inline fun <T : Any> MDPropertyConflictStrategy.applyNonMergable(
    oldData: () -> T?,
    newData: () -> T?,
): T? = when (this) {
    MDPropertyConflictStrategy.AbortTransaction -> throw MDPropertyConflictException.AbortTransaction
    MDPropertyConflictStrategy.IgnoreWord -> throw MDPropertyConflictException.AbortWord
    MDPropertyConflictStrategy.IgnoreProperty, MDPropertyConflictStrategy.MergeOrIgnore -> oldData() ?: newData()
    MDPropertyConflictStrategy.Override, MDPropertyConflictStrategy.MergeOrOverride -> newData() ?: oldData()
}

inline fun <T : Any> T.validateWith(
    corruptionStrategy: MDPropertyCorruptionStrategy,
    isValid: (T) -> Boolean,
): T? {
    return if (isValid(this)) {
        this
    } else {
        when (corruptionStrategy) {
            MDPropertyCorruptionStrategy.AbortTransaction -> throw MDPropertyCorruptionException.AbortTransaction
            MDPropertyCorruptionStrategy.IgnoreWord -> throw MDPropertyCorruptionException.AbortWord
            MDPropertyCorruptionStrategy.IgnoreProperty -> null
        }
    }
}

enum class MDExtraTagsStrategy(override val strLabel: String): LabeledEnum {
    All("All Words"),
    New("New Words"),
    Updated("Updated Words"),
}