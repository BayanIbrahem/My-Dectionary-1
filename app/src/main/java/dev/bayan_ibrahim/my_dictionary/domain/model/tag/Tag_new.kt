package dev.bayan_ibrahim.my_dictionary.domain.model.tag

import androidx.compose.ui.graphics.Color
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID

interface Tag {
    /**
     * this is direct tag color, to get the closest parent color use [TagsTree.getColor]
     */
    val color: Color?
    val id: Long
    val label: String
    val passColor: Boolean
    val isMarkerTag: Boolean
        get() = color != null

    fun onCopy(
        color: Color? = this.color,
        id: Long = this.id,
        label: String = this.label,
        passColor: Boolean = this.passColor,
    ): Delegate = Delegate(
        color = color,
        id = id,
        label = label,
        passColor = passColor
    )

    companion object {
        operator fun invoke(
            color: Color? = null,
            id: Long = INVALID_ID,
            label: String = "",
            passColor: Boolean = true,
        ): Tag = Delegate(
            color = color,
            id = id,
            label = label,
            passColor = passColor
        )
    }

    data class Delegate(
        override val color: Color? = null,
        override val id: Long = INVALID_ID,
        override val label: String = "",
        override val passColor: Boolean = true,
    ) : Tag {
        constructor(tag: Tag) : this(
            color = tag.color,
            id = tag.id,
            label = tag.label,
            passColor = tag.passColor
        )
    }
}

interface ParentedTag : Tag {
    val parentId: Long?

    companion object {
        operator fun invoke(
            color: Color? = null,
            id: Long = INVALID_ID,
            parentId: Long? = null,
            label: String = "",
            passColor: Boolean = true,
        ): ParentedTag = Delegate(
            color = color,
            id = id,
            parentId = parentId,
            label = label,
            passColor = passColor
        )

        operator fun invoke(
            tag: Tag = Delegate(),
            parentId: Long? = null,
        ): ParentedTag = Delegate(
            color = tag.color,
            id = tag.id,
            parentId = parentId,
            label = tag.label,
            passColor = tag.passColor
        )
    }

    data class Delegate(
        override val color: Color? = null,
        override val id: Long = INVALID_ID,
        override val parentId: Long? = null,
        override val label: String = "",
        override val passColor: Boolean = true,
    ) : ParentedTag {
        constructor(tag: ParentedTag) : this(
            color = tag.color,
            id = tag.id,
            parentId = tag.parentId,
            label = tag.label,
            passColor = tag.passColor
        )
    }
}