package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.context_tag.ContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.language.LanguageDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.language.LanguageWordSpaceDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.TrainHistoryDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagRelatedWordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordTypeTagRelationWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithContextTagsAndRelatedWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithRelatedWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_context_tag.WordsCrossContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordsPaginatedDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.ContextTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.TrainHistoryEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossContextTagEntity
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
        ContextTagEntity::class,
        WordCrossContextTagEntity::class,
    ],
    version = 9,
    exportSchema = true,
)
abstract class MDDataBase : RoomDatabase() {
    abstract fun getWordDao(): WordDao
    abstract fun getWordWithContextTagDao(): WordWithContextTagDao
    abstract fun getWordWithRelatedWordsDao(): WordWithRelatedWordsDao
    abstract fun getWordsWithContextTagAndRelatedWordsDao(): WordWithContextTagsAndRelatedWordsDao
    abstract fun getWordsPaginatedDao(): WordsPaginatedDao

    abstract fun getLanguageWordSpaceDao(): LanguageWordSpaceDao
    abstract fun getLanguageDao(): LanguageDao
    abstract fun getWordTrainDao(): TrainHistoryDao
    abstract fun getWordTypeTagDao(): WordTypeTagDao
    abstract fun getWordTypeTagRelatedWordDao(): WordTypeTagRelatedWordDao
    abstract fun getWordTypeTagRelationDao(): WordTypeTagRelationWordsDao
    abstract fun getContextTagDao(): ContextTagDao
    abstract fun getWordsCrossTagsDao(): WordsCrossContextTagDao
}


