package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelatedWordRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelationLabel

data class WordTypeTagRelatedWordWithRelationEntity(
    @Embedded
    val related: WordTypeTagRelatedWordEntity,
    @Relation(
        WordTypeTagRelationEntity::class,
        parentColumn = dbTypeTagRelatedWordRelationId,
        entityColumn = dbTypeTagRelationId,
        projection = [dbTypeTagRelationLabel]
    )
    val relationLabel: String,
)
