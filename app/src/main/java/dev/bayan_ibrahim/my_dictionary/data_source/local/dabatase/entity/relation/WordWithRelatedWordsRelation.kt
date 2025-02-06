package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.ContextTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossContextTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordWordClassRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbContextTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordBaseWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsWordId

data class WordWithRelatedWordsRelation(
    @Embedded
    val word: WordEntity,
    @Relation(
        entity = WordWordClassRelatedWordEntity::class,
        parentColumn = dbWordId,
        entityColumn = dbWordClassRelatedWordBaseWordId,
    )
    val relatedWords: List<WordWordClassRelatedWordWithRelationEntity>,
)
