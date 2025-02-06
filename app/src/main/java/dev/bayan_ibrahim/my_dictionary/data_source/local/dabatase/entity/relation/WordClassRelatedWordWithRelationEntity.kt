package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationLabel

data class WordClassRelatedWordWithRelationEntity(
    @Embedded
    val related: WordClassRelatedWordEntity,
    @Relation(
        WordClassRelationEntity::class,
        parentColumn = dbWordClassRelatedWordRelationId,
        entityColumn = dbWordClassRelationId,
        projection = [dbWordClassRelationLabel]
    )
    val relationLabel: String,
)
