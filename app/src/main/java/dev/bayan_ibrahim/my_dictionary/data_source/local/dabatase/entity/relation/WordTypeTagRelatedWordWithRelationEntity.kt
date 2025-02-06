package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordWordClassRelatedWordEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordWordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationLabel

data class WordWordClassRelatedWordWithRelationEntity(
    @Embedded
    val related: WordWordClassRelatedWordEntity,
    @Relation(
        WordWordClassRelationEntity::class,
        parentColumn = dbWordClassRelatedWordRelationId,
        entityColumn = dbWordClassRelationId,
        projection = [dbWordClassRelationLabel]
    )
    val relationLabel: String,
)
