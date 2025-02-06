package dev.bayan_ibrahim.my_dictionary.domain.repo

import androidx.paging.PagingData
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageCode
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListMemorizingProbabilityGroup
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListSortByOrder
import dev.bayan_ibrahim.my_dictionary.ui.screen.words_list.util.MDWordsListViewPreferencesSortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat

interface WordRepo {
    fun getWordsIdsOf(
        languages: Set<LanguageCode> = emptySet(),
        tags: Set<Tag> = emptySet(),
        includeTags: Boolean = true,
        wordsClasses: Set<Long> = emptySet(),
        memorizingProbabilities: Set<MDWordsListMemorizingProbabilityGroup> = emptySet(),
    ): Flow<Set<Long>>

    fun getWordsOfIds(ids: Set<Long>): Flow<Sequence<Word>>

    fun getWordsOf(
        languages: Set<LanguageCode> = emptySet(),
        tags: Set<Tag> = emptySet(),
        includeTags: Boolean = true,
        wordsClasses: Set<Long> = emptySet(),
        memorizingProbabilities: Set<MDWordsListMemorizingProbabilityGroup> = emptySet(),
    ): Flow<Sequence<Word>> = getWordsIdsOf(
        languages = languages,
        tags = tags,
        includeTags = includeTags,
        wordsClasses = wordsClasses,
        memorizingProbabilities = memorizingProbabilities
    ).flatMapConcat(::getWordsOfIds)

    suspend fun getWord(wordId: Long): Word?

    // words list
    fun getPaginatedWordsList(
        code: LanguageCode,
        targetWords: Set<Long>,
        includeMeaning: Boolean,
        includeTranslation: Boolean,
        searchQuery: String,
        sortBy: MDWordsListViewPreferencesSortBy,
        sortByOrder: MDWordsListSortByOrder,
    ): Flow<PagingData<Word>>

    suspend fun deleteWord(id: Long) = deleteWords(listOf(id))
    suspend fun deleteWords(ids: Collection<Long>)
    suspend fun appendTagsToWords(
        wordsIds: Collection<Long>,
        tags: Collection<Tag>,
    )

    suspend fun saveOrUpdateWord(word: Word): Long
}