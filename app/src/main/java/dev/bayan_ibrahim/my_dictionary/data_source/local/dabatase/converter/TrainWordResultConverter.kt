package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter

import androidx.room.TypeConverter
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordResult
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordResultSerializer
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType
import kotlinx.serialization.json.Json

data object TrainWordResultConverter {
    @TypeConverter
    fun trainResultToInt(trainWordType: TrainWordResult): String = Json.encodeToString(
        serializer = TrainWordResultSerializer,
        value = trainWordType
    )

    @TypeConverter
    fun intToTrainWordResult(value: String): TrainWordResult = Json.decodeFromString(
        deserializer = TrainWordResultSerializer,
        string = value
    )
}