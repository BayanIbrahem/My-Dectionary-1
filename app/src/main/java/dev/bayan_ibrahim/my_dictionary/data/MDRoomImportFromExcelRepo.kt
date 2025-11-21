package dev.bayan_ibrahim.my_dictionary.data

import android.content.Context
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.meaningViewNormalize
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.db.MDDataBase
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.applyCollection
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.applyComputedOld
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.applyString
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.core.file_part.validateWithDatabase
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.data_type_wrapper.excel.MDExcel
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.data_type_wrapper.excel.poi.AndroidPoiMDExcel
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDCellData
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDExcelFileType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDExcelFileType.HSSF
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDExcelFileType.SXSSF
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDExcelFileType.XSSF
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.MDSheetDataType
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet.MDSheetImportDetails
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.sheet.MDSheetImportProgressStatus
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeader
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeaderRole
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeaderTagRole
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeaderWordClassRole
import dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header.MDSheetHeaderWordRole
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileSize
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictException
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyConflictStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionException
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDPropertyCorruptionStrategy
import dev.bayan_ibrahim.my_dictionary.domain.model.file.applyMergable
import dev.bayan_ibrahim.my_dictionary.domain.model.file.megabytes
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ParentedTag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelation
import dev.bayan_ibrahim.my_dictionary.domain.repo.LanguageRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.TagRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordClassRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordRepo
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.strHex
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import androidx.compose.ui.graphics.Color.Companion as ComposeColor


class MDRoomImportFromExcelRepo(
    private val context: Context,
    private val db: MDDataBase,
    private val tagRepo: TagRepo,
    private val wordClassRepo: WordClassRepo,
    private val wordRepo: WordRepo,
    private val languageRepo: LanguageRepo,
) {
    /**
     * if type is null then try the suggested type of file
     */
    fun onBuildMDExcel(
        document: MDDocumentData,
        type: MDExcelFileType? = null,
    ) = flow<MDExcel> {
        /// open valid stream of throw
        val stream = with(context) {
            document.open()
        }

        @Suppress("NAME_SHADOWING") val type = type ?: suggestTypeOfFile(document);
        val excel = when (type) {
            XSSF -> AndroidPoiMDExcel.createXSSF(stream)
            SXSSF -> AndroidPoiMDExcel.createSXSSF(stream)
            HSSF -> AndroidPoiMDExcel.createHSSF(stream)
        }.getOrThrow()
        emit(excel)
    }

    /**
     * suggest best type of file
     * * if suffix is [HSSF.sfx] (`xls`) then type is [HSSF]
     * * if suffix is [SXSSF.sfx] (`xlsx`) and the file is larger than [maxXSSFSize]
     * then the type is [SXSSF]
     * * else the type is [XSSF]
     */
    fun suggestTypeOfFile(document: MDDocumentData, maxXSSFSize: MDFileSize = 5.megabytes): MDExcelFileType {
        val legacy = document.sfx == HSSF.sfx
        return if (legacy) {
            HSSF
        } else if (document.size > maxXSSFSize) {
            SXSSF
        } else {
            XSSF
        }
    }

    suspend fun onImport(
        excel: MDExcel,
        sheets: List<MDSheetImportDetails>,
        corruptionStrategy: MDPropertyCorruptionStrategy,
        conflictStrategy: MDPropertyConflictStrategy,
        /**
         * on [MDPropertyCorruptionStrategy.AbortTransaction] abort only the current sheet
         */
        abortOnlyCorruptedSheet: Boolean = true,
        onAction: (sheetIndex: Int, sheet: MDSheetImportDetails, row: Int, status: MDSheetImportProgressStatus) -> Unit,
    ) {
        db.beginTransaction()
        sheets.forEachIndexed { sheetIndex, sheet ->
            onAction(sheetIndex, sheet, 0, MDSheetImportProgressStatus.ParsingRows)
            processSheet(
                excel = excel,
                sheetIndex = sheetIndex,
                sheet = sheet,
                conflictStrategy = conflictStrategy,
                corruptionStrategy = corruptionStrategy,
                onAction = onAction
            )
            onAction(sheetIndex, sheet, sheet.actualLastRow, MDSheetImportProgressStatus.Finished)
            if (abortOnlyCorruptedSheet) {
                db.setTransactionSuccessful()
                db.beginTransaction()
            }
        }
        db.endTransaction()
    }

    private suspend fun processSheet(
        excel: MDExcel,
        sheetIndex: Int,
        sheet: MDSheetImportDetails,
        conflictStrategy: MDPropertyConflictStrategy,
        corruptionStrategy: MDPropertyCorruptionStrategy,
        onAction: (Int, MDSheetImportDetails, Int, MDSheetImportProgressStatus) -> Unit,
    ) {
        /**
         * mutable map for rows where the key is the row index:
         * for each value:
         *      map for headers roles and its values
         */
        val rawRowsData = parseRawRowsData(
            excel = excel,
            sheet = sheet,
            onAction = onAction,
            sheetIndex = sheetIndex
        )

        processRowsData(
            onAction = onAction,
            sheetIndex = sheetIndex,
            sheet = sheet,
            rawRowsData = rawRowsData,
            conflictStrategy = conflictStrategy,
            corruptionStrategy = corruptionStrategy
        )
    }

    private suspend fun processRowsData(
        sheetIndex: Int,
        sheet: MDSheetImportDetails,
        rawRowsData: Map<Int, Map<MDSheetHeaderRole, MDCellData>>,
        conflictStrategy: MDPropertyConflictStrategy,
        corruptionStrategy: MDPropertyCorruptionStrategy,
        onAction: (Int, MDSheetImportDetails, Int, MDSheetImportProgressStatus) -> Unit,
    ) {
        onAction(sheetIndex, sheet, 0, MDSheetImportProgressStatus.ProcessingRows)
        when (sheet.dataType) {
            MDSheetDataType.TAG -> {
                processTagsRowsData(
                    sheetIndex = sheetIndex,
                    sheet = sheet,
                    rawRowsData = rawRowsData,
                    conflictStrategy = conflictStrategy,
                    corruptionStrategy = corruptionStrategy,
                    onAction = onAction
                )
            }

            MDSheetDataType.WORD -> {
                processWordsRowsData(
                    rawRowsData = rawRowsData,
                    onAction = onAction,
                    sheetIndex = sheetIndex,
                    sheet = sheet,
                    corruptionStrategy = corruptionStrategy,
                    conflictStrategy = conflictStrategy
                )
            }

            MDSheetDataType.WORD_CLASS -> {
                processWordsClassesRowsData(rawRowsData = rawRowsData, onAction = onAction, sheetIndex = sheetIndex, sheet = sheet)
            }

            MDSheetDataType.GENERAL -> {
                // DO nothing
            }
        }
    }

    private suspend fun processWordsClassesRowsData(
        rawRowsData: Map<Int, Map<MDSheetHeaderRole, MDCellData>>,
        onAction: (Int, MDSheetImportDetails, Int, MDSheetImportProgressStatus) -> Unit,
        sheetIndex: Int,
        sheet: MDSheetImportDetails,
    ) {
        rawRowsData.values.forEachIndexed { index, row ->
            onAction(sheetIndex, sheet, index, MDSheetImportProgressStatus.ProcessingRows)

            val rawLanguageCode = row[MDSheetHeaderWordClassRole.LanguageCode]?.toStringValue()
            val rawName = row[MDSheetHeaderWordClassRole.WordClassName]?.toStringValue()
            val rawRelations = row[MDSheetHeaderWordClassRole.WordClassRelationsNames]?.toStringListValue()
            if (!rawLanguageCode.isNullOrBlank() && !rawName.isNullOrBlank()) {
                val languageCode = LanguageCode(rawLanguageCode)
                val name = rawName.meaningViewNormalize
                val dbWordClass = wordClassRepo.addWordClass(
                    wordClass = WordClass(
                        name = name,
                        language = languageCode.language,
                        id = INVALID_ID,
                        relations = emptyList()
                    )
                )
                rawRelations?.forEach {
                    wordClassRepo.addWordClassRelation(
                        wordClassId = dbWordClass.id,
                        label = it.meaningViewNormalize,
                    )
                }
            }
        }
    }

    private suspend fun processWordsRowsData(
        sheetIndex: Int,
        sheet: MDSheetImportDetails,
        rawRowsData: Map<Int, Map<MDSheetHeaderRole, MDCellData>>,
        corruptionStrategy: MDPropertyCorruptionStrategy,
        conflictStrategy: MDPropertyConflictStrategy,
        onAction: (Int, MDSheetImportDetails, Int, MDSheetImportProgressStatus) -> Unit,
    ) {
        rawRowsData.values.forEachIndexed { index, row ->
            onAction(sheetIndex, sheet, index, MDSheetImportProgressStatus.ProcessingRows)

            processWordRowData(
                sheetIndex = sheetIndex,
                sheet = sheet,
                index = index,
                row = row,
                conflictStrategy = conflictStrategy,
                corruptionStrategy = corruptionStrategy,
                onAction = onAction
            )
        }
    }

    private suspend fun processWordRowData(
        sheetIndex: Int,
        sheet: MDSheetImportDetails,
        index: Int,
        row: Map<MDSheetHeaderRole, MDCellData>,
        conflictStrategy: MDPropertyConflictStrategy,
        corruptionStrategy: MDPropertyCorruptionStrategy,
        onAction: (Int, MDSheetImportDetails, Int, MDSheetImportProgressStatus) -> Unit,
    ) {
        val rawLanguageCode = row[MDSheetHeaderWordRole.LanguageCode]?.toStringValue()
        val rawMeaning = row[MDSheetHeaderWordRole.Meaning]?.toStringValue()
        val rawTranslation = row[MDSheetHeaderWordRole.Translation]?.toStringValue()
        val rawNote = row[MDSheetHeaderWordRole.Note]?.toStringValue()
        val rawTranscription = row[MDSheetHeaderWordRole.Transcription]?.toStringValue()
        val rawTags = row[MDSheetHeaderWordRole.Tag]?.toStringListValue()
        val rawAdditionalTranslation = row[MDSheetHeaderWordRole.AdditionalTranslation]?.toStringListValue()
        val rawExamples = row[MDSheetHeaderWordRole.Examples]?.toStringListValue()
        val rawWordClass = row[MDSheetHeaderWordRole.WordClass]?.toStringValue()
        val rawRelatedWords = row[MDSheetHeaderWordRole.RelatedWords]?.toStringListValue()
        val rawSynonym = row[MDSheetHeaderWordRole.Synonym]?.toStringListValue()
        val rawAntonym = row[MDSheetHeaderWordRole.Antonym]?.toStringListValue()
        val rawHyponym = row[MDSheetHeaderWordRole.Hyponym]?.toStringListValue()
        val rawHypernym = row[MDSheetHeaderWordRole.Hypernym]?.toStringListValue()
        val rawMeronym = row[MDSheetHeaderWordRole.Meronym]?.toStringListValue()
        val rawHolonym = row[MDSheetHeaderWordRole.Holonym]?.toStringListValue()
        val rawHomonym = row[MDSheetHeaderWordRole.Homonym]?.toStringListValue()
        val rawPolysemy = row[MDSheetHeaderWordRole.Polysemy]?.toStringListValue()
        val rawPrototype = row[MDSheetHeaderWordRole.Prototype]?.toStringListValue()
        val rawMetonymy = row[MDSheetHeaderWordRole.Metonymy]?.toStringListValue()
        val rawCollocation = row[MDSheetHeaderWordRole.Collocation]?.toStringListValue()
        val rawHomograph = row[MDSheetHeaderWordRole.Homograph]?.toStringListValue()
        val rawHomophone = row[MDSheetHeaderWordRole.Homophone]?.toStringListValue()
        val validNewWord = validNewWord(
            rawLanguageCode = rawLanguageCode,
            rawMeaning = rawMeaning,
            rawTranslation = rawTranslation
        )
        requireValidOrNotAbortTransaction(
            validNewWord = validNewWord,
            corruptionStrategy = corruptionStrategy,
            onAction = onAction,
            sheetIndex = sheetIndex,
            sheet = sheet,
            index = index,
            rawLanguageCode = rawLanguageCode,
            rawMeaning = rawMeaning,
            rawTranslation = rawTranslation
        )
        if (validNewWord) {
            val languageCode = LanguageCode(rawLanguageCode!!)
            val meaning: String = rawMeaning!!.meaningViewNormalize
            val translation: String = rawTranslation!!.meaningViewNormalize

            val dbWord = wordRepo.getWord(languageCode, meaning, translation)
            val shouldContinue = when (conflictStrategy) {
                MDPropertyConflictStrategy.AbortTransaction -> {
                    onAction(
                        sheetIndex,
                        sheet,
                        index,
                        MDSheetImportProgressStatus.Failed.Word.ConflictWord(
                            code = languageCode,
                            meaning = meaning,
                            translation = translation
                        )
                    )
                    throw MDPropertyConflictException.AbortTransaction
                }

                MDPropertyConflictStrategy.IgnoreEntry -> false
                MDPropertyConflictStrategy.IgnoreProperty,
                MDPropertyConflictStrategy.Override,
                MDPropertyConflictStrategy.MergeOrIgnore,
                MDPropertyConflictStrategy.MergeOrOverride,
                    -> true
            }
            if (shouldContinue) {
                val note = conflictStrategy.applyString(dbWord?.note) { rawNote }
                val transcription = conflictStrategy.applyString(dbWord?.transcription) { rawTranscription }
                val tags = conflictStrategy.applyCollection(
                    old = { dbWord?.tags },
                    new = {
                        rawTags?.map { label ->
                            tagRepo.getTag(label) ?: tagRepo.addOrUpdateTag(ParentedTag(label = label))
                        } ?: emptyList()
                    }
                ).toList()
                val additionalTranslations = conflictStrategy.applyCollection(
                    old = { dbWord?.additionalTranslations },
                    new = { rawAdditionalTranslation }
                ).toList()
                val examples = conflictStrategy.applyCollection(
                    old = { dbWord?.examples },
                    new = { rawExamples }
                ).toList()
                val wordClass = conflictStrategy.applyComputedOld(
                    old = dbWord?.wordClass,
                    new = {
                        rawWordClass?.let { rawWordClassName ->
                            wordClassRepo.addWordClass(
                                wordClass = WordClass(
                                    name = rawWordClassName,
                                    id = INVALID_ID,
                                    language = languageCode.language,
                                    relations = emptyList(),
                                )
                            )
                        }
                    },
                )
                val relatedWords = wordClass?.let { wordClass ->
                    conflictStrategy.applyComputedOld(
                        old = dbWord?.relatedWords,
                        new = {
                            val relations = wordClass.relations.associateBy {
                                it.label.meaningViewNormalize
                            }.toMutableMap()
                            rawRelatedWords?.mapNotNull { rawRelated ->
                                rawRelated.split(MDSheetHeaderWordRole.RELATED_WORD_SEPARATOR).let {
                                    if (it.count() < 2) {
                                        null
                                    } else {
                                        Pair(it[0], it[1])
                                    }
                                }?.let { (label, value) ->
                                    val relation = relations.getOrPut(
                                        key = label.meaningViewNormalize,
                                    ) {
                                        wordClassRepo.addWordClassRelation(
                                            wordClassId = wordClass.id, label = label
                                        )
                                    }
                                    RelatedWord(
                                        id = INVALID_ID,
                                        baseWordId = INVALID_ID,
                                        relationId = relation.id,
                                        relationLabel = relation.label,
                                        value = value,
                                    )
                                }
                            } ?: emptyList()
                        }
                    )
                }
                val lexicalRelations = conflictStrategy.applyMergable(
                    oldData = {
                        dbWord?.lexicalRelations
                    },
                    newData = {
                        (rawSynonym.mapNullable { synonym ->
                            WordLexicalRelation.Synonym(synonym)
                        } + rawAntonym.mapNullable { antonym ->
                            WordLexicalRelation.Antonym(antonym)
                        } + rawHyponym.mapNullable { hyponym ->
                            WordLexicalRelation.Hyponym(hyponym)
                        } + rawHypernym.mapNullable { hypernym ->
                            WordLexicalRelation.Hypernym(hypernym)
                        } + rawMeronym.mapNullable { meronym ->
                            WordLexicalRelation.Meronym(meronym)
                        } + rawHolonym.mapNullable { holonym ->
                            WordLexicalRelation.Holonym(holonym)
                        } + rawHomonym.mapNullable { homonym ->
                            WordLexicalRelation.Homonym(homonym)
                        } + rawPolysemy.mapNullable { polysemy ->
                            WordLexicalRelation.Polysemy(polysemy)
                        } + rawPrototype.mapNullable { prototype ->
                            WordLexicalRelation.Prototype(prototype)
                        } + rawMetonymy.mapNullable { metonymy ->
                            WordLexicalRelation.Metonymy(metonymy)
                        } + rawCollocation.mapNullable { collocation ->
                            WordLexicalRelation.Collocation(collocation)
                        } + rawHomograph.mapNullable { homograph ->
                            WordLexicalRelation.Homograph(homograph)
                        } + rawHomophone.mapNullable { homophone ->
                            WordLexicalRelation.Homophone(homophone)
                        }).groupBy { it.type }
                    },
                    merge = { old, new ->
                        (old.keys + new.keys).associateWith { key ->
                            (old[key] ?: emptyList()) + (new[key] ?: emptyList())
                        }
                    }
                ) ?: emptyMap()
                val createdAt = Clock.System.now()
                val word = Word(
                    id = INVALID_ID,
                    meaning = meaning,
                    translation = translation,
                    language = languageCode.language,
                    note = note,
                    additionalTranslations = additionalTranslations,
                    tags = tags.toSet(),
                    transcription = transcription,
                    examples = examples,
                    wordClass = wordClass,
                    relatedWords = relatedWords ?: emptyList(),
                    lexicalRelations = lexicalRelations,
                    createdAt = createdAt,
                    updatedAt = createdAt,
                )
                wordRepo.saveOrUpdateWord(word)
            }
        }
    }

    private fun validNewWord(rawLanguageCode: String?, rawMeaning: String?, rawTranslation: String?): Boolean =
        !rawLanguageCode.isNullOrBlank() && !rawMeaning.isNullOrBlank() && !rawTranslation.isNullOrBlank() && LanguageCode(
            rawLanguageCode
        ).valid

    private fun requireValidOrNotAbortTransaction(
        validNewWord: Boolean,
        corruptionStrategy: MDPropertyCorruptionStrategy,
        onAction: (Int, MDSheetImportDetails, Int, MDSheetImportProgressStatus) -> Unit,
        sheetIndex: Int,
        sheet: MDSheetImportDetails,
        index: Int,
        rawLanguageCode: String?,
        rawMeaning: String?,
        rawTranslation: String?,
    ) {
        if (!validNewWord && corruptionStrategy == MDPropertyCorruptionStrategy.AbortTransaction) {
            onAction(
                sheetIndex,
                sheet,
                index,
                MDSheetImportProgressStatus.Failed.Word.InvalidWord(
                    code = rawLanguageCode,
                    meaning = rawMeaning,
                    translation = rawTranslation
                )
            )
            throw MDPropertyCorruptionException.AbortTransaction
        }
    }

    private suspend fun processTagsRowsData(
        sheetIndex: Int,
        sheet: MDSheetImportDetails,
        rawRowsData: Map<Int, Map<MDSheetHeaderRole, MDCellData>>,
        conflictStrategy: MDPropertyConflictStrategy,
        corruptionStrategy: MDPropertyCorruptionStrategy,
        onAction: (Int, MDSheetImportDetails, Int, MDSheetImportProgressStatus) -> Unit,
    ) {
        rawRowsData.values.forEachIndexed { index, row ->
            onAction(sheetIndex, sheet, index, MDSheetImportProgressStatus.ProcessingRows)

            processTagRowData(
                sheetIndex = sheetIndex,
                sheet = sheet,
                row = row,
                index = index,
                conflictStrategy = conflictStrategy,
                corruptionStrategy = corruptionStrategy,
                onAction = onAction
            )
        }
    }

    private suspend fun processTagRowData(
        sheetIndex: Int,
        sheet: MDSheetImportDetails,
        row: Map<MDSheetHeaderRole, MDCellData>,
        index: Int,
        conflictStrategy: MDPropertyConflictStrategy,
        corruptionStrategy: MDPropertyCorruptionStrategy,
        onAction: (Int, MDSheetImportDetails, Int, MDSheetImportProgressStatus) -> Unit,
    ) {
        val rawLabel = row[MDSheetHeaderTagRole.PassColor]?.toStringValue() // type string
        val rawParentTag = row[MDSheetHeaderTagRole.ParentTag]?.toStringValue() // type string
        val rawColor = row[MDSheetHeaderTagRole.Color]?.toStringValue() // type string
        val rawPassColor = row[MDSheetHeaderTagRole.PassColor]?.let { (it as? MDCellData.Bool)?.value }

        val label: String = rawLabel ?: ""
        val dbTag = tagRepo.getTag(label)

        val color = rawColor?.let { ComposeColor.strHex(it) }
        val parent = rawParentTag?.let {
            tagRepo.getTag(rawParentTag)
        }

        val passColor = color != null && rawPassColor == true
        val newTag = try {
            ParentedTag(
                label = label,
                color = color,
                parentId = parent?.id,
                passColor = passColor,
            ).validateWithDatabase(
                dbTag = dbTag,
                conflictStrategy = conflictStrategy,
                corruptedStrategy = corruptionStrategy
            )

        } catch (e: MDPropertyConflictException.AbortTransaction) {
            onAction(sheetIndex, sheet, index, MDSheetImportProgressStatus.Failed.Tag.ConflictTagLabel(label))
            throw e
        } catch (e: MDPropertyCorruptionException.AbortTransaction) {
            onAction(sheetIndex, sheet, index, MDSheetImportProgressStatus.Failed.Tag.InvalidTagLabel)
            throw e
        } catch (e: MDPropertyConflictException.AbortEntry) {
            null
        } catch (e: MDPropertyCorruptionException.AbortEntry) {
            null
        }
        newTag?.let { tagRepo.addOrUpdateTag(it) }
    }

    private suspend fun parseRawRowsData(
        excel: MDExcel,
        sheet: MDSheetImportDetails,
        onAction: (Int, MDSheetImportDetails, Int, MDSheetImportProgressStatus) -> Unit,
        sheetIndex: Int,
    ): Map<Int, Map<MDSheetHeaderRole, MDCellData>> {
        /**
         * mutable map for rows where the key is the row index:
         * for each value:
         *      map for headers roles and its values
         */
        val rawRowsData: MutableMap<Int, MutableMap<MDSheetHeaderRole, MDCellData>> = mutableMapOf()

        /** sheet headers with actual columns indexes */
        val indexedHeaders: Map<Int, MDSheetHeader> = sheet.headers.associateBy { it.index }
        // reading sheet rows and assign data to rawRowsData as a string set for each row
        excel.readSheetRows(
            sheet = sheet,
            startRow = sheet.actualFirstRow,
            endRow = sheet.actualLastRow
        ) { index, cells ->
            onAction(sheetIndex, sheet, index, MDSheetImportProgressStatus.ParsingRows)
            val rowMap = rawRowsData.getOrPut(index) { mutableMapOf() }
            cells.forEach { cell ->
                val header = indexedHeaders[cell.columnIndex]
                if (header != null && header is MDSheetHeader.Assigned) {
                    val data = cell.data
                    if (data !is MDCellData.Blank) {
                        rowMap[header.explicitRote] = data
                    }
                }
            }
        }
        return rawRowsData
    }
}

inline fun <T, R> Iterable<T>?.mapNullable(transform: (T) -> R): List<R> {
    return this?.map(transform) ?: emptyList()
}

