package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.LanguageDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.LanguageWordSpaceDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.TrainHistoryDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagRelatedWordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagRelationWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.TrainHistoryEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelationEntity

@Database(
    entities = [
        WordEntity::class,
        TrainHistoryEntity::class,
        WordTypeTagEntity::class,
        WordTypeTagRelatedWordEntity::class,
        WordTypeTagRelationEntity::class,
        LanguageEntity::class,
    ],
    version = 5,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = MDDataBase.MigrationSpec1_2::class
        ),
    ],
    exportSchema = true,
)
abstract class MDDataBase : RoomDatabase() {
    abstract fun getLanguageWordSpaceDao(): LanguageWordSpaceDao
    abstract fun getLanguageDao(): LanguageDao
    abstract fun getWordDao(): WordDao
    abstract fun getWordTrainDao(): TrainHistoryDao
    abstract fun getWordTypeTagDao(): WordTypeTagDao
    abstract fun getWordTypeTagRelatedWordDao(): WordTypeTagRelatedWordDao
    abstract fun getWordTypeTagRelationDao(): WordTypeTagRelationWordsDao

    class MigrationSpec1_2:AutoMigrationSpec
}

