package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.TagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordBaseWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossTagsTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossTagsWordId

data class WordWithRelatedWordsRelation(
    @Embedded
    val word: WordEntity,
    @Relation(
        entity = WordClassRelatedWordEntity::class,
        parentColumn = dbWordId,
        entityColumn = dbWordClassRelatedWordBaseWordId,
    )
    val relatedWords: List<WordClassRelatedWordWithRelationEntity>,
)
