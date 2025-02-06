package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.tag.TagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_tag.WordsCrossTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.TagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asModel
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.repo.TagRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class MDRoomTagRepo(
    private val tagDao: TagDao,
    private val wordsCrossTagDao: WordsCrossTagDao,
) : TagRepo {
    override suspend fun addOrUpdateTag(tag: Tag): Tag {
        return if (tag.id != INVALID_ID) {
            tagDao.updateTag(tag.asEntity())
            tag
        } else {
            val newId = tagDao.insertTag(tag.asEntity())
            tag.copy(id = newId)
        }
    }

    override suspend fun removeTag(tag: Tag) {
        if (tag.id == INVALID_ID) {
            tagDao.deleteTagsOfValues(listOf(tag.value))
        } else {
            tagDao.deleteTagsOfIds(listOf(tag.id))
        }
    }


    override suspend fun getTag(id: Long): Tag? = tagDao.getTag(id)?.asModel()
    override fun getTagsStream(
        includeMarkerTags: Boolean,
        includeNonMarkerTags: Boolean,
    ): Flow<List<Tag>> = tagDao.getTagsOfMarkerStateAndLanguage(
        includeMarker = includeMarkerTags,
        includeNotMarker = includeNonMarkerTags
    ).onStart {
        emptyList<TagEntity>()
    }.combine(
        wordsCrossTagDao.getAllWordsCrossTags().map {
            it.groupBy { it.tagId }.mapValues { it.value.count() }
        }.onStart {
            emptyMap<Long, Int>()
        }
    ) { tags, tagIdWordsCount ->
        tags.map { tag ->
            tag.asModel(tagIdWordsCount[tag.tagId] ?: 0)
        }
    }
}