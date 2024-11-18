package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

fun <T> Iterator<T>.nextOrNull(): T? = if (hasNext()) next() else null
fun <T> Iterator<T>.nextOrElse(builder: () -> T): T? = if (hasNext()) next() else builder()
