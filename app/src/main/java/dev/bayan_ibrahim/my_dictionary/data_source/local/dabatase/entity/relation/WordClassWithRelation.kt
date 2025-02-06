package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationTagId


data class WordClassWithRelation(
    @Embedded
    val tag: WordClassEntity,
    @Relation(
        entity = WordClassRelationEntity::class,
        parentColumn = dbWordClassId,
        entityColumn = dbWordClassRelationTagId,
    )
    val relations: List<WordClassRelationEntity>
)
