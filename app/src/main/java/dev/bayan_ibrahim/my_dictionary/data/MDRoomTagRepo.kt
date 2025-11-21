package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.normalizer.tagMatchNormalize
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.tag.TagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_tag.WordsCrossTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asModel
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ParentedTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.repo.TagRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MDRoomTagRepo(
    private val tagDao: TagDao,
    private val wordsCrossTagDao: WordsCrossTagDao,
) : TagRepo {
    override suspend fun addOrUpdateTag(tag: Tag, parentId: Long?): ParentedTag {
        return if (tag.id != INVALID_ID) {
            tagDao.updateTag(tag.asEntity())
            ParentedTag(tag = tag, parentId = parentId)
        } else {
            val newId = tagDao.insertTag(tag.asEntity())
            getTag(newId)!!
        }
    }

    override suspend fun removeTag(tag: Tag, parentId: Long?) {
        if (tag.id == INVALID_ID) {
            tagDao.deleteTagsOfValues(listOf(tag.label))
        } else {
            tagDao.deleteTagsOfIds(listOf(tag.id))
        }
    }


    override suspend fun getTag(id: Long): ParentedTag? = tagDao.getTag(id)?.asModel()
    override suspend fun getTag(label: String): ParentedTag? = tagDao.getTag(label.tagMatchNormalize)?.asModel()

    override suspend fun getTags(ids: Set<Long>): List<ParentedTag> = tagDao.getTags(ids).map { it.asModel() }
    override suspend fun getTags(labels: Set<String>): List<ParentedTag> = tagDao.getTags(
        labels = labels.map {
            it.tagMatchNormalize
        }.toSet()
    ).map {
        it.asModel()
    }

    override fun getTagsStream(
        includeMarkerTags: Boolean,
        includeNonMarkerTags: Boolean,
    ): Flow<List<ParentedTag>> = tagDao.getTagsOfMarkerStateAndLanguage(
        includeMarker = includeMarkerTags,
        includeNotMarker = includeNonMarkerTags
    ).map { tags ->
        tags.map { tag ->
            tag.asModel()
        }
    }

    override fun getWordsCount(): Flow<Map<Long, Int>> {
        return wordsCrossTagDao.getAllWordsCrossTags().map {
            it.groupBy { it.tagId }.mapValues { it.value.count() }
        }
    }
}