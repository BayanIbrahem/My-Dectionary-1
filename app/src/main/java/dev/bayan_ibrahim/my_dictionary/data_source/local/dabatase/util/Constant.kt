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
const val dbWordNote= "word_note"
const val dbWordMemoryDecayFactor = "word_memoryDecayFactor"
const val dbWordLastTrain = "word_lastTrain"
const val dbWordClass = "word_typeTag"
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

// Word word class:
const val dbWordClassTable = "typeTags"
const val dbWordClassId = "typeTag_id"
const val dbWordClassName = "typeTag_name"
const val dbWordClassLanguage = "typeTag_language"

// Word word class relation:
const val dbWordClassRelationTable = "tagRelations"
const val dbWordClassRelationId = "relation_id"
const val dbWordClassRelationLabel = "relation_label"
const val dbWordClassRelationTagId = "relation_tagId"

// Word word class related words:
const val dbWordClassRelatedWordTable = "tagRelatedWords"
const val dbWordClassRelatedWordId = "relatedWord_id"
const val dbWordClassRelatedWordRelationId = "relatedWord_relationId"
const val dbWordClassRelatedWordBaseWordId = "relatedWord_baseWordId"
const val dbWordClassRelatedWordName = "relatedWord_name"

// Language:
const val dbLanguageTable = "languages"
const val dbLanguageCode = "language_code"

// Context tags:
const val dbTagTable = "contextTags"
const val dbTagId = "contextTags_id"
const val dbTagLabel = "contextTags_path"
const val dbTagParent = "contextTags_parent"
const val dbTagColor = "contextTags_color"
const val dbTagPassColorToChildren = "contextTags_passColorToChildren"

// tags with words:
const val dbWordsCrossTagsTable = "wordsWithContextTags"
const val dbWordsCrossTagsId = "wordsWithContextTags_id"
const val dbWordsCrossTagsWordId = "wordsWithContextTags_wordId"
const val dbWordsCrossTagsTagId = "wordsWithContextTags_tagId"
