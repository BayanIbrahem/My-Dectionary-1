package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordWordClassEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordWordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationTagId


data class WordClassWithRelation(
    @Embedded
    val tag: WordWordClassEntity,
    @Relation(
        entity = WordWordClassRelationEntity::class,
        parentColumn = dbWordClassId,
        entityColumn = dbWordClassRelationTagId,
    )
    val relations: List<WordWordClassRelationEntity>
)
