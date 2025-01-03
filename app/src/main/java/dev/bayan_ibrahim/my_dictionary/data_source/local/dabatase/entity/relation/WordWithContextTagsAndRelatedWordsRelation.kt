package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.ContextTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossContextTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbContextTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordBaseWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordsCrossContextTagsWordId

data class WordWithContextTagsAndRelatedWordsRelation(
    @Embedded
    val word: WordEntity,
    @Relation(
        entity = WordTypeTagRelatedWordEntity::class,
        parentColumn = dbWordId,
        entityColumn = dbTypeTagRelatedWordBaseWordId,
    )
    val relatedWords: List<WordTypeTagRelatedWordWithRelationEntity>,
    @Relation(
        parentColumn = dbWordId,
        entityColumn = dbContextTagId,
        associateBy = Junction(
            value = WordCrossContextTagEntity::class,
            parentColumn = dbWordsCrossContextTagsWordId,
            entityColumn = dbWordsCrossContextTagsTagId,
        )
    )
    val tags: List<ContextTagEntity>,
)
