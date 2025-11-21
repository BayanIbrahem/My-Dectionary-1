package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.TrainHistoryDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassRelatedWordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.WordClassRelationWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.language.LanguageDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.language.LanguageWordSpaceDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.tag.TagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithRelatedWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordWithTagsAndRelatedWordsDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word.WordsPaginatedDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_tag.WordsCrossTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.TagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.TrainHistoryEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity

@Database(
    entities = [
        WordEntity::class,
        TrainHistoryEntity::class,
        WordClassEntity::class,
        WordClassRelatedWordEntity::class,
        WordClassRelationEntity::class,
        LanguageEntity::class,
        TagEntity::class,
        WordCrossTagEntity::class,
    ],
    version = 13,
    exportSchema = false,
)
abstract class MDDataBase : RoomDatabase() {
    abstract fun getWordDao(): WordDao
    abstract fun getWordWithTagDao(): WordWithTagDao
    abstract fun getWordWithRelatedWordsDao(): WordWithRelatedWordsDao
    abstract fun getWordsWithTagAndRelatedWordsDao(): WordWithTagsAndRelatedWordsDao
    abstract fun getWordsPaginatedDao(): WordsPaginatedDao

    abstract fun getLanguageWordSpaceDao(): LanguageWordSpaceDao
    abstract fun getLanguageDao(): LanguageDao
    abstract fun getWordTrainDao(): TrainHistoryDao
    abstract fun getWordClassDao(): WordClassDao
    abstract fun getWordClassRelatedWordDao(): WordClassRelatedWordDao
    abstract fun getWordClassRelationDao(): WordClassRelationWordsDao
    abstract fun getTagDao(): TagDao
    abstract fun getWordsCrossTagsDao(): WordsCrossTagDao
}


/*
TableInfo{
    name = 'words', columns ={
        word_createdAt = Column {
            name = 'word_createdAt', type = 'INTEGER', affinity = '3', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
        }, word_examples = Column{
        name = 'word_examples', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Polysemy = Column{
        name = 'word_Polysemy', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Hyponym = Column{
        name = 'word_Hyponym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_languageCode = Column{
        name = 'word_languageCode', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_note = Column{
        name = 'word_note', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = ''''
    }, word_Prototype = Column{
        name = 'word_Prototype', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Collocation = Column{
        name = 'word_Collocation', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Metonymy = Column{
        name = 'word_Metonymy', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_id = Column{
        name = 'word_id', type = 'INTEGER', affinity = '3', notNull = false, primaryKeyPosition = 1, defaultValue = 'undefined'
    }, word_Homophone = Column{
        name = 'word_Homophone', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_transcription = Column{
        name = 'word_transcription', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_normalized_meaning = Column{
        name = 'word_normalized_meaning', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_memoryDecayFactor = Column{
        name = 'word_memoryDecayFactor', type = 'REAL', affinity = '4', notNull = true, primaryKeyPosition = 0, defaultValue = '1'
    }, word_lastTrain = Column{
        name = 'word_lastTrain', type = 'INTEGER', affinity = '3', notNull = false, primaryKeyPosition = 0, defaultValue = 'NULL'
    }, word_translation = Column{
        name = 'word_translation', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_typeTag = Column{
        name = 'word_typeTag', type = 'INTEGER', affinity = '3', notNull = false, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Meronym = Column{
        name = 'word_Meronym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_additionalTranslations = Column{
        name = 'word_additionalTranslations', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Hypernym = Column{
        name = 'word_Hypernym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Holonym = Column{
        name = 'word_Holonym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_meaning = Column{
        name = 'word_meaning', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_normalized_translation = Column{
        name = 'word_normalized_translation', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_updatedAt = Column{
        name = 'word_updatedAt', type = 'INTEGER', affinity = '3', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Synonym = Column{
        name = 'word_Synonym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Antonym = Column{
        name = 'word_Antonym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Homograph = Column{
        name = 'word_Homograph', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Homonym = Column{
        name = 'word_Homonym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }
    }, foreignKeys = [ForeignKey{
        referenceTable =
            'languages', onDelete = 'CASCADE +', onUpdate = 'CASCADE', columnNames = [word_languageCode], referenceColumnNames = [language_code]
    } , ForeignKey{
        referenceTable = 'typeTags', onDelete = 'SET NULL +', onUpdate = 'SET NULL', columnNames = [word_typeTag], referenceColumnNames = [typeTag_id]
    }], indices = [Index{
        name =
            'index_words_word_languageCode', unique = false, columns = [word_languageCode], orders = [ASC]'}, Index{name='index_words_word_normalized_translation', unique=false, columns=[word_normalized_translation], orders=[ASC]'
    }, Index{
        name =
            'index_words_word_normalized_meaning', unique = false, columns = [word_normalized_meaning], orders = [ASC]'}, Index{name='index_words_word_typeTag', unique=false, columns=[word_typeTag], orders=[ASC]'
    }]
}

TableInfo{
    name = 'words', columns ={
        word_id = Column {
            name = 'word_id', type = 'INTEGER', affinity = '3', notNull = false, primaryKeyPosition = 1, defaultValue = 'undefined'
        }, word_meaning = Column{
        name = 'word_meaning', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_createdAt = Column{
        name = 'word_createdAt', type = 'INTEGER', affinity = '3', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_updatedAt = Column{
        name = 'word_updatedAt', type = 'INTEGER', affinity = '3', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_normalized_meaning = Column{
        name = 'word_normalized_meaning', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_translation = Column{
        name = 'word_translation', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_normalized_translation = Column{
        name = 'word_normalized_translation', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_languageCode = Column{
        name = 'word_languageCode', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_additionalTranslations = Column{
        name = 'word_additionalTranslations', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_typeTag = Column{
        name = 'word_typeTag', type = 'INTEGER', affinity = '3', notNull = false, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_memoryDecayFactor = Column{
        name = 'word_memoryDecayFactor', type = 'REAL', affinity = '4', notNull = true, primaryKeyPosition = 0, defaultValue = '1'
    }, word_lastTrain = Column{
        name = 'word_lastTrain', type = 'INTEGER', affinity = '3', notNull = false, primaryKeyPosition = 0, defaultValue = 'NULL'
    }, word_transcription = Column{
        name = 'word_transcription', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_examples = Column{
        name = 'word_examples', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Synonym = Column{
        name = 'word_Synonym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Antonym = Column{
        name = 'word_Antonym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Hyponym = Column{
        name = 'word_Hyponym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Hypernym = Column{
        name = 'word_Hypernym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Meronym = Column{
        name = 'word_Meronym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Holonym = Column{
        name = 'word_Holonym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Homonym = Column{
        name = 'word_Homonym', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Polysemy = Column{
        name = 'word_Polysemy', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Prototype = Column{
        name = 'word_Prototype', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Metonymy = Column{
        name = 'word_Metonymy', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Collocation = Column{
        name = 'word_Collocation', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Homograph = Column{
        name = 'word_Homograph', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }, word_Homophone = Column{
        name = 'word_Homophone', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'undefined'
    }
    }, foreignKeys = [ForeignKey{
        referenceTable =
            'languages', onDelete = 'CASCADE +', onUpdate = 'CASCADE', columnNames = [word_languageCode], referenceColumnNames = [language_code]
    }, ForeignKey{
        referenceTable = 'typeTags', onDelete = 'SET NULL +', onUpdate = 'SET NULL', columnNames = [word_typeTag], refere
        nceColumnNames = [typeTag_id]
    }], indices = [Index{
        name =
            'index_words_word_normalized_translation', unique = false, columns = [word_normalized_translation], orders = [ASC]'}, Index{name='index_words_word_normalized_meaning', unique=false, columns=[word_normalized_meaning], orders=[ASC]'
    }, Index{
        name =
            'index_words_word_languageCode', unique = false, columns = [word_languageCode], orders = [ASC]'}, Index{name='index_words_word_typeTag', unique=false, columns=[word_typeTag], orders=[ASC]'
    }]
}
 */
