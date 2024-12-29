package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.context_tag.ContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_context_tag.WordsCrossContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.ContextTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asModel
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDContextTagsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class MDContextTagsRepoImpl(
    private val contextTagDao: ContextTagDao,
    private val wordsCrossContextTagDao: WordsCrossContextTagDao,
) : MDContextTagsRepo {
    override suspend fun addOrUpdateContextTag(tag: ContextTag): ContextTag {
        return if (tag.id != INVALID_ID) {
            contextTagDao.updateContextTag(tag.asEntity())
            tag
        } else {
            val newId = contextTagDao.insertContextTag(tag.asEntity())
            tag.copy(id = newId)
        }
    }

    override suspend fun removeContextTag(tag: ContextTag) {
        if (tag.id == INVALID_ID) {
            contextTagDao.deleteContextTagsOfValues(listOf(tag.value))
        } else {
            contextTagDao.deleteContextTagsOfIds(listOf(tag.id))
        }
    }


    override fun getContextTagsStream(
        includeMarkerTags: Boolean,
        includeNonMarkerTags: Boolean,
    ): Flow<List<ContextTag>> = contextTagDao.getAllTagsOfMarkerState(
        includeMarker = includeMarkerTags,
        includeNotMarker = includeNonMarkerTags
    ).onStart {
        emptyList<ContextTagEntity>()
    }.combine(
        wordsCrossContextTagDao.getAllWordsCrossContextTags().map {
            it.groupBy { it.tagId }.mapValues { it.value.count() }
        }.onStart {
            emptyMap<Long, Int>()
        }
    ) { contextTags, tagIdWordsCount ->
        contextTags.map { tag ->
            tag.asModel(tagIdWordsCount[tag.tagId] ?: 0)
        }
    }
}