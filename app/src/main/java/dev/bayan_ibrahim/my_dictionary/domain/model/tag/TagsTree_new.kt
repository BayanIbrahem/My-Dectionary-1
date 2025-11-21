package dev.bayan_ibrahim.my_dictionary.domain.model.tag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.Color
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap

interface TagsTree {
    /** return the parent of the passed id  or throw [IllegalArgumentException]*/
    fun parent(id: Long): Tag?

    /**
     * a map of tags ids with its orders, used for sort,
     * tags order must follow those roles:
     * 1) parent before child
     * 2) asc order between siblings
     * 3) child before sibling
     */
    val tagsOrderKeys: Map<Long, Int>

    /**
     * check the level of a node in the tags tree.
     * for root tags the value is 0
     */
    fun level(id: Long): Int {
        return levelRecursive(id, 0)
    }

    private fun levelRecursive(id: Long, count: Int = 0): Int {
        val parent = parent(id) ?: return count
        return levelRecursive(parent.id, count.inc())
    }

    operator fun contains(id: Long): Boolean
    operator fun contains(tag: Tag): Boolean = contains(tag.id)
    fun getOrNull(id: Long): Tag?
    operator fun get(id: Long): Tag = getOrNull(id) ?: throw IllegalArgumentException("Requested a non existed map id")

    /**
     * return the direct children of the passed id  or throw [IllegalArgumentException]
     * @see topLevelTags
     * */
    fun children(id: Long): List<Tag>

    /**
     * return a list of all predecessors of this tag, each child is followed by its children directly in the tree
     */
    fun predecessors(id: Long): List<Tag> {
        val list = mutableListOf<Tag>()
        predecessorsRecursive(id, list)
        return list
    }

    private fun predecessorsRecursive(id: Long, predecessorsList: MutableList<Tag>) {
        children(id).forEach { child ->
            predecessorsList.add(child)
            predecessorsRecursive(child.id, predecessorsList)
        }
    }

    /**
     * return a set of top level tags (tags with no parents, this is equivalent to [children] with `null` param
     * @see children
     * */
    fun topLevelTags(): List<Tag>

    /**
     * check if a tag is an ancestor an another tag
     */
    fun isAncestor(ancestorId: Long, successorId: Long): Boolean
    fun isAncestor(ancestor: Tag, successor: Tag): Boolean = isAncestor(ancestor.id, successor.id)
    val tags: Collection<Tag>

    /**
     * get the color of the tag directly or get the color of closest ancestor that pass its color
     */
    fun getColor(id: Long): Color? {
        val tag = get(id)
        if (tag.color != null) return tag.color
        var parent = parent(tag.id)
        while (parent != null) {
            if (parent.passColor && parent.color != null) return parent.color
            else parent = parent(parent.id)
        }
        return null
    }

    companion object {
        operator fun invoke(
            tagsMap: Map<Long, Tag>,
            parentsIds: Map<Long, Long>,
        ): TagsTree = TagsTreeDelegate(tagsMap, parentsIds)
    }
}

data class TagsTreeDelegate(
    private val tagsMap: Map<Long, Tag>,
    private val parentsIds: Map<Long, Long>,
) : TagsTree {
    private val childrenIds: Map<Long, Set<Long>> = buildChildrenMapOfParentMap(parentsIds)
    override val tagsOrderKeys: Map<Long, Int> = buildTagsOrder()


    override val tags: Collection<Tag>
        get() = tagsMap.values

    override fun parent(id: Long): Tag? {
        return tagsMap[parentsIds[id]]
    }

    override operator fun contains(id: Long): Boolean {
        return id in tagsMap
    }

    override fun getOrNull(id: Long): Tag? {
        return tagsMap[id]
    }

    override fun children(id: Long): List<Tag> {
        return childrenIds[id]?.map(::get) ?: emptyList()
    }

    override fun topLevelTags(): List<Tag> {
        return tagsMap.values.filterNot { it.id in parentsIds }
    }

    override fun isAncestor(ancestorId: Long, successorId: Long): Boolean {
        val parentId = parentsIds[successorId] ?: return false
        if (parentId == ancestorId) return true
        return isAncestor(ancestorId, parentId)
    }
}

interface MutableTagsTree : TagsTree {
    fun onUpdate(tag: Tag): Boolean
    fun onInsert(tag: Tag): Boolean
    fun onUpdateOrInsert(tag: Tag): Boolean
    fun onDelete(id: Long, assignChildrenToParent: Boolean = true): Tag?
    fun onDeleteSubtree(id: Long)
    fun setParent(parentId: Long, childId: Long): Boolean
    fun setParent(parentId: Long, tag: Tag): Boolean {
        return setParent(parentId, tag.id)
    }

    fun removeParent(id: Long): Boolean

    companion object {
        operator fun invoke(
            tagsMap: Map<Long, Tag>,
            parentsIds: Map<Long, Long>,
        ): MutableTagsTree = MutableTagsTreeDelegate(tagsMap, parentsIds)
    }

}

class MutableTagsTreeDelegate(
    tagsMap: Map<Long, Tag>,
    parentsIds: Map<Long, Long>,
) : MutableTagsTree {
    val tagsMap: SnapshotStateMap<Long, Tag> = mutableStateMapOf()
    override var tagsOrderKeys: Map<Long, Int> by mutableStateOf(mapOf<Long, Int>())
    val parentsIds: SnapshotStateMap<Long, Long> = mutableStateMapOf()
    private val childrenIds: SnapshotStateMap<Long, Set<Long>> = mutableStateMapOf()

    private fun updateTagsOrderKeys() {
        tagsOrderKeys = buildTagsOrder()
    }
    init {
        setFrom(tagsMap, parentsIds)
        updateTagsOrderKeys()
    }


    fun setFrom(
        parentedTags: Collection<ParentedTag>,
    ) {
        val parentsIds = mutableMapOf<Long, Long>()
        val tagsMap = mutableMapOf<Long, Tag>()
        parentedTags.forEach { tag ->
            tag.parentId?.let { parentId ->
                parentsIds[tag.id] = parentId
            }
            tagsMap[tag.id] = tag
        }
        setFrom(tagsMap, parentsIds)
    }

    fun setFrom(
        tagsMap: Map<Long, Tag>,
        parentsIds: Map<Long, Long>,
    ) {
        this.tagsMap.setAll(tagsMap)
        this.parentsIds.setAll(parentsIds)
        val childrenIds = buildChildrenMapOfParentMap(parentsIds)
        this.childrenIds.putAll(childrenIds)
    }


    override fun onUpdate(tag: Tag): Boolean {
        if (tag.id == INVALID_ID) return false
        if (!tagsMap.containsKey(tag.id)) return false
        tagsMap[tag.id] = tag
        return true
    }

    override fun onInsert(tag: Tag): Boolean {
        if (tag.id == INVALID_ID) return false
        val result = tagsMap.putIfAbsent(tag.id, tag) == null
        updateTagsOrderKeys()
        return result
    }

    override fun onUpdateOrInsert(tag: Tag): Boolean {
        if (tag.id == INVALID_ID) return false
        tagsMap[tag.id] = tag
        updateTagsOrderKeys()
        return true
    }

    override fun onDelete(id: Long, assignChildrenToParent: Boolean): Tag? {
        val prevTag = tagsMap.remove(id)
        // parent id before removing anything
        val parentId = parent(id)?.id
        // remove this tag from parents ids
        parentsIds.remove(id)

        // remove this tag from the children list, so its children has no parent from now
        childrenIds.remove(id)

        parentId?.let { parentId ->
            childrenIds[parentId] = (childrenIds[parentId] ?: emptySet()) - id
        }

        updateTagsOrderKeys()
        return prevTag
    }

    override fun onDeleteSubtree(id: Long) {
        val deletedIds = mutableSetOf(id)
        deletedIds.addAll(predecessors(id).map { it.id })
//        val parent = parent(id)?.id
        deletedIds.forEach { id ->
            parentsIds.remove(id)
            childrenIds.remove(id)
        }

        updateTagsOrderKeys()
    }

    override fun setParent(parentId: Long, childId: Long): Boolean {

        if (tagsMap.contains(parentId) && tagsMap.contains(childId)) {
            parentsIds[childId] = parentId
            childrenIds[parentId] = (childrenIds[parentId] ?: emptySet()) + childId
            updateTagsOrderKeys()
            return true
        }
        return false
    }

    override fun removeParent(id: Long): Boolean {
        val parentId = parentsIds.remove(id) ?: return false
        childrenIds[parentId] = (childrenIds[parentId] ?: emptySet()) - id
        updateTagsOrderKeys()
        return true
    }

    override fun parent(id: Long): Tag? {
        return tagsMap[parentsIds[id]]
    }


    override operator fun contains(id: Long): Boolean {
        return id in tagsMap
    }

    override fun getOrNull(id: Long): Tag? {
        return tagsMap[id]
    }

    override fun children(id: Long): List<Tag> {
        return childrenIds.keys.map(::get)
    }


    override fun topLevelTags(): List<Tag> {
        return tagsMap.values.filterNot { it.id in parentsIds }
    }

    override fun isAncestor(ancestorId: Long, successorId: Long): Boolean {
        val parentId = parentsIds[successorId] ?: return false
        if (ancestorId == parentId) return true
        return isAncestor(ancestorId, parentId)
    }

    override val tags: Collection<Tag>
        get() = tagsMap.values

}

private fun buildChildrenMapOfParentMap(parentsIds: Map<Long, Long>): MutableMap<Long, MutableSet<Long>> {
    val ids = mutableMapOf<Long, MutableSet<Long>>()
    parentsIds.forEach { (child, parent) ->
        ids.getOrPut(parent) { mutableSetOf() }.add(child)
    }
    return ids
}

private fun TagsTree.buildTagsOrder(): MutableMap<Long, Int> {
    var i = 0
    val orderMap = mutableMapOf<Long, Int>()
    topLevelTags().sortedBy { it.label }.forEach { tag ->
        i = insertTagRecursive(tag, i, orderMap)
    }
    return orderMap
}

/**
 * parent before children
 * children before siblings
 * siblings are asc order
 */
private fun TagsTree.insertTagRecursive(tag: Tag, i: Int, order: MutableMap<Long, Int>): Int {
    var mutableI = i
    // parent before children
    order[tag.id] = mutableI
    children(tag.id)
        // siblings are asc order
        .sortedBy { it.label }
        .forEach { child ->
            // children before siblings
            mutableI = insertTagRecursive(child, mutableI, order)
        }
    return mutableI.inc()
}
