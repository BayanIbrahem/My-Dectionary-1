package dev.bayan_ibrahim.my_dictionary.domain.model.file

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

/**
 * this strategy is used when both old and new values are not null, if any of them is null (or invalid like a blank string or list) then
 * it would try to get other value if also not null
 */
enum class MDPropertyConflictStrategy(@StringRes val labelRes: Int) : LabeledEnum {
    /**
     * abort all transaction if there is a conflict in words
     */
    AbortTransaction(R.string.abort_all),

    /**
     * ignore only the new word if old word exists
     */
    IgnoreWord(R.string.ignore_word),

    /**
     * firstNotNullOfOrNull(old, new)
     */
    IgnoreProperty(R.string.ignore_new_value),

    /**
     * firstNotNullOfOrNull(new, old)
     */
    Override(R.string.replace_old_value),

    /**
     * firstNotNullOfOrNull(old + new, old, new)
     */
    MergeOrIgnore(R.string.merge_or_ignore),

    /**
     * firstNotNullOfOrNull(old + new, new, old)
     */
    MergeOrOverride(R.string.merge_or_ignore);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(labelRes)
}

/**
 * this strategy is used when trying to get new word value but it appears that it is corrupted
 */
enum class MDPropertyCorruptionStrategy(@StringRes val labelRes: Int) : LabeledEnum {
    /**
     * abort all transaction if there is a conflict in words
     */
    AbortTransaction(R.string.abort_all),

    /**
     * ignore only the new word if old word exists
     */
    IgnoreWord(R.string.ignore_word),

    /**
     * just ignore the new property
     */
    IgnoreProperty(R.string.ignore_new_value);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(labelRes)
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

enum class MDExtraTagsStrategy(@StringRes val labelRes: Int) : LabeledEnum {
    All(R.string.all_x),
    New(R.string.new_x),
    Updated(R.string.updated_x);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = firstCapStringResource(labelRes, firstCapStringResource(R.string.words))
}