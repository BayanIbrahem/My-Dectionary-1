package dev.bayan_ibrahim.my_dictionary.domain.model.tag

import androidx.compose.ui.graphics.Color
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID

/**
 * separator for tags segments in levels
 */
const val TagSegmentSeparator: String = "/"

data class Tag(
    val value: String,
    val id: Long = INVALID_ID,
    val wordsCount: Int? = null,
    val color: Color? = null,
    /**
     * if true then its color would be passed to any children
     */
    val passColorToChildren: Boolean = true,
    /**
     * if the current value is its parent color or its own color
     */
    val currentColorIsPassed: Boolean = true,
) {
    /**
     * return [color] if it is not passed [currentColorIsPassed]
     */
    val originalColor: Color?
        get() = color?.let {
            if (currentColorIsPassed) {
                null
            } else {
                it
            }
        }

    constructor(
        segments: Iterable<String>,
        id: Long = INVALID_ID,
        wordsCount: Int? = null,
        markerColor: Color? = null,
        /**
         * if true then its color would be passed to any children
         */
        passColorToChildren: Boolean = true,
        /**
         * if the current value is its parent color or its own color
         */
        currentColorIsPassed: Boolean = true,
    ) : this(
        value = segments.joinToString(TagSegmentSeparator),
        id = id,
        wordsCount = wordsCount,
        color = markerColor,
        passColorToChildren = passColorToChildren,
        currentColorIsPassed = currentColorIsPassed
    )

    constructor(
        vararg segments: String,
        id: Long = INVALID_ID,
        wordsCount: Int? = null,
    ) : this(
        value = segments.joinToString(TagSegmentSeparator),
        id = id,
        wordsCount = wordsCount,
    )

    val segments = value.split(TagSegmentSeparator)

    operator fun get(level: Int): String = segments[level]

    init {
        require(value.isNotBlank()) {
            "blank context tag value"
        }
        require(segments.none { it.isBlank() }) {
            "invalid tag segments, it has some blank segments"
        }
    }

    companion object
}

/**
 * return true if any of [targetTags] [isContained] in any of receiver
 * @see anyContainedInAny
 */
fun Iterable<Tag>.anyContainsAny(targetTags: Iterable<Tag>): Boolean = targetTags.anyContainedInAny(this)

/**
 * return true if any of receiver [isContained] in any of [targetTags]
 * @see [anyContainsAny]
 */
fun Iterable<Tag>.anyContainedInAny(targetTags: Iterable<Tag>): Boolean {
    return this.any { source ->
        targetTags.any { target ->
            source.isContained(target)
        }
    }
}

/**
 * check if this in contained in [other], is is similar if we check if [this] is some how a sub tag from [other]
 * return true if the start n segment (where n is [Tag.depth] of [other]) from [this] equals
 * first n segment from the other tag
 *
 * ```kotlin
 * val t1 = "object/food"
 * val t2 = "object/food/fruit"
 * val t3 = "object"
 * val t4 = "object/device"
 * t1.isContained(t2) // false
 * t2.isContained(t1) // true
 * t4.isContained(t3) // true
 * t4.isContained(t1) // false
 * ```
 * @see contains
 */
fun Tag.isContained(other: Tag): Boolean {
    if (this.depth < other.depth) return false

    repeat(other.depth) { l -> // level
        if (segments[l] != other.segments[l]) {
            return false
        }
    }
    return true
}

private const val TAG_SEPARATOR = "|"

/**
 * generate a simple string from id and tag id and tag value
 * notice that this method drop all params like color and wordsCount and keep only [id], and [value]
 * */
fun Tag.simpleSerialize(separator: String = TAG_SEPARATOR): String = "$id$separator$value"
fun Tag.Companion.simpleString(value: String, separator: String = TAG_SEPARATOR): Tag {
    val (strId, v) = value.split(separator)
    return Tag(id = strId.toLong(), value = v)
}

/**
 * check if [other] contains this and it is opposite of [isContained]
 * @see isContained
 */
fun Tag.contains(other: Tag): Boolean = other.isContained(this)

val Tag.depth: Int
    get() = segments.count()

val Tag.isTopLevel
    get() = depth == 1

val Tag.parentOrNull: Tag?
    get() = if (isTopLevel) null else parentAtLevelOrNull(depth.dec())

val Tag.parent: Tag
    get() = parentOrNull ?: throw IllegalArgumentException("Top level tag has no parent")

/**
 * return parent that contains [level]  segments count, if [level] > [depth] return null
 */
fun Tag.parentAtLevelOrNull(level: Int): Tag? {
    return if (level > depth) {
        null
    } else {
        Tag(segments.subList(0, level))
    }
}

/**
 * return parent that contains [level] segments count, if [level] > [depth] throw an exception
 */
fun Tag.parentAtLevel(level: Int): Tag = parentAtLevelOrNull(level)!!

/**
 * return true if current tag has a color its own
 */
val Tag.isMarkerTag: Boolean
    get() = color != null && !currentColorIsPassed


/**
 * Comparator for `Tag` objects to determine their ordering based on inheritance and path.
 *
 * In general it compare tas as it is an inheritance tree (like family tree when we need to compare which is older 🙂)
 *
 * Comparison rules:
 * 1. `null` is considered the smallest value (both null returns 0).
 * 2. A child tag is greater than its parent tag.
 * 3. If neither is a parent of the other:
 *    - The tag with the shorter path is smaller.
 *    - If paths are of equal length, comparison is based on the lexicographic order of their segments.
 */
object InheritedTagsComparable : Comparator<Tag> {
    override fun compare(t1: Tag?, t2: Tag?): Int {
        // Null checks
        if (t1 == null) return if (t2 == null) 0 else -1
        if (t2 == null) return 1

        // Parent-child relationship
        when {
            t1.contains(t2) -> return -1
            t2.contains(t1) -> return 1
        }

        // Path length comparison
        val depthComparison = t1.depth.compareTo(t2.depth)
        if (depthComparison != 0) return depthComparison

        // Lexicographic comparison of segments
        for (i in 0 until t1.depth) {
            val segmentComparison = t1[i].compareTo(t2[i])
            if (segmentComparison != 0) return segmentComparison
        }

        return 0
    }
}

/**
 * return a copy of the context that with validated segment text
 */
fun Tag.validate(): Tag = Tag(
    segments = segments.map { it.trim() },
    id = id,
    wordsCount = wordsCount,
    markerColor = this.color,
    passColorToChildren = this.passColorToChildren,
    currentColorIsPassed= this.currentColorIsPassed,
)
