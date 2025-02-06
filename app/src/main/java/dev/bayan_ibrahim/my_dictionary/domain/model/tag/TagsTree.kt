package dev.bayan_ibrahim.my_dictionary.domain.model.tag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.Color

interface TagsTree {
    val tag: Tag?
    val nextLevel: Map<String, TagsTree>

    val isLeaf: Boolean
        get() = nextLevel.isEmpty()
    val isRoot: Boolean
        get() = tag == null

    operator fun get(segment: String): TagsTree? = nextLevel[segment]
    operator fun get(tag: Tag): TagsTree? {
        if (tag == this.tag) return this
        return if (this.tag?.contains(tag) == false) {
            null
        } else {
            val segments = tag.segments.subList(this.tag?.depth ?: 0, tag.depth)
            var deepestSubTree = this
            segments.forEach { segment ->
                deepestSubTree = deepestSubTree[segment] ?: return null
            }
            deepestSubTree
        }
    }

    /**
     * check if any of [nextLevel] contains this tag, this tag must be not [Tag.isTopLevel]
     * and [Tag.parent] must equal to [TagsTree.tag] of this and finally
     */
    operator fun contains(tag: Tag): Boolean {
        val parent = tag.parentOrNull ?: return false
        if (parent != this.tag) return false
        return parent.segments.last() in nextLevel
    }

    /**
     * check if it contains this tag at any level
     */
    fun deepContains(tag: Tag): Boolean = get(tag) != null
}

class TagsMutableTree(
    tag: Tag? = null,
    nextLevel: Map<String, TagsTree> = mapOf(),
) : TagsTree {
    override var tag: Tag? by mutableStateOf(tag)
    override val nextLevel: SnapshotStateMap<String, TagsMutableTree> = mutableStateMapOf<String, TagsMutableTree>().apply {
        this.putAll(nextLevel.map { it.key to TagsMutableTree(it.value.tag, it.value.nextLevel) })
    }

    constructor(tree: TagsTree) : this(tree.tag, tree.nextLevel)

    override operator fun get(segment: String): TagsMutableTree? = nextLevel[segment]
    override operator fun get(tag: Tag): TagsMutableTree? = super.get(tag)?.let { it as TagsMutableTree }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TagsMutableTree) return false

        if (tag != other.tag) return false
        if (nextLevel != other.nextLevel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag?.hashCode() ?: 0
        result = 31 * result + nextLevel.hashCode()
        return result
    }

    fun addTag(
        segment: String,
    ): TagsMutableTree {
        return nextLevel.getOrPut(segment) {
            TagsMutableTree(
                tag = Tag(
                    segments = (this.tag?.segments ?: emptyList()) + segment,
                )
            )
        }
    }

    fun addTag(tag: Tag) {
        if (tag == this.tag) {
            this.tag = tag // update color
            return
        }
        if (this.tag?.contains(tag) == false) {
            return
        } else {
            val segments = tag.segments.subList(this.tag?.depth ?: 0, tag.depth)
            var deepestSubTree = this
            segments.forEachIndexed { i, segment ->
                deepestSubTree = deepestSubTree.addTag(segment)
            }
            this[tag]?.let {
                it.tag = tag // adding words count and tag id, and color values
            }
        }
    }

    fun updateColors() {
        updateColorsRecursive()
    }

    private fun updateColorsRecursive(inheritableParentColor: Color? = this.tag?.color) {
        if (this.tag?.color == null) {
            this.tag = this.tag?.copy(color = inheritableParentColor, currentColorIsPassed = true, passColorToChildren = true)
        }
        nextLevel.forEach {
            it.value.updateColorsRecursive()
        }
    }

    fun addTree(tree: TagsTree) {
        tree.asList().forEach(::addTag)
    }

    fun removeTag(segment: String): TagsMutableTree? {
        return nextLevel.remove(segment)
    }

    fun removeTag(tag: Tag): TagsMutableTree? {
        val parent = tag.parentOrNull
        return if (parent == null) {
            removeTag(tag.segments.first())
        } else {
            this[parent]?.removeTag(tag.segments.last())
        }
    }

    fun setFrom(tags: Collection<Tag>) {
        this.nextLevel.clear()
        this.tag = null
        tags.forEach(::addTag)
    }

    fun setFrom(tree: TagsTree) {
        this.nextLevel.clear()
        this.tag = tree.tag
        addTree(tree)
    }

    fun refreshWordsCountForNodes() {
        this.calculateTotalWordsCount()
    }

    private fun calculateTotalWordsCount(): Int {
        val t = this.tag
        val newCount = t?.wordsCount ?: let {
            nextLevel.values.sumOf {
                it.calculateTotalWordsCount()
            }
        }

        this.tag = t?.copy(wordsCount = newCount)

        return newCount
    }
}

fun Collection<Tag>.asTree(): TagsTree {
    val tree = TagsMutableTree()
    tree.setFrom(this)

    tree.refreshWordsCountForNodes()
    tree.updateColors()

    return tree
}

fun TagsTree.asList(): List<Tag> {
    val result = mutableListOf<Tag>()

    asListRecursive(result)

    return result
}

fun TagsTree.asListRecursive(result: MutableList<Tag>) {
    if (this.isLeaf) {
        this.tag?.let {
            result.add(it)
        }
    } else {
        this.nextLevel.values.forEach {
            it.asListRecursive(result)
        }
    }
}
