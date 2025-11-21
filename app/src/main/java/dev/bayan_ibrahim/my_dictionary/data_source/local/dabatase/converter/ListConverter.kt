package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter

import androidx.room.TypeConverter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

abstract class ListConverter<T>(
    serializer: KSerializer<T>,
    private val onCorruptedString: (String) -> List<T> = {emptyList()}
) {
    private val listSerializer = ListSerializer(serializer)

    @TypeConverter
    fun listToStringConverter(
        list: List<T>,
    ): String {
        return Json.encodeToString(listSerializer, list)
    }

    @TypeConverter
    fun stringToListConverter(
        string: String,
    ): List<T> = try {
        // TODO, issue detected at version 2.0.0-beta02, and some users may have corrupted data, remove, this try catch on releasing first stable version
        Json.decodeFromString(deserializer = listSerializer, string = string)
    } catch (e: Exception) {
        onCorruptedString(string)
    }
}