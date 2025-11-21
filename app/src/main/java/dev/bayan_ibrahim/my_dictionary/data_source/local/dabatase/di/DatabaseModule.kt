package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.di

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun providesDatabase(
        @ApplicationContext
        context: Context,
    ): MDDataBase = Room.databaseBuilder(
        context = context,
        klass = MDDataBase::class.java,
        name = "my_dictionary_db"
    )
        .fallbackToDestructiveMigrationFrom(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .addMigrations(object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE words ADD COLUMN word_note TEXT NOT NULL DEFAULT ''")
            }
        }, object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // current tag contestant for this version:
                val dbTagTable = "contextTags"
                val dbTagId = "contextTags_id"
                val dbTagPath = "contextTags_path"
                val dbTagParent = "contextTags_parent"
                val dbTagColor = "contextTags_color"
                val dbTagPassColorToChildren = "contextTags_passColorToChildren"

                val tempTableName = "${dbTagTable}_temp_for_hierarchy_migration"

                // 1. Create the new temporary table with the schema for version 13.
                // The foreign key will reference the temporary table itself, which becomes correct after renaming.
                db.execSQL(
                    "CREATE TABLE `$tempTableName` (" +
                            "`$dbTagId` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "`$dbTagPath` TEXT NOT NULL, " +                // This column will store the 'label' for each segment
                            "`$dbTagParent` INTEGER, " +                     // Stores the ID of the parent tag segment
                            "`$dbTagColor` INTEGER, " +
                            "`$dbTagPassColorToChildren` INTEGER NOT NULL DEFAULT 0, " +
                            "FOREIGN KEY(`$dbTagParent`) REFERENCES `$tempTableName`(`$dbTagId`) ON UPDATE SET NULL ON DELETE SET NULL" +
                            ")"
                )

                // This map will cache 'label' -> 'tagId' for segments already inserted into tempTableName
                // This helps ensure each unique label segment is created only once.
                val processedSegmentLabelsToId = mutableMapOf<String, Long>()

                // 2. Read data from the old table, parse paths, and insert into the new hierarchical structure.
                val oldTableCursor = db.query("SELECT $dbTagPath, $dbTagColor, $dbTagPassColorToChildren FROM $dbTagTable")
                oldTableCursor.use { cursor ->
                    while (cursor.moveToNext()) {
                        val originalFullPath = cursor.getString(cursor.getColumnIndexOrThrow(dbTagPath))
                        val originalColor: Int? = if (cursor.isNull(cursor.getColumnIndexOrThrow(dbTagColor))) null else cursor.getInt(
                            cursor.getColumnIndexOrThrow(dbTagColor)
                        )
                        val originalPassToChildrenInt = cursor.getInt(cursor.getColumnIndexOrThrow(dbTagPassColorToChildren))

                        // Split the path into segments. Filter out empty segments (e.g., from "a//b" or "a/b/")
                        val segments = originalFullPath.split('/').filter { it.isNotEmpty() }
                        var currentParentIdForSegment: Long? = null

                        for ((index, segmentLabel) in segments.withIndex()) {
                            var segmentTagId: Long? = processedSegmentLabelsToId[segmentLabel]

                            if (segmentTagId == null) {
                                // Segment label not in cache, check if it's already in the temp table
                                // (e.g., inserted by a previously processed path that shares this segment)
                                val queryExistingSegmentCursor = db.query(
                                    "SELECT `$dbTagId` FROM `$tempTableName` WHERE `$dbTagPath` = ?",
                                    arrayOf(segmentLabel)
                                )
                                queryExistingSegmentCursor.use { qc ->
                                    if (qc.moveToFirst()) {
                                        segmentTagId = qc.getLong(qc.getColumnIndexOrThrow(dbTagId))
                                        processedSegmentLabelsToId[segmentLabel] = segmentTagId // Cache it
                                    } else {
                                        // Segment does not exist in temp table yet, so insert it.
                                        val isLeafNodeOfOriginalPath = index == segments.size - 1
                                        val colorForThisSegment = if (isLeafNodeOfOriginalPath) originalColor else null
                                        /// default value of pass to children at this version is true and it has no effect since the color is null
                                        val passToChildrenForThisSegment = if (isLeafNodeOfOriginalPath) (originalPassToChildrenInt == 1) else true

                                        val contentValues = ContentValues().apply {
                                            put(dbTagPath, segmentLabel) // Store the segment as the 'label'
                                            if (currentParentIdForSegment != null) {
                                                put(dbTagParent, currentParentIdForSegment)
                                            } else {
                                                putNull(dbTagParent) // This is a root segment
                                            }
                                            if (colorForThisSegment != null) {
                                                put(dbTagColor, colorForThisSegment)
                                            } else {
                                                putNull(dbTagColor)
                                            }
                                            put(dbTagPassColorToChildren, passToChildrenForThisSegment)
                                        }

                                        // Insert the new segment. CONFLICT_NONE is used as we don't have a unique constraint on dbTagPath in tempTable yet.
                                        // The logic with cache and pre-query handles ensuring unique label processing.
                                        segmentTagId = db.insert(tempTableName, SQLiteDatabase.CONFLICT_NONE, contentValues)

                                        if (segmentTagId != -1L) {
                                            processedSegmentLabelsToId[segmentLabel] = segmentTagId // Cache newly inserted segment's ID
                                        }
                                    }
                                }
                            }
                            currentParentIdForSegment = segmentTagId // This segment's ID becomes parent for the next
                        }
                    }
                }

                // 3. Drop the old table
                db.execSQL("DROP TABLE `$dbTagTable`")

                // 4. Rename the new temporary table to the original table name
                db.execSQL("ALTER TABLE `$tempTableName` RENAME TO `$dbTagTable`")

                // 5. Re-create the unique index on the '$dbTagPath' (label) column, as per new entity definition.
                // Room typically names indices like 'index_{tableName}_{columnName}'.
                val indexName = "index_${dbTagTable}_${dbTagPath}"
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `$indexName` ON `$dbTagTable` (`$dbTagPath`)")
            }

        })
        .build()
}