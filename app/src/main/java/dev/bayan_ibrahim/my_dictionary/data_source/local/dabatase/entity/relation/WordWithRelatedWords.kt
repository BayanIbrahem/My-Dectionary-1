package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordBaseWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity

//data class WordWithRelatedWords(
//    @Embedded
//    val word: WordEntity,
//    @Relation(
//        entity = WordTypeTagRelatedWordEntity::class,
//        parentColumn = dbWordId,
//        entityColumn = dbTypeTagRelatedWordBaseWordId,
//    )
//    val relatedWords: List<WordTypeTagRelatedWordWithRelationEntity>
//)
