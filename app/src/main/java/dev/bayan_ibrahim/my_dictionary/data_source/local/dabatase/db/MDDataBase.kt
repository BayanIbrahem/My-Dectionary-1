package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagRelatedWordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelationEntity

@Database(
    entities = [
        WordEntity::class,
        WordTypeTagEntity::class,
        WordTypeTagRelatedWordEntity::class,
        WordTypeTagRelationEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class MDDataBase : RoomDatabase() {
    abstract fun getWordDao(): WordDao
    abstract fun getWordTypeTagDao(): WordTypeTagDao
    abstract fun getWordTypeTagRelatedWordDao(): WordTypeTagRelatedWordDao
}