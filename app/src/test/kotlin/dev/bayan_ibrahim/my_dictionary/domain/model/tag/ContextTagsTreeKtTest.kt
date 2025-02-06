package dev.bayan_ibrahim.my_dictionary.domain.model.tag

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TagsTreeKtTest {
    private lateinit var tagsList: List<Tag>
    private lateinit var tagsTree: TagsMutableTree

    @Before
    fun setup() {
        tagsList = listOf(
            Tag("object", "food", "fruit"),
            Tag("object", "food", "vegetables"),
            Tag("object", "food", "healthy"),
            Tag("object", "device"),
            Tag("language", "en"),
        )
        tagsTree = TagsMutableTree().apply {
            nextLevel["object"] = TagsMutableTree(Tag("object")).apply {
                nextLevel["food"] = TagsMutableTree(Tag("object", "food")).apply {
                    nextLevel["fruit"] = TagsMutableTree(Tag("object", "food", "fruit"))
                    nextLevel["vegetables"] = TagsMutableTree(Tag("object", "food", "vegetables"))
                    nextLevel["healthy"] = TagsMutableTree(Tag("object", "food", "healthy"))
                }
                nextLevel["device"] = TagsMutableTree(Tag("object", "device"))
            }
            nextLevel["language"] = TagsMutableTree(Tag("language")).apply {
                nextLevel["en"] = TagsMutableTree(Tag("language", "en"))
            }
        }
    }

    @Test
    fun `asTree-emptyList-emptyTree`() {
        val tagsList: List<Tag> = emptyList()
        val tree = tagsList.asTree()
        assertNull(tree.tag)
        assertTrue(tree.nextLevel.isEmpty())
    }

    @Test
    fun `asTree-tagsList-treeOfTags`() {
        val actualTree = tagsList.asTree()
        assertEquals(
            actualTree,
            tagsTree,
        )
    }

    @Test
    fun `getSubTree-existedDirectSubTree-returnSubTree`() {
        val actualSubTree = tagsTree["object"]
        val expectedSubTree = TagsMutableTree(Tag("object")).apply {
            nextLevel["food"] = TagsMutableTree(Tag("object", "food")).apply {
                nextLevel["fruit"] = TagsMutableTree(Tag("object", "food", "fruit"))
                nextLevel["vegetables"] = TagsMutableTree(Tag("object", "food", "vegetables"))
                nextLevel["healthy"] = TagsMutableTree(Tag("object", "food", "healthy"))
            }
            nextLevel["device"] = TagsMutableTree(Tag("object", "device"))
        }
        assertEquals(expectedSubTree, actualSubTree)
    }


    @Test
    fun `getSubTree-currentTree-returnSameTree`() {
        val tree=tagsTree["object"]!!
        val actualSubTree = tagsTree[Tag("object")]
        assertEquals(tree, actualSubTree)
    }
    @Test
    fun `getSubTree-nonExistedDirectSubTree-returNull`() {
        val actualSubTree = tagsTree["not an existed value"]
        assertNull(actualSubTree)
    }

    @Test
    fun `getSubTree-existedDeepSubTree-returnSubTree`() {
        val actualSubTree = tagsTree[
            Tag("object", "food")
        ]
        val expectedSubTree = TagsMutableTree(Tag("object", "food")).apply {
            nextLevel["fruit"] = TagsMutableTree(Tag("object", "food", "fruit"))
            nextLevel["vegetables"] = TagsMutableTree(Tag("object", "food", "vegetables"))
            nextLevel["healthy"] = TagsMutableTree(Tag("object", "food", "healthy"))
        }
        assertEquals(expectedSubTree, actualSubTree)
    }

    @Test
    fun `getSubTree-nonExistedDeepSubTree-returnSubTree`() {
        val actualSubTree = tagsTree[
            Tag("object", "non existed")
        ]
        assertNull(actualSubTree)
    }
}