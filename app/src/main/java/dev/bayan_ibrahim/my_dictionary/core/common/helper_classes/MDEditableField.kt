package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes

import androidx.compose.runtime.Immutable

@Immutable
data class MDEditableField<T: Any> private constructor(
    val current: T,
    val original: T?,
) {
    val status: MDEditableFieldStatus = if (original == null) {
        MDEditableFieldStatus.NEW
    } else if (current != original) {
        MDEditableFieldStatus.EDITED
    } else {
        MDEditableFieldStatus.NOT_CHANGED
    }

    fun edit(currentValueScope: T.() -> T): MDEditableField<T> = copy(
        current = current.currentValueScope()
    )

    fun reset(): MDEditableField<T>  = resetOrNull() ?: throw IllegalArgumentException("Invalid null value when trying to reset value")

    fun resetOrNull(): MDEditableField<T>? = original?.let {
        copy(
            current = it,
            original = original
        )
    }

    fun confirm(): MDEditableField<T> = copy(original = current)

    companion object Companion {
        /**
         * - [current] would be [value]
         * - [original] would be null
         */
        fun <T: Any> new(value: T): MDEditableField<T> = MDEditableField(value, null)
        /**
         * - [current] would be [value]
         * - [original] would be [value]
         */
        fun <T: Any> of(value: T): MDEditableField<T> = MDEditableField(value, value)
    }
}

fun <T: Any> T.toMDEditableField(new: Boolean): MDEditableField<T> = if (new) {
    MDEditableField.new(this)
} else {
    MDEditableField.of(this)
}
