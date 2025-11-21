@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package dev.bayan_ibrahim.my_dictionary.domain.model.result

import dev.bayan_ibrahim.my_dictionary.domain.model.result.MDResult.Companion.failure
import dev.bayan_ibrahim.my_dictionary.domain.model.result.MDResult.Companion.success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.io.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * copy of [Result] but with loading status
 */
@SinceKotlin("1.3")
@JvmInline
value class MDResult<out T> @PublishedApi internal constructor(
    @PublishedApi
    internal val value: Any?,
) : Serializable {
    // discovery

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] and [isLoading] returns `false`.
     */
    val isSuccess: Boolean get() = value !is Failure && value !is Loading

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] and [isLoading] returns `false`.
     */
    val isFailure: Boolean get() = value is Failure

    /**
     * Returns `true` if this instance represents the loading process (outcome is not ready yet)
     * In this case [isSuccess] and [isLoading] returns `false`.
     */
    val isLoading: Boolean get() = value is Loading

    // value & exception retrieval

    /**
     * Returns the encapsulated value if this instance represents [success][MDResult.isSuccess] or `null`
     * if it is [failure][MDResult.isFailure].
     *
     * This function is a shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    inline fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    /**
     * Returns the encapsulated [Throwable] exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is a shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    /**
     * Returns a string `Success(v)` if this instance represents [success][MDResult.isSuccess]
     * where `v` is a string representation of the value or a string `Failure(x)` if
     * it is [failure][isFailure] where `x` is a string representation of the exception.
     */
    override fun toString(): String =
        when (value) {
            is Failure -> value.toString() // "Failure($exception)"
            is Loading -> "Loading"
            else -> "Success($value)"
        }

    // companion with constructors

    /**
     * Companion object for [MDResult] class that contains its constructor functions
     * [success] and [failure].
     */
    companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("success")
        inline fun <T> success(value: T): MDResult<T> =
            MDResult(value)

        /**
         * Returns an instance that encapsulates the given [Throwable] [exception] as failure.
         */
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("failure")
        inline fun <T> failure(exception: Throwable): MDResult<T> =
            MDResult(createFailure(exception))

        /**
         * Returns an instance that represent loading status of result
         */
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("failure")
        inline fun <T> loading(): MDResult<T> =
            MDResult(createLoading())
    }

    internal class Failure(
        @JvmField
        val exception: Throwable,
    ) : Serializable {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }

    internal class Loading : Serializable
}

/**
 * Creates an instance of internal marker [MDResult.Failure] class to
 * make sure that this class is not exposed in ABI.
 */
@PublishedApi
@SinceKotlin("1.3")
internal fun createFailure(exception: Throwable): Any =
    MDResult.Failure(exception)

@PublishedApi
@SinceKotlin("1.3")
internal fun createLoading(): Any =
    MDResult.Loading()

/**
 * Throws exception if the result is failure. This internal function minimizes
 * inlined bytecode for [getOrThrow] and makes sure that in the future we can
 * add some exception-augmenting logic here (if needed).
 */
@PublishedApi
@SinceKotlin("1.3")
internal fun MDResult<*>.throwOnFailure() {
    if (value is MDResult.Failure) throw value.exception
}

// -- extensions ---

/**
 * Returns the encapsulated value if this instance represents [success][MDResult.isSuccess] or throws the encapsulated [Throwable] exception
 * if it is [failure][MDResult.isFailure].
 *
 * This function is a shorthand for `getOrElse { throw it }` (see [getOrElse]).
 */
@SinceKotlin("1.3")
inline fun <T> MDResult<T>.getOrThrow(): T {
    throwOnFailure()
    return value as T
}

/**
 * Returns the encapsulated value if this instance represents [success][MDResult.isSuccess] or the
 * result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][MDResult.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onFailure] function.
 *
 * This function is a shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <R, T : R> MDResult<T>.getOrElse(onFailure: (exception: Throwable) -> R): R {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when (val exception = exceptionOrNull()) {
        null -> value as T
        else -> onFailure(exception)
    }
}

/**
 * Returns the encapsulated value if this instance represents [success][MDResult.isSuccess] or the
 * [defaultValue] if it is [failure][MDResult.isFailure].
 *
 * This function is a shorthand for `getOrElse { defaultValue }` (see [getOrElse]).
 */
@SinceKotlin("1.3")
inline fun <R, T : R> MDResult<T>.getOrDefault(defaultValue: R): R {
    if (isFailure) return defaultValue
    return value as T
}

/**
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [success][MDResult.isSuccess]
 * or the result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][MDResult.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onSuccess] or by [onFailure] function.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <R, T> MDResult<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R,
    onLoading: () -> R,
): R {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when {
        isLoading -> onLoading()
        isFailure -> onFailure(exceptionOrNull()!!)
        else -> onSuccess(value as T)
    }
}

// transformation

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][MDResult.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][MDResult.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [mapCatching] for an alternative that encapsulates exceptions.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <R, T> MDResult<T>.map(transform: (value: T) -> R): MDResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when {
        isSuccess -> MDResult.success(transform(value as T))
        else -> MDResult(value)
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][MDResult.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][MDResult.isFailure].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [map] for an alternative that rethrows exceptions from `transform` function.
 */
@SinceKotlin("1.3")
inline fun <R, T> MDResult<T>.mapCatching(transform: (value: T) -> R): Serializable {
    return when {
        isSuccess -> runCatching { transform(value as T) }
        else -> MDResult<T>(value)
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated [Throwable] exception
 * if this instance represents [failure][MDResult.isFailure] or the
 * original encapsulated value if it is [success][MDResult.isSuccess].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [recoverCatching] for an alternative that encapsulates exceptions.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <R, T : R> MDResult<T>.recover(transform: (exception: Throwable) -> R): MDResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> MDResult.success(transform(exception))
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated [Throwable] exception
 * if this instance represents [failure][MDResult.isFailure] or the
 * original encapsulated value if it is [success][MDResult.isSuccess].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [recover] for an alternative that rethrows exceptions.
 */
@SinceKotlin("1.3")
inline fun <R, T : R> MDResult<T>.recoverCatching(transform: (exception: Throwable) -> R): Serializable {
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> runCatching { transform(exception) }
    }
}

// "peek" onto value/exception and pipe

/**
 * Performs the given [action] on the encapsulated [Throwable] exception if this instance represents [failure][MDResult.isFailure].
 * Returns the original `Result` unchanged.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <T> MDResult<T>.onFailure(action: (exception: Throwable) -> Unit): MDResult<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    exceptionOrNull()?.let { action(it) }
    return this
}

/**
 * Performs the given [action] on the encapsulated value if this instance represents [success][MDResult.isSuccess].
 * Returns the original `Result` unchanged.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <T> MDResult<T>.onSuccess(action: (value: T) -> Unit): MDResult<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    if (isSuccess) action(value as T)
    return this
}


/**
 * Performs the given [action] on the encapsulated value if this instance represents [MDResult.isLoading].
 * Returns the original `Result` unchanged.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <T> MDResult<T>.onLoading(action: () -> Unit): MDResult<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    if (isLoading) action()
    return this
}
// -------------------

// mapping with kotlin.result


fun <T> Result<T>.asMDResult(): MDResult<T> = fold(
    onSuccess = { MDResult.success(it) },
    onFailure = { MDResult.failure(it) }
)

/**
 * convert [MDResult] to kotlin result
 * @param onLoading action when status is loading, default value is [Result.failure] with exception
 * [IllegalStateException] (Loading state not handled), if you want a null result value on [MDResult.onLoading]
 * see [asResult]
 */
fun <T> MDResult<T>.asResult(
    onLoading: () -> Result<T> = {
        Result.failure(IllegalStateException("Loading state not handled"))
    },
): Result<T> = fold(
    onSuccess = { Result.success(it) },
    onFailure = { Result.failure(it) },
    onLoading = onLoading
)

/**
 * convert [MDResult] to kotlin result
 * @return null when current status is [MDResult.onLoading]
 */
fun <T> MDResult<T>.asResult(): Result<T>? = fold(
    onSuccess = { Result.success(it) },
    onFailure = { Result.failure(it) },
    onLoading = { null }
)

/**
 * convert the flow to result emitting [MDResult.Loading] at [Flow.onStart] and [MDResult.Failure] on
 * [Flow.catch] and return every success value as [MDResult.success]
 */
fun <T> Flow<T>.asResult(): Flow<MDResult<T>> = map {
    MDResult.success(it)
}
    .onStart {
        emit(MDResult.loading())
    }
    .catch {
        emit(MDResult.failure(it))
    }
