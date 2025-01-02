package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

// Word:
const val dbWordTable = "words"
const val dbWordId = "word_id"
const val dbWordMeaning = "word_meaning"
const val dbWordCreatedAt = "word_createdAt"
const val dbWordUpdatedAt = "word_updatedAt"
const val dbWordNormalizedMeaning = "word_normalized_meaning"
const val dbWordTranslation = "word_translation"
const val dbWordNormalizedTranslation = "word_normalized_translation"
const val dbWordLanguageCode = "word_languageCode"
const val dbWordAdditionalTranslations = "word_additionalTranslations"
const val dbWordExamples = "word_examples"
const val dbWordTranscription = "word_transcription"
const val dbWordMemoryDecayFactor = "word_memoryDecayFactor"
const val dbWordLastTrain = "word_lastTrain"
const val dbWordTypeTag = "word_typeTag"
const val dbWordSynonym = "word_Synonym"
const val dbWordAntonym = "word_Antonym"
const val dbWordHyponym = "word_Hyponym"
const val dbWordHypernym = "word_Hypernym"
const val dbWordMeronym = "word_Meronym"
const val dbWordHolonym = "word_Holonym"
const val dbWordHomonym = "word_Homonym"
const val dbWordPolysemy = "word_Polysemy"
const val dbWordPrototype = "word_Prototype"
const val dbWordMetonymy = "word_Metonymy"
const val dbWordCollocation = "word_Collocation"
const val dbWordHomograph = "word_Homograph"
const val dbWordHomophone = "word_Homophone"


// Word Train:
const val dbTrainHistoryTable = "trainHistory"
const val dbTrainHistoryId = "trainHistory_id"
const val dbTrainHistoryWordId = "trainHistory_wordId"
const val dbTrainHistoryTime = "trainHistory_time"
const val dbTrainHistoryQuestionWord = "trainHistory_questionWord"
const val dbTrainHistoryTrainHistoryType = "trainHistory_TrainType"
const val dbTrainHistoryTrainHistoryResult = "trainHistory_TrainResult"

// LanguageWordSpace :
const val dbLanguageWordSpaceLanguageCode = "languageWordSpace_code"
const val dbLanguageWordSpaceWordsCount = "languageWordSpace_wordsCount"
//const val dbLanguageWordSpaceAverageMemorizingProbability = "languageWordSpace_averageMemorizingProbability"

// Word type tag:
const val dbTypeTagTable = "typeTags"
const val dbTypeTagId = "typeTag_id"
const val dbTypeTagName = "typeTag_name"
const val dbTypeTagLanguage = "typeTag_language"

// Word type tag relation:
const val dbTypeTagRelationTable = "tagRelations"
const val dbTypeTagRelationId = "relation_id"
const val dbTypeTagRelationLabel = "relation_label"
const val dbTypeTagRelationTagId = "relation_tagId"

// Word type tag related words:
const val dbTypeTagRelatedWordTable = "tagRelatedWords"
const val dbTypeTagRelatedWordId = "relatedWord_id"
const val dbTypeTagRelatedWordRelationId = "relatedWord_relationId"
const val dbTypeTagRelatedWordBaseWordId = "relatedWord_baseWordId"
const val dbTypeTagRelatedWordName = "relatedWord_name"

// Language:
const val dbLanguageTable = "languages"
const val dbLanguageCode = "language_code"

// Context tags:
const val dbContextTagTable = "contextTags"
const val dbContextTagId = "contextTags_id"
const val dbContextTagPath = "contextTags_path"
const val dbContextTagColor = "contextTags_color"
const val dbContextTagPassColorToChildren = "contextTags_passColorToChildren"

// context tags with words:
const val dbWordsCrossContextTagsTable = "wordsWithContextTags"
const val dbWordsCrossContextTagsId = "wordsWithContextTags_id"
const val dbWordsCrossContextTagsWordId = "wordsWithContextTags_wordId"
const val dbWordsCrossContextTagsTagId = "wordsWithContextTags_tagId"
