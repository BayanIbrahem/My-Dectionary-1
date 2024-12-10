package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter

import androidx.room.TypeConverter
import dev.bayan_ibrahim.my_dictionary.domain.model.train_word.TrainWordType

data object TrainWordTypeConverter {
    @TypeConverter
    fun trainTypeToInt(trainWordType: TrainWordType): Int = trainWordType.key

    @TypeConverter
    fun intToTrainWordType(key: Int): TrainWordType = TrainWordType.entries.first { it.key == key }
}