package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelationEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbTypeTagRelationTagId


data class TypeTagWithRelation(
    @Embedded
    val tag: WordTypeTagEntity,
    @Relation(
        entity = WordTypeTagRelationEntity::class,
        parentColumn = dbTypeTagId,
        entityColumn = dbTypeTagRelationTagId,
    )
    val relations: List<WordTypeTagRelationEntity>
)
