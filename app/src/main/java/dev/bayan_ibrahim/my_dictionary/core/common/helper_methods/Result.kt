package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

fun <T> Result<Result<T>>.flatten(): Result<T> = runCatching {
    this.getOrThrow().getOrThrow()
}
