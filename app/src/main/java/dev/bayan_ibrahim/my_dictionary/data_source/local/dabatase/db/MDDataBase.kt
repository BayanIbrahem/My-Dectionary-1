package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.domain.model.Word

@Database(
    entities = [
        Word::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class MDDataBase: RoomDatabase() {
    abstract fun getWordDao(): WordDao
}