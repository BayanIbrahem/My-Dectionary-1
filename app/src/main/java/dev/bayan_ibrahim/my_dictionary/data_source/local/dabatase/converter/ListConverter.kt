package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter

import androidx.room.TypeConverter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

private const val separator = ", "

private const val emptyJsonString = "\"\""

abstract class ListConverter<T>(
    private val serializer: KSerializer<T>,
) {
    @TypeConverter
    fun listToStringConverter(
        list: List<T>,
    ): String = if(list.isEmpty()) "" else list.joinToString(separator = separator) { listItem ->
        Json.encodeToString(serializer = serializer, value = listItem)
    }

    @TypeConverter
    fun stringToListConverter(
        string: String,
    ): List<T> = if (string.isEmpty() || string == emptyJsonString) emptyList() else string.split(separator).map { listItem ->
        Json.decodeFromString(deserializer = serializer, string = listItem)
    }
}