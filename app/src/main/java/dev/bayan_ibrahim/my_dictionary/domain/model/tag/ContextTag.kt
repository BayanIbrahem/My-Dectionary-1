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
        segments: Iterable<String>,
        id: Long = INVALID_ID,
        wordsCount: Int? = null
    ) : this(
        value = segments.joinToString(ContextTagSegmentSeparator),
        id = id,
        wordsCount = wordsCount,
    )

    constructor(
        vararg segments: String,
        id: Long = INVALID_ID,
        wordsCount: Int? = null
    ) : this(
        value = segments.joinToString(ContextTagSegmentSeparator),
        id = id,
        wordsCount = wordsCount,
    )

    val segments = value.split(ContextTagSegmentSeparator)

    operator fun get(level: Int): String = segments[level]

    val depth: Int
        get() = segments.count()

    /**
     * return parent that contains [level]  segments count, if [level] > [depth] return null
     */
    fun parentAtLevelOrNull(level: Int): ContextTag? {
        return if (level > depth) {
            null
        } else {
            ContextTag(segments.subList(0, level))
        }
    }

    /**
     * return parent that contains [level] segments count, if [level] > [depth] throw an exception
     */
    fun parentAtLevel(level: Int): ContextTag = parentAtLevelOrNull(level)!!

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
}

