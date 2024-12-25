package dev.bayan_ibrahim.my_dictionary.domain.model.tag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap

interface ContextTagsTree {
    val tag: ContextTag?
    val nextLevel: Map<String, ContextTagsTree>

    val isLeaf: Boolean
        get() = nextLevel.isEmpty()
    val isRoot: Boolean
        get() = tag == null

    operator fun get(segment: String): ContextTagsTree? = nextLevel[segment]
    operator fun get(tag: ContextTag): ContextTagsTree? {
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
     * check if any of [nextLevel] contains this tag, this tag must be not [ContextTag.isTopLevel]
     * and [ContextTag.parent] must equal to [ContextTagsTree.tag] of this and finally
     */
    operator fun contains(tag: ContextTag): Boolean {
        val parent = tag.parentOrNull ?: return false
        if (parent != this.tag) return false
        return parent.segments.last() in nextLevel
    }

    /**
     * check if it contains this tag at any level
     */
    fun deepContains(tag: ContextTag): Boolean = get(tag) != null
}

class ContextTagsMutableTree(
    tag: ContextTag? = null,
    nextLevel: Map<String, ContextTagsTree> = mapOf(),
) : ContextTagsTree {
    override var tag: ContextTag? by mutableStateOf(tag)
    override val nextLevel: SnapshotStateMap<String, ContextTagsMutableTree> = mutableStateMapOf<String, ContextTagsMutableTree>().apply {
        this.putAll(nextLevel.map { it.key to ContextTagsMutableTree(it.value.tag, it.value.nextLevel) })
    }

    constructor(tree: ContextTagsTree) : this(tree.tag, tree.nextLevel)

    override operator fun get(segment: String): ContextTagsMutableTree? = nextLevel[segment]
    override operator fun get(tag: ContextTag): ContextTagsMutableTree? = super.get(tag)?.let { it as ContextTagsMutableTree }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContextTagsMutableTree) return false

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
    ): ContextTagsMutableTree {
        return nextLevel.getOrPut(segment) {
            ContextTagsMutableTree(
                tag = ContextTag(
                    parent = this.tag,
                    segment = segment,
                )
            )
        }
    }

    fun addTag(tag: ContextTag) {
        if (tag == this.tag) return
        if (this.tag?.contains(tag) == false) {
            return
        } else {
            val segments = tag.segments.subList(this.tag?.depth ?: 0, tag.depth)
            var deepestSubTree = this
            segments.forEachIndexed { i, segment ->
                deepestSubTree = deepestSubTree.addTag(segment)
            }
            this[tag]?.let {
                it.tag = tag // adding words count and tag id
            }
        }
    }

    fun addTree(tree: ContextTagsTree) {
        tree.asList().forEach(::addTag)
    }

    fun removeTag(segment: String): ContextTagsMutableTree? {
        return nextLevel.remove(segment)
    }

    fun removeTag(tag: ContextTag): ContextTagsMutableTree? {
        val parent = tag.parentOrNull
        return if (parent == null) {
            removeTag(tag.segments.first())
        } else {
            this[parent]?.removeTag(tag.segments.last())
        }
    }

    fun setFrom(tree: ContextTagsTree) {
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

fun Collection<ContextTag>.asTree(): ContextTagsTree {
    val tree = ContextTagsMutableTree()
    this.forEach { tag ->
        tree.addTag(tag)
    }
    tree.refreshWordsCountForNodes()

    return tree
}

fun ContextTagsTree.asList(): List<ContextTag> {
    val result = mutableListOf<ContextTag>()
    asListRecursive(result)
    return result
}

fun ContextTagsTree.asListRecursive(result: MutableList<ContextTag>) {
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
