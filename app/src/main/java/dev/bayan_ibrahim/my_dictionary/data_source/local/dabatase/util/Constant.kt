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
const val dbWordTags = "words_tags"
const val dbWordExamples = "word_examples"
const val dbWordTranscription = "word_transcription"
const val dbWordLearningProgress = "word_learningProgress"
const val dbWordTypeTag = "word_typeTag"

// Word Train:
const val dbTrainHistoryTable = "trainHistory"
const val dbTrainHistoryId = "trainHistory_id"
const val dbTrainHistoryWordId = "trainHistory_wordId"
const val dbTrainHistoryTime = "trainHistory_time"
const val dbTrainHistoryMeaningSnapshot = "trainHistory_wordMeaningSnapshot"
const val dbTrainHistoryTrainHistoryType = "trainHistory_TrainType"
const val dbTrainHistoryTrainHistoryResult = "trainHistory_TrainResult"

// LanguageWordSpace :
const val dbLanguageWordSpaceLanguageCode = "languageWordSpace_code"
const val dbLanguageWordSpaceWordsCount = "languageWordSpace_wordsCount"
const val dbLanguageWordSpaceAverageLearningProgress = "languageWordSpace_averageLearningProgress"

// Word type tag:
const val dbTypeTagTable = "tags"
const val dbTypeTagId = "tag_id"
const val dbTypeTagName = "tag_name"
const val dbTypeTagLanguage = "tag_language"

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