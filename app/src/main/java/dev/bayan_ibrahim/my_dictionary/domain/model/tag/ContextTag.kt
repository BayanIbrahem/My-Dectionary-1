package dev.bayan_ibrahim.my_dictionary.domain.model.tag

import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID

/**
 * separator for tags segments in levels
 */
const val ContextTagSegmentSeparator: String = "/"

data class ContextTag(
    val value: String,
    val id: Long = INVALID_ID,
    val wordsCount: Int? = null,
) {
    constructor(
        parent: ContextTag? = null,
        segment: String,
        id: Long = INVALID_ID,
        wordsCount: Int? = null,
    ) : this(
        segments = (parent?.segments ?: emptyList()) + segment,
        id = id,
        wordsCount = wordsCount,
    )

    constructor(
        segments: Iterable<String>,
        id: Long = INVALID_ID,
        wordsCount: Int? = null,
    ) : this(
        value = segments.joinToString(ContextTagSegmentSeparator),
        id = id,
        wordsCount = wordsCount,
    )

    constructor(
        vararg segments: String,
        id: Long = INVALID_ID,
        wordsCount: Int? = null,
    ) : this(
        value = segments.joinToString(ContextTagSegmentSeparator),
        id = id,
        wordsCount = wordsCount,
    )

    val segments = value.split(ContextTagSegmentSeparator)

    operator fun get(level: Int): String = segments[level]

    init {
        require(value.isNotBlank()) {
            "blank context tag value"
        }
        require(segments.none { it.isBlank() }) {
            "invalid tag segments, it has some blank segments"
        }
    }

    /**
     * check if [other] contains this and it is opposite of [isContained]
     * @see isContained
     */
    fun contains(other: ContextTag): Boolean = other.isContained(this)

    /**
     * check if this in contained in [other], is is similar if we check if [this] is some how a sub tag from [other]
     * return true if the start n segment (where n is [ContextTag.depth] of [other]) from [this] equals
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
    fun isContained(other: ContextTag): Boolean {
        if (this.depth < other.depth) return false

        repeat(other.depth) { l -> // level
            if (segments[l] != other.segments[l]) {
                return false
            }
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContextTag) return false

        if (value != other.value) return false
        if (id != other.id) return false
        if ((wordsCount ?: 0) != (other.wordsCount ?: 0)) return false
        if (segments != other.segments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + (wordsCount ?: 0)
        result = 31 * result + segments.hashCode()
        return result
    }
}

val ContextTag.depth: Int
    get() = segments.count()

val ContextTag.isTopLevel
    get() = depth == 1

val ContextTag.parentOrNull: ContextTag?
    get() = if (isTopLevel) null else parentAtLevelOrNull(depth.dec())

val ContextTag.parent: ContextTag
    get() = parentOrNull ?: throw IllegalArgumentException("Top level tag has no parent")

/**
 * return parent that contains [level]  segments count, if [level] > [depth] return null
 */
fun ContextTag.parentAtLevelOrNull(level: Int): ContextTag? {
    return if (level > depth) {
        null
    } else {
        ContextTag(segments.subList(0, level))
    }
}

/**
 * return parent that contains [level] segments count, if [level] > [depth] throw an exception
 */
fun ContextTag.parentAtLevel(level: Int): ContextTag = parentAtLevelOrNull(level)!!
