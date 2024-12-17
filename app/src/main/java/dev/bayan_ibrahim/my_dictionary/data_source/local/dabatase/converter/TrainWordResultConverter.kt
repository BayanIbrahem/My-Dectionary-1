package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter

import androidx.room.TypeConverter
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.MDTrainWordResult
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordResultSerializer
import kotlinx.serialization.json.Json

data object TrainWordResultConverter {
    @TypeConverter
    fun trainResultToInt(trainWordType: MDTrainWordResult): String = Json.encodeToString(
        serializer = TrainWordResultSerializer,
        value = trainWordType
    )

    @TypeConverter
    fun intToTrainWordResult(value: String): MDTrainWordResult = Json.decodeFromString(
        deserializer = TrainWordResultSerializer,
        string = value
    )
}