package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordBaseWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordName
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelatedWordTable
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordClassRelationId
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.dbWordId

@Entity(
    tableName = dbWordClassRelatedWordTable,
    foreignKeys = [
        ForeignKey(
            entity = WordWordClassRelationEntity::class,
            parentColumns = [dbWordClassRelationId],
            childColumns = [dbWordClassRelatedWordRelationId],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = [dbWordId],
            childColumns = [dbWordClassRelatedWordBaseWordId],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(dbWordClassRelatedWordRelationId),
        Index(dbWordClassRelatedWordBaseWordId),
    ]
)
data class WordWordClassRelatedWordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = dbWordClassRelatedWordId)
    val id: Long? = null,
    @ColumnInfo(name = dbWordClassRelatedWordRelationId)
    val relationId: Long,
    @ColumnInfo(name = dbWordClassRelatedWordBaseWordId)
    val baseWordId: Long,
    @ColumnInfo(name = dbWordClassRelatedWordName)
    val word: String,
)