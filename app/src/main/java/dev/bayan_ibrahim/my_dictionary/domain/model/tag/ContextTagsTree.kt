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
        var deepestSubTree = this
        repeat(tag.depth) { l -> // level
            deepestSubTree = deepestSubTree[tag[l]] ?: return null
        }
        return deepestSubTree
    }
}

data class ContextTagsMutableTree(
    override var tag: ContextTag? = null,
    override val nextLevel: MutableMap<String, ContextTagsMutableTree> = mutableMapOf(),
) : ContextTagsTree {

    override operator fun get(segment: String): ContextTagsMutableTree? = nextLevel[segment]

    override operator fun get(tag: ContextTag): ContextTagsMutableTree? = super.get(tag)?.let { it as ContextTagsMutableTree }

    fun addTag(tag: ContextTag) {
        var deepestSubTree = this
        repeat(tag.depth) { i -> // level
            val level = i.inc()
            val parent = tag.parentAtLevel(level)
            deepestSubTree = deepestSubTree.nextLevel.getOrPut(tag[i]) {
                ContextTagsMutableTree(parent)
            }
        }
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

class ContextTagsObservableTree(
    tag: ContextTag? = null,
) : ContextTagsTree {
    override var tag: ContextTag? by mutableStateOf(tag)
    override val nextLevel: SnapshotStateMap<String, ContextTagsObservableTree> = mutableStateMapOf()

    override operator fun get(segment: String): ContextTagsObservableTree? = nextLevel[segment]
    override operator fun get(tag: ContextTag): ContextTagsObservableTree? = super.get(tag)?.let { it as ContextTagsObservableTree }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContextTagsObservableTree) return false

        if (tag != other.tag) return false
        if (nextLevel != other.nextLevel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag?.hashCode() ?: 0
        result = 31 * result + nextLevel.hashCode()
        return result
    }

    fun addTag(tag: ContextTag) {
        var deepestSubTree = this
        repeat(tag.depth) { i -> // level
            val level = i.inc()
            val parent = tag.parentAtLevel(level)
            deepestSubTree = deepestSubTree.nextLevel.getOrPut(tag[i]) {
                ContextTagsObservableTree(parent)
            }
        }
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

fun List<ContextTag>.asTree(): ContextTagsTree {
    val tree = ContextTagsMutableTree()
    this.forEach { tag ->
        tree.addTag(tag)
    }
    tree.refreshWordsCountForNodes()

    return tree
}
